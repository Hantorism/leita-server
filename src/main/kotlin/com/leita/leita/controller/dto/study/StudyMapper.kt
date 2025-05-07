package com.leita.leita.controller.dto.study

import com.leita.leita.controller.dto.study.response.StudiesResponse
import com.leita.leita.controller.dto.study.response.StudyCreateResponse
import com.leita.leita.controller.dto.study.response.StudyDetailResponse
import com.leita.leita.domain.study.Study
import org.springframework.data.domain.Page

class StudyMapper {
    companion object {
        fun toStudyDetailResponse(study: Study): StudyDetailResponse {
            return StudyDetailResponse(
                id = study.id,
                participants = study.participants,
                startDateTime = study.startDateTime,
                endDateTime = study.endDateTime,
                studyClassId = study.studyClassId,
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