package com.leita.leita.study.dto

import com.leita.leita.study.domain.AttendanceRecordStatus

data class MemberAttendanceUpdateRequest(
    val status: AttendanceRecordStatus
)
