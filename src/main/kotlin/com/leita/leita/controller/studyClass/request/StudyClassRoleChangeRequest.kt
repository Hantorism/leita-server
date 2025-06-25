package com.leita.leita.controller.studyClass.request

import com.leita.leita.domain.study.StudyRole

class StudyClassRoleChangeRequest(
    val email: String,
    val newRole: StudyRole
)
