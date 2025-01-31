package com.leita.leita.controller.dto.auth

import com.leita.leita.controller.dto.study.response.StudyCreateResponse
import com.leita.leita.controller.dto.study.response.StudyDetailResponse
import com.leita.leita.domain.Study

class StudyMapper {
    companion object {
        fun toStudyDetailResponse(study: Study): StudyDetailResponse {
            return StudyDetailResponse(
                admins = study.admins,
                title = study.title,
                description = study.description,
                participants = study.participants
            )
        }

        fun toStudyCreateResponse(id: Long): StudyCreateResponse {
            return StudyCreateResponse(id)
        }
    }
}