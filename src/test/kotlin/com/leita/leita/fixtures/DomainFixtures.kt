package com.leita.leita.fixtures

import com.leita.leita.common.security.SecurityRole
import com.leita.leita.user.domain.User
import com.leita.leita.problem.domain.Description
import com.leita.leita.problem.domain.Limit
import com.leita.leita.problem.domain.TestCase

import com.leita.leita.user.domain.Affiliation

class DummyUser: User(
    name = "홍길동",
    email = "hong@example.com",
    profileImage = "https://example.com/profile.jpg",
    githubInfo = null,
    sub = "google-oauth-sub-123456",
    role = SecurityRole.USER,
    affiliation = Affiliation("더미소속", "dummy.com")
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

fun DummyTestCases(count: Int = 5): List<TestCase> =
    List(count) { index ->
        TestCase(
            input = "input$index",
            output = "output$index",
            isShow = index % 2 == 0
        )
    }

class DummyTestCase : TestCase(
    input = "input",
    output = "output",
    isShow = true
)