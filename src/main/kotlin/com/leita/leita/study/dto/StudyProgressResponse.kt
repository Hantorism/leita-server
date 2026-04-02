package com.leita.leita.study.dto

import com.leita.leita.user.domain.User

data class StudyProgressDetailResponse(
    val studyId: Long,
    val title: String,
    val members: List<StudyProgressResponse>,
    val completedMembers: List<User>,
    val inProgressMembers: List<User>
)

data class StudyProgressResponse(
    val userId: Long,
    val userName: String,
    val userEmail: String,
    val attendanceStatus: UserAttendanceStatus,
    val assignmentStatus: UserAssignmentStatus,
    val isCompleted: Boolean
)

data class UserAttendanceStatus(
    val totalSessions: Int,
    val attendedCount: Int,
    val presentCount: Int,
    val lateCount: Int,
    val absentCount: Int,
    val attendancePercentage: Double
)

data class UserAssignmentStatus(
    val totalAssignments: Int,
    val completedCount: Int,
    val assignmentPercentage: Double
)


