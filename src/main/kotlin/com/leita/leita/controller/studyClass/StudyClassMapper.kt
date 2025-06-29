package com.leita.leita.controller.dto.auth

import com.leita.leita.controller.studyClass.response.StudyClassCreateResponse
import com.leita.leita.controller.studyClass.response.StudyClassDetailResponse
import com.leita.leita.controller.studyClass.response.StudyClassesResponse
import com.leita.leita.domain.study.StudyClass
import org.springframework.data.domain.Page

class StudyClassMapper {
    companion object {
        fun toStudyClassDetailResponse(study: StudyClass): StudyClassDetailResponse {
            return StudyClassDetailResponse(
                id = study.id,
                title = study.title,
                description = study.description,
                requirement = study.requirement,
                members = study.members,
                admins = study.admins,
                pendings = study.pendings,
            )
        }

        fun toStudyClassesResponse(studies: Page<StudyClass>): StudyClassesResponse {
            return StudyClassesResponse(
                content = studies.content.map { toStudyClassDetailResponse(it) },
                currentPage = studies.number,
                totalPages = studies.totalPages,
                totalElements = studies.totalElements,
                size = studies.size
            )
        }

        fun toStudyCreateResponse(id: Long): StudyClassCreateResponse {
            return StudyClassCreateResponse(id)
        }
    }
}