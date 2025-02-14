package com.leita.leita.repository

import com.leita.leita.domain.submit.Submit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SubmitRepository: JpaRepository<Submit, Long> {
}