import com.leita.leita.domain.problem.Problem
import com.leita.leita.domain.problem.TestCase
import com.leita.leita.fixtures.DomainFixtures
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class TestCaseTest {

    @Test
    fun `withProblem 메서드는 problem 필드를 설정하고 자기 자신을 반환한다`() {
        val testCases = List(5) { TestCase("input$it", "output$it") }

        val problem = Problem.create(
            title = "Sample",
            author = DomainFixtures.DummyUser(),
            description = DomainFixtures.DummyDescription(),
            limit = DomainFixtures.DummyLimit(),
            testCases = testCases,
            source = "source",
            category = listOf("category1")
        )

        val testCase = TestCase("custom input", "custom output")
        val linkedTestCase = testCase.withProblem(problem)

        assertThat(linkedTestCase.problem).isEqualTo(problem)
        assertThat(linkedTestCase).isSameAs(testCase)
    }
}