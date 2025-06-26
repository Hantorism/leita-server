package com.leita.leita.fixtures

import com.leita.leita.common.security.SecurityRole
import com.leita.leita.domain.User
import com.leita.leita.domain.problem.Description
import com.leita.leita.domain.problem.Limit

class DomainFixtures {
    class DummyUser : User(
        name = "홍길동",
        email = "hong@example.com",
        profileImage = "https://example.com/profile.jpg",
        sub = "google-oauth-sub-123456",
        role = SecurityRole.USER
    )

    class DummyDescription : Description(
        problem = "자연수 N이 주어졌을 때 1부터 N까지의 합을 구하는 문제입니다.",
        input = "첫째 줄에 자연수 N이 주어진다.",
        output = "1부터 N까지의 합을 출력한다."
    )

    class DummyLimit : Limit(
        memory = 128000,
        time = 2000
    )
}