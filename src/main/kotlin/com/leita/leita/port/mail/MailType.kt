package com.leita.leita.port.mail

enum class MailType(
    val title: String,
    private val template: String
) {
    // TODO: 템플릿 필요
    STUDY_MEMBER_JOIN(
        title = "[LEITA] 스터디 구성원 참여 신청",
        template = "스터디 구성원 참여 신청이 도착했습니다."
    ),
    STUDY_ADMIN_INVITE(
        title = "[LEITA] 스터디 관리자 초대",
        template = "스터디 관리자 초대 제안이 도착했습니다."
    ),;

    fun formatMessage(vararg args: String): String {
        return String.format(template, *args)
    }
}