package com.leita.leita.domain.judge

import com.leita.leita.domain.User
import com.leita.leita.port.judge.dto.response.JudgeWCResponse
import com.leita.leita.repository.BaseEntity
import jakarta.annotation.Nullable
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "judge")
@Access(AccessType.FIELD)
open class Judge(

    @Column(nullable = false)
    open val problemId: Long,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    open val user: User,

    @Nullable
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    open var result: Result? = null,

    @Nullable
    @Embedded
    @Column(nullable = true)
    open var used: UsedInfo? = null,

    @Nullable
    @Column(nullable = true)
    open var sizeOfCode: Long? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    open val type: JudgeType
) : BaseEntity() {
    fun updateSizeOfCode(code: String) {
        this.sizeOfCode = Base64.getDecoder().decode(code).size.toLong()
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