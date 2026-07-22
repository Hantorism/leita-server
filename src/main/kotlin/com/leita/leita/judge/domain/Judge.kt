package com.leita.leita.judge.domain

import com.leita.leita.user.domain.User
import com.leita.leita.judge.dto.JudgeWCResponse
import com.leita.leita.common.domain.BaseEntity
import jakarta.annotation.Nullable
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "judge")
@Access(AccessType.FIELD)
open class Judge(

    @Column(nullable = false)
    open val problemId: String,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    open val user: User,

    @Nullable
    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 50)
    open var result: Result? = null,

    @Nullable
    @Embedded
    @Column(nullable = true)
    open var used: UsedInfo? = null,

    @Nullable
    @Column(nullable = true)
    open var sizeOfCode: Long? = null,

    @Nullable
    @Column(nullable = true)
    open var codeUrl: String? = null,

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    open val type: JudgeType
) : BaseEntity() {

    companion object {
        fun create(
            problemId: String,
            user: User,
            language: Language,
            type: JudgeType
        ): Judge {
            return Judge(
                problemId,
                user,
                used = UsedInfo(
                    memory = 0,
                    time = 0,
                    language,
                ),
                type = type
            )
        }
    }

    fun updateSizeOfCode(code: String) {
        this.sizeOfCode = Base64.getDecoder().decode(code).size.toLong()
    }

    fun updateCodeUrl(codeUrl: String) {
        this.codeUrl = codeUrl
    }

    fun updateSubmitInfo(response: JudgeWCResponse) {
        this.result = response.result;
        this.used = used?.let {
            UsedInfo(
                memory = response.usedMemory,
                time = response.usedTime,
                language = it.language
            )
        };
    }
}