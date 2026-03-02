package com.leita.leita.repository

import com.leita.leita.domain.study.StudyMember
import com.leita.leita.domain.study.StudyMemberRole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface StudyMemberRepository : JpaRepository<StudyMember, Long> {
    fun findByStudyIdAndUserId(studyId: Long, userId: Long): StudyMember?

    fun findByStudyId(studyId: Long): List<StudyMember>

    fun findByStudyIdAndRole(studyId: Long, role: StudyMemberRole): List<StudyMember>

    @EntityGraph(attributePaths = ["user"])
    fun findByStudyIdAndRoleOrderByJoinedAtDesc(
        studyId: Long,
        role: StudyMemberRole,
        pageable: Pageable
    ): Page<StudyMember>

    @Query("SELECT sm FROM StudyMember sm WHERE sm.study.id = :studyId ORDER BY sm.role, sm.joinedAt DESC")
    fun findAllByStudyIdOrdered(studyId: Long): List<StudyMember>
}
