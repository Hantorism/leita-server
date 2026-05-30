package com.leita.leita.problem.migration

import com.leita.leita.file.util.OracleStorageUtil
import com.leita.leita.problem.domain.Description
import com.leita.leita.problem.repository.ProblemRepository
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Commit
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
@Disabled("마이그레이션 테스트는 수동으로만 실행합니다.")
class ProblemMigrationTest(
    @Autowired private val problemRepository: ProblemRepository,
    @Autowired private val oracleStorageUtil: OracleStorageUtil,
    @Autowired private val entityManager: EntityManager
) {

    @Test
    @Transactional
    @Rollback(false)
    @Commit
    fun migrateDescriptionsAndTestCasesToOCI() {
        // 모든 문제를 가져옵니다. 페치 조인을 사용하지 않아도 @Transactional 내에서는 레이지 로딩이 가능합니다.
        val problems = problemRepository.findAll()
        var count = 0

        println("\n=== [마이그레이션 시작: 총 ${problems.size}개] ===")

        for (problem in problems) {
            val oldId = problem.problemId
            val newId = try {
                if (oldId.length == 4 && oldId.all { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }) oldId.lowercase()
                else String.format("%04x", oldId.toLong())
            } catch (e: Exception) { oldId }

            // 1. 카테고리 데이터 유실 방지를 위해 명시적으로 로드 및 복사
            val currentCategories = problem.category.toList()
            
            // 2. 테스트케이스 데이터 유실 방지를 위해 리스트 복사
            val currentTestCases = problem.testCases.toList()

            println("Processing Problem: ID=${problem.id}, Code=$oldId -> $newId | Categories: $currentCategories")
            val basePath = "problems/$newId"

            try {
                // 3. 원본 텍스트 추출 (URL이면 다운로드, 텍스트면 유지)
                fun getRaw(v: String) = if (v.startsWith("http")) {
                    try { oracleStorageUtil.readString(oracleStorageUtil.extractObjectName(v)) } catch (e: Exception) { v }
                } else v

                // 4. 새로운 경로로 업로드
                val pUrl = oracleStorageUtil.uploadString("$basePath/descriptions/problem.html", getRaw(problem.description.problem))
                val iUrl = oracleStorageUtil.uploadString("$basePath/descriptions/input.html", getRaw(problem.description.input))
                val oUrl = oracleStorageUtil.uploadString("$basePath/descriptions/output.html", getRaw(problem.description.output))

                // 5. 테스트케이스 마이그레이션
                currentTestCases.forEachIndexed { idx, tc ->
                    val rawIn = getRaw(tc.input)
                    val rawOut = getRaw(tc.output)
                    tc.input = oracleStorageUtil.uploadString("$basePath/testcases/$idx.in", rawIn)
                    tc.output = oracleStorageUtil.uploadString("$basePath/testcases/$idx.out", rawOut)
                }

                // 6. ProblemId 강제 업데이트 (Reflection)
                val idField = problem.javaClass.getDeclaredField("problemId")
                idField.isAccessible = true
                idField.set(problem, newId)

                // 7. 엔티티 정보 갱신 (리스트 복사본을 넘겨서 내부 clear() 동작으로부터 안전하게 보호)
                problem.update(
                    problem.title,
                    Description.create(pUrl, iUrl, oUrl),
                    problem.limit,
                    currentTestCases,
                    problem.source,
                    currentCategories
                )

                problemRepository.saveAndFlush(problem)

                // 8. 타 테이블 ID 동기화 (Native Query 사용으로 확실하게 반영)
                if (oldId != newId) {
                    syncExternalIdsDirectly(oldId, newId)
                }

                count++
                println(" [OK] Success: $newId")
            } catch (e: Exception) {
                println(" [FAIL] $oldId: ${e.message}")
                e.printStackTrace()
            }
        }
        println("\n=== [마이그레이션 완료: 총 ${count}개] ===\n")
    }

    private fun syncExternalIdsDirectly(oldId: String, newId: String) {
        // Judge 테이블 업데이트
        val judgeUpdateCount = entityManager.createNativeQuery("UPDATE judge SET problem_id = :newId WHERE problem_id = :oldId")
            .setParameter("newId", newId)
            .setParameter("oldId", oldId)
            .executeUpdate()
        if (judgeUpdateCount > 0) println("    -> Updated $judgeUpdateCount judge records")

        // Assignment Problem 테이블 업데이트
        try {
            val asgUpdateCount = entityManager.createNativeQuery("UPDATE assignment_problem SET problem_id = :newId WHERE problem_id = :oldId")
                .setParameter("newId", newId)
                .setParameter("oldId", oldId)
                .executeUpdate()
            if (asgUpdateCount > 0) println("    -> Updated $asgUpdateCount assignment problem entries")
        } catch (e: Exception) {
            // 테이블이 없는 경우(과제가 아직 한 번도 생성 안 된 경우) 무시
        }
            
        entityManager.flush()
        entityManager.clear()
    }
}
