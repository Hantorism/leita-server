package com.leita.leita.study.service

import com.leita.leita.judge.event.ProblemJudgedEvent
import com.leita.leita.judge.repository.JudgeRepository
import com.leita.leita.study.domain.AssignmentStatus
import com.leita.leita.study.repository.AssignmentRecordRepository
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AssignmentRecordUpdater(
    private val assignmentRecordRepository: AssignmentRecordRepository,
    private val judgeRepository: JudgeRepository
) {

    @EventListener
    @Transactional
    fun handleProblemJudgedEvent(event: ProblemJudgedEvent) {
        val records = assignmentRecordRepository.findByUserIdAndProblemId(event.userId, event.problemId)
        
        records.forEach { record ->
            val problemIds = record.assignment.problemIds
            if (problemIds.isEmpty()) return@forEach
            
            val correctJudges = judgeRepository.findByProblemIdInAndUserIdAndResult(
                problemIds, 
                event.userId, 
                com.leita.leita.judge.domain.Result.CORRECT
            )
            val correctProblemIds = correctJudges.map { it.problemId }.distinct()

            val submitJudges = judgeRepository.findByProblemIdInAndUserIdAndType(
                problemIds, 
                event.userId, 
                com.leita.leita.judge.domain.JudgeType.SUBMIT
            )
            val submittedProblemIds = submitJudges.map { it.problemId }.distinct()

            val status = when {
                correctProblemIds.size == problemIds.size -> AssignmentStatus.COMPLETED
                submittedProblemIds.size == problemIds.size -> AssignmentStatus.PARTIAL
                else -> AssignmentStatus.INCOMPLETE
            }
            
            record.updateStatus(status)
            assignmentRecordRepository.save(record)
        }
    }
}
