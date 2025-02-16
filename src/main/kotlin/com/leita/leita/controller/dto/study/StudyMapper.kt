package com.leita.leita.controller.dto.auth

import com.leita.leita.controller.dto.problem.response.StudiesResponse
import com.leita.leita.controller.dto.study.response.StudyCreateResponse
import com.leita.leita.controller.dto.study.response.StudyDetailResponse
import com.leita.leita.domain.Study
import org.springframework.data.domain.Page

class StudyMapper {
    companion object {
        fun toStudyDetailResponse(study: Study): StudyDetailResponse {
            return StudyDetailResponse(
                admins = study.admins,
                title = study.title,
                description = study.description,
                members = study.members
            )
        }

        fun toStudiesResponse(studies: Page<Study>): StudiesResponse {
            return StudiesResponse(
                content = studies.content.map { toStudyDetailResponse(it) },
                currentPage = studies.number,
                totalPages = studies.totalPages,
                totalElements = studies.totalElements,
                size = studies.size
            )
        }

        fun toStudyCreateResponse(id: Long): StudyCreateResponse {
            return StudyCreateResponse(id)
        }
    }
}