package com.leita.leita.problem.migration

import com.leita.leita.file.util.OracleStorageUtil
import com.leita.leita.problem.domain.Description
import com.leita.leita.problem.repository.ProblemRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(
    properties = [
        "spring.config.import=optional:file:.env[.properties]",
        "oci.region=\${OCI_REGION:ap-chuncheon-1}",
        "oci.bucketName=\${OCI_BUCKET_NAME:bucket}",
        "oci.namespace=\${OCI_NAMESPACE:namespace}"
    ]
)
@ActiveProfiles("dev")
class ProblemMigrationTest(
    @Autowired private val problemRepository: ProblemRepository,
    @Autowired private val oracleStorageUtil: OracleStorageUtil,
    @Autowired private val judgeRepository: com.leita.leita.judge.repository.JudgeRepository,
    @Autowired private val studySessionRepository: com.leita.leita.study.repository.StudySessionRepository
) {

    @Test
    @Transactional
    @Rollback(false)
    fun migrateDescriptionsAndTestCasesToOCI() {
        val problems = problemRepository.findAll()
        var count = 0

        for (problem in problems) {
            val oldId = problem.problemId
            val newId = try {
                if (oldId.length == 4 && oldId.all { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }) oldId.lowercase()
                else String.format("%04x", oldId.toLong())
            } catch (e: Exception) { oldId }

            println("Migrating: $oldId -> $newId")
            val basePath = "problems/$newId"

            try {
                // 1. 원본 텍스트 추출 (URL이면 다운로드, 텍스트면 유지)
                fun getRaw(v: String) = if (v.startsWith("http")) {
                    try { oracleStorageUtil.readString(oracleStorageUtil.extractObjectName(v)) } catch (e: Exception) { v }
                } else v

                // 2. 새로운 경로로 업로드
                val pUrl = oracleStorageUtil.uploadString("$basePath/descriptions/problem.html", getRaw(problem.description.problem))
                val iUrl = oracleStorageUtil.uploadString("$basePath/descriptions/input.html", getRaw(problem.description.input))
                val oUrl = oracleStorageUtil.uploadString("$basePath/descriptions/output.html", getRaw(problem.description.output))

                // 3. 테스트케이스 마이그레이션 (리스트 복사본 생성하여 clear() 부작용 방지)
                val migratedTestCases = problem.testCases.mapIndexed { idx, tc ->
                    tc.input = oracleStorageUtil.uploadString("$basePath/testcases/$idx.in", getRaw(tc.input))
                    tc.output = oracleStorageUtil.uploadString("$basePath/testcases/$idx.out", getRaw(tc.output))
                    tc
                }.toList()

                // 4. ProblemId 강제 업데이트 (Reflection)
                val idField = problem.javaClass.getDeclaredField("problemId")
                idField.isAccessible = true
                idField.set(problem, newId)

                // 5. 엔티티 정보 갱신
                problem.update(
                    problem.title,
                    Description.create(pUrl, iUrl, oUrl),
                    problem.limit,
                    migratedTestCases,
                    problem.source,
                    problem.category
                )

                problemRepository.save(problem)

                // 6. 타 테이블 ID 동기화
                if (oldId != newId) {
                    syncExternalIds(oldId, newId)
                }

                count++
                println("Success: $newId")
            } catch (e: Exception) {
                println("Fail $oldId: ${e.message}")
            }
        }
        println("\n✅ 마이그레이션 완료: 총 ${count}개")
    }

    private fun syncExternalIds(oldId: String, newId: String) {
        // Judge 업데이트
        val judges = judgeRepository.findAllByProblemIdAndType(oldId, com.leita.leita.judge.domain.JudgeType.SUBMIT) +
                     judgeRepository.findAllByProblemIdAndType(oldId, com.leita.leita.judge.domain.JudgeType.RUN)
        judges.forEach { j ->
            val f = j.javaClass.getDeclaredField("problemId")
            f.isAccessible = true
            f.set(j, newId)
            judgeRepository.save(j)
        }
        // Assignment 업데이트
        studySessionRepository.findAll().forEach { s ->
            var changed = false
            s.assignments.forEach { a ->
                if (a.problemIds.contains(oldId)) {
                    a.problemIds.remove(oldId)
                    a.problemIds.add(newId)
                    changed = true
                }
            }
            if (changed) studySessionRepository.save(s)
        }
    }
}
