package com.leita.leita.study.repository

import com.leita.leita.study.domain.AssignmentRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

import org.springframework.data.jpa.repository.Query

@Repository
interface AssignmentRecordRepository : JpaRepository<AssignmentRecord, Long> {
    fun findByAssignmentIdAndUserId(assignmentId: Long, userId: Long): AssignmentRecord?
    
    @Query("SELECT ar FROM AssignmentRecord ar JOIN ar.assignment a JOIN a.problemIds p WHERE ar.user.id = :userId AND p = :problemId")
    fun findByUserIdAndProblemId(userId: Long, problemId: Long): List<AssignmentRecord>
}
