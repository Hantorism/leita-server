package com.leita.leita

import com.leita.leita.common.exception.CustomException
import com.leita.leita.domain.problem.*
import com.leita.leita.fixtures.DummyDescription
import com.leita.leita.fixtures.DummyLimit
import com.leita.leita.fixtures.DummyTestCases
import com.leita.leita.fixtures.DummyUser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.assertj.core.api.Assertions.assertThat

class ProblemTest {

    private val author = DummyUser()
    private val description = DummyDescription()
    private val limit = DummyLimit()

    @Test
    fun `create 메서드는 테스트 케이스 5개 미만이면 예외를 던진다`() {
        val testCases = DummyTestCases(4)

        val exception = assertThrows<CustomException> {
            Problem.create(
                title = "Title",
                author = author,
                description = description,
                limit = limit,
                testCases = testCases,
                source = "source",
                category = listOf("cat"),
            )
        }
        assertThat(exception.message).isEqualTo("테스트 케이스는 최소 5개 이상이어야 합니다.")
    }

    @Test
    fun `create 메서드는 정상적으로 Problem 객체를 생성한다`() {

        val problem = Problem.create(
            title = "Title",
            author = author,
            description = description,
            limit = limit,
            testCases = DummyTestCases(5),
            source = "source",
            category = listOf("cat1", "cat2")
        )

        assertThat(problem.title).isEqualTo("Title")
        assertThat(problem.testCases).hasSize(5)
        assertThat(problem.category).containsExactly("cat1", "cat2")
    }

    @Test
    fun `update 메서드는 필드와 테스트 케이스, 카테고리를 갱신한다`() {
        val initialTestCases = DummyTestCases(5)
        val problem = Problem.create(
            title = "Old Title",
            author = author,
            description = description,
            limit = limit,
            testCases = initialTestCases,
            source = "old source",
            category = listOf("oldCat")
        )

        val newDescription = DummyDescription()
        val newLimit = DummyLimit()
        val newTestCases = DummyTestCases(6)
        val newCategory = listOf("newCat1", "newCat2")

        problem.update(
            title = "New Title",
            description = newDescription,
            limit = newLimit,
            testCases = newTestCases,
            source = "new source",
            category = newCategory
        )

        assertThat(problem.title).isEqualTo("New Title")
        assertThat(problem.description).isEqualTo(newDescription)
        assertThat(problem.limit).isEqualTo(newLimit)
        assertThat(problem.testCases).hasSize(6)
        assertThat(problem.category).containsExactlyElementsOf(newCategory)
        assertThat(problem.source).isEqualTo("new source")
    }
}