package com.leita.leita.controller.dto.auth

import com.leita.leita.controller.dto.problem.response.StudyClassesResponse
import com.leita.leita.controller.dto.studyClass.response.StudyClassCreateResponse
import com.leita.leita.controller.dto.studyClass.response.StudyClassDetailResponse
import com.leita.leita.controller.dto.studyClass.response.StudyClassPendingResponse
import com.leita.leita.domain.study.StudyClass
import com.leita.leita.domain.User
import org.springframework.data.domain.Page

class StudyClassMapper {
    companion object {
        fun toStudyDetailResponse(study: StudyClass): StudyClassDetailResponse {
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

        fun toStudiesResponse(studies: Page<StudyClass>): StudyClassesResponse {
            return StudyClassesResponse(
                content = studies.content.map { toStudyDetailResponse(it) },
                currentPage = studies.number,
                totalPages = studies.totalPages,
                totalElements = studies.totalElements,
                size = studies.size
            )
        }

        fun toStudyCreateResponse(id: Long): StudyClassCreateResponse {
            return StudyClassCreateResponse(id)
        }

        fun toStudyPendingResponse(pendings: Page<User>): StudyClassPendingResponse {
            return StudyClassPendingResponse(
                content = pendings.content.map { it },
                currentPage = pendings.number,
                totalPages = pendings.totalPages,
                totalElements = pendings.totalElements,
                size = pendings.size
            )
        }
    }
}