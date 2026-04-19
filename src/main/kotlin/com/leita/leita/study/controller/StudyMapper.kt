package com.leita.leita.study.controller

import com.leita.leita.study.dto.StudiesResponse
import com.leita.leita.study.dto.StudyCreateResponse
import com.leita.leita.study.dto.StudyDetailResponse
import com.leita.leita.study.dto.StudyMemberResponse
import com.leita.leita.study.dto.StudyResponse
import com.leita.leita.study.domain.Study
import com.leita.leita.study.domain.StudyMemberRole
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

        fun toStudyResponse(study: Study, isJoined: Boolean = false): StudyResponse {
            val activeMemberCount = try {
                study.studyMembers.count { it.role == StudyMemberRole.ADMIN || it.role == StudyMemberRole.MEMBER }
            } catch (e: Exception) {
                0
            }
            return StudyResponse(
                id = study.id,
                title = study.title,
                description = study.description,
                requirement = study.requirement,
                startDate = study.startDate,
                endDate = study.endDate,
                isJoined = isJoined,
                memberCount = activeMemberCount
            )
        }

        fun toStudiesResponse(studies: Page<Study>, joinedStudyIds: Set<Long> = emptySet()): StudiesResponse {
            return StudiesResponse(
                content = studies.content.map { toStudyResponse(it, joinedStudyIds.contains(it.id)) },
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
