import com.leita.leita.domain.problem.Problem
import com.leita.leita.domain.problem.TestCase
import com.leita.leita.fixtures.DummyDescription
import com.leita.leita.fixtures.DummyLimit
import com.leita.leita.fixtures.DummyTestCase
import com.leita.leita.fixtures.DummyTestCases
import com.leita.leita.fixtures.DummyUser
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class TestCaseTest {

    @Test
    fun `withProblem 메서드는 problem 필드를 설정하고 자기 자신을 반환한다`() {

        val problem = Problem.create(
            title = "Sample",
            author = DummyUser(),
            description = DummyDescription(),
            limit = DummyLimit(),
            testCases = DummyTestCases(5),
            source = "source",
            category = listOf("category1")
        )

        val testCase = DummyTestCase()
        val linkedTestCase = testCase.withProblem(problem)

        assertThat(linkedTestCase.problem).isEqualTo(problem)
        assertThat(linkedTestCase).isSameAs(testCase)
    }
    @Test
    fun `show 메서드는 isShow 필드를 true로 설정한다`() {
        // given
        val testCase = TestCase(
            input = "입력 예시",
            output = "출력 예시",
            isShow = false
        )

        // when
        testCase.show()

        // then
        assertThat(testCase.isShow).isTrue()
    }

    @Test
    fun `hide 메서드는 isShow 필드를 false로 설정한다`() {
        // given
        val testCase = TestCase(
            input = "입력 예시",
            output = "출력 예시",
            isShow = true
        )

        // when
        testCase.hide()

        // then
        assertThat(testCase.isShow).isFalse()
    }

    @Test
    fun `show 메서드는 이미 isShow가 true인 경우에도 정상적으로 동작한다`() {
        // given
        val testCase = TestCase(
            input = "입력 예시",
            output = "출력 예시",
            isShow = true
        )

        // when
        testCase.show()

        // then
        assertThat(testCase.isShow).isTrue()
    }

    @Test
    fun `hide 메서드는 이미 isShow가 false인 경우에도 정상적으로 동작한다`() {
        // given
        val testCase = TestCase(
            input = "입력 예시",
            output = "출력 예시",
            isShow = false
        )

        // when
        testCase.hide()

        // then
        assertThat(testCase.isShow).isFalse()
    }
}