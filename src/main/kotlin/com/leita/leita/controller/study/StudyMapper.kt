package com.leita.leita.controller.study

import com.leita.leita.controller.study.response.StudiesResponse
import com.leita.leita.controller.study.response.StudyCreateResponse
import com.leita.leita.controller.study.response.StudyDetailResponse
import com.leita.leita.controller.study.response.StudyMemberResponse
import com.leita.leita.controller.study.response.StudyResponse
import com.leita.leita.domain.study.Study
import org.springframework.data.domain.Page

class StudyMapper {
    companion object {
        fun toStudyDetailResponse(study: Study): StudyDetailResponse {
            return StudyDetailResponse(
                id = study.id,
                title = study.title,
                description = study.description,
                requirement = study.requirement,
                startDate = study.startDate,
                endDate = study.endDate,
                members = study.studyMembers.map {
                    StudyMemberResponse(
                        userId = it.user.id,
                        name = it.user.name,
                        email = it.user.email,
                        role = it.role,
                        joinedAt = it.joinedAt,
                        approvedAt = it.approvedAt
                    )
                }
            )
        }

        fun toStudyResponse(study: Study): StudyResponse {
            return StudyResponse(
                id = study.id,
                title = study.title,
                description = study.description,
                requirement = study.requirement,
                startDate = study.startDate,
                endDate = study.endDate,
            )
        }

        fun toStudiesResponse(studies: Page<Study>): StudiesResponse {
            return StudiesResponse(
                content = studies.content.map { toStudyResponse(it) },
                currentPage = studies.number,
                totalPages = studies.totalPages,
                totalElements = studies.totalElements,
                size = studies.size
            )
        }

        fun toStudyCreateResponse(study: Study): StudyCreateResponse {
            return StudyCreateResponse(
                id = study.id ?: throw IllegalStateException("Study ID must not be null after persistence"),
                title = study.title
            )
        }
    }
}
