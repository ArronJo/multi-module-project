package com.snc.test.test.testcase

import com.snc.zero.test.base.BaseJUnit5Test
import com.snc.zero.test.testcase.TestCase
import com.snc.zero.test.testcase.givenContext
import com.snc.zero.test.testcase.givenContextMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@Suppress("NonAsciiCharacters")
class TestCaseTest : BaseJUnit5Test() {

    @Nested
    inner class SingleValue() {

        @Test
        fun `TestCase 클래스 테스트 - 싱글 변수 설정`() {
            TestCase.given {
                "a"
            }.`when` { data ->
                data.uppercase()
            }.then { result ->
                assertEquals("A", result)
            }
        }
    }

    @Nested
    inner class MultiValue() {

        @Test
        fun `TestCase 클래스 테스트 - 변수 2개 설정`() {
            TestCase.given {
                val name = "test"
                val number = 42
                givenContext(name, number)
            }.`when` { (name, number) ->
                "$name: $number".uppercase()
            }.then { result ->
                assertEquals("TEST: 42", result)
            }
        }

        @Test
        fun `TestCase 클래스 테스트 - 변수 3개 설정`() {
            TestCase.given {
                val name = "test"
                val number = 42
                val sex = "Female"
                givenContext(name, number, sex)
            }.`when` { (name, number, sex) ->
                "$name: $number: $sex".uppercase()
            }.then { result ->
                assertEquals("TEST: 42: FEMALE", result)
            }
        }

        @Test
        fun `TestCase 클래스 테스트 - 변수 4개 설정`() {
            TestCase.given {
                val name = "test"
                val number = 42
                val sex = "Female"
                val tall = 185
                givenContext(name, number, sex, tall)
            }.`when` { (name, number, sex, tall) ->
                "$name: $number: $sex $tall".uppercase()
            }.then { result ->
                assertEquals("TEST: 42: FEMALE 185", result)
            }
        }

        @Test
        fun `TestCase 클래스 테스트 - 변수 5개 설정`() {
            TestCase.given {
                val name = "test"
                val number = 42
                val sex = "Female"
                val tall = 185
                val weight = 72
                givenContext(name, number, sex, tall, weight)
            }.`when` { (name, number, sex, tall, weight) ->
                "$name: $number: $sex $tall $weight".uppercase()
            }.then { result ->
                assertEquals("TEST: 42: FEMALE 185 72", result)
            }
        }

        @Test
        fun `TestCase 클래스 테스트 - 변수 n개 설정`() {
            TestCase.given {
                givenContextMap(
                    "name" to "test",
                    "number" to 42,
                    "active" to true
                )
            }.`when` { context ->
                val name: String = context.get("name")
                val number: Int = context.get("number")
                val active: Boolean = context.get("active")

                if (active) "$name: $number" else "inactive"
            }.then { result ->
                assertEquals("test: 42", result)
            }
        }
    }

    @Nested
    inner class ExceptionCase() {

        @Test
        fun `TestCase 클래스 테스트 - 예외 케이스 1`() {
            TestCase.given {
                "not a number"
            }.`when` { data ->
                data.toInt()
            }.catch { e ->
                assertThrows(NumberFormatException::class.java) { throw e }
            }
        }

        @Test
        fun `TestCase 클래스 테스트 - 예외 케이스 2`() {
            TestCase.given {
                "not a number"
            }.`when` { data ->
                val e = assertThrows(NumberFormatException::class.java) {
                    data.toInt()
                }
                e.message!!
            }.then { result ->
                println(result)
            }
        }

        @Test
        fun `TestCase 클래스 테스트 - 실패 케이스 (의도적으로 실패하는 테스트) 1`() {
            try {
                TestCase.given {
                    5
                }.`when` { data ->
                    data * 2
                }.then { result ->
                    assertEquals(11, result)
                }
            } catch (e: AssertionError) {
                println("Test failed as expected: ${e.message}")
            }
        }

        @Test
        fun `TestCase 클래스 테스트 - 실패 케이스 (의도적으로 실패하는 테스트) 2`() {
            try {
                TestCase.given {
                    5
                }.catch { e ->
                    println("catch: ${e.message}")
                }
            } catch (e: AssertionError) {
                println("Test failed as expected: ${e.message}")
            }
        }

        @Test
        fun `TestCase 클래스 테스트 - 실패 케이스 (의도적으로 실패하는 테스트) 3`() {
            try {
                TestCase.given {
                    5
                }.`when` {
                    throw AssertionError("test test")
                }.catch { e ->
                    println("catch: ${e.message}")
                }
            } catch (e: AssertionError) {
                println("Test failed as expected: ${e.message}")
            }
        }
    }

    @Nested
    inner class GivenStageExceptionTest {

        @Test
        fun `given 블록에서 예외가 발생하면 givenException에 저장된다`() {
            // given
            val expectedException = RuntimeException("Given에서 발생한 예외")

            // when
            val testCase = TestCase.given<String> {
                throw expectedException
            }

            // then - catch를 통해 예외 확인
            var caughtException: Exception? = null
            testCase.catch { exception ->
                caughtException = exception
            }

            assertEquals(expectedException, caughtException)
            assertEquals("Given에서 발생한 예외", caughtException?.message)
        }

        @Test
        fun `given에서 예외가 발생한 후 when을 호출하면 WhenResult에 예외가 전파된다`() {
            // given
            val expectedException = IllegalArgumentException("잘못된 인자")
            val testCase = TestCase.given<String> {
                throw expectedException
            }

            // when
            val whenResult = testCase.`when` { data ->
                data.uppercase() // 실행되지 않아야 함
            }

            // then - when 결과에서 예외 확인
            var caughtException: Exception? = null
            whenResult.catch { exception ->
                caughtException = exception
            }

            assertEquals(expectedException, caughtException)
        }

        @Test
        fun `given에서 예외 발생 후 then 호출시 예외가 다시 던져진다`() {
            // given
            val expectedException = NullPointerException("Null 값 발생")
            val testCase = TestCase.given<String> {
                throw expectedException
            }

            // when & then
            val whenResult = testCase.`when` { data ->
                data.length
            }

            // then 호출시 예외가 다시 던져져야 함
            val thrownException = assertThrows<NullPointerException> {
                whenResult.then { result ->
                    assertTrue(result > 0)
                }
            }

            assertEquals(expectedException, thrownException)
            assertEquals("Null 값 발생", thrownException.message)
        }
    }

    @Nested
    inner class WhenStageExceptionTest {

        @Test
        fun `when 블록에서 예외가 발생하면 WhenResult에 저장된다`() {
            // given
            val testCase = TestCase.given { "valid data" }
            val expectedException = UnsupportedOperationException("지원하지 않는 연산")

            // when
            val whenResult = testCase.`when` { _: String ->
                throw expectedException
            }

            // then
            var caughtException: Exception? = null
            whenResult.catch { exception ->
                caughtException = exception
            }

            assertEquals(expectedException, caughtException)
            assertEquals("지원하지 않는 연산", caughtException?.message)
        }
    }

    @Nested
    inner class NormalFlowTest {

        @Test
        fun `given과 when이 모두 성공하면 then에서 결과를 검증할 수 있다`() {
            // given & when & then
            TestCase.given {
                "hello world"
            }.`when` { data ->
                data.uppercase()
            }.then { result ->
                assertEquals("HELLO WORLD", result)
            }
        }

        @Test
        fun `복합 데이터 타입으로 정상 플로우 테스트`() {
            // given & when & then
            TestCase.given {
                givenContext("test", 42)
            }.`when` { (name, number) ->
                "$name: $number".uppercase()
            }.then { result ->
                assertEquals("TEST: 42", result)
            }
        }

        @Test
        fun `catch 블록은 예외가 없을 때 실행되지 않는다`() {
            // given
            val testCase = TestCase.given { "정상 데이터" }
            var catchCalled = false

            // when
            testCase.catch {
                catchCalled = true
            }

            // then
            assertFalse(catchCalled)
        }
    }

    @Nested
    inner class NullDataHandlingTest {

        @Test
        fun `givenData가 null인 경우 IllegalStateException이 발생한다`() {
            // given - null을 반환하는 시나리오 시뮬레이션
            val testCase = TestCase.given<String?> { null }

            // when
            val whenResult = testCase.`when` { data ->
                data?.uppercase() ?: "DEFAULT"
            }

            // then - IllegalStateException 확인
            var caughtException: Exception? = null
            whenResult.catch { exception ->
                caughtException = exception
            }

            assertTrue(caughtException is IllegalStateException)
            assertEquals("Given data is null", caughtException?.message)
        }
    }

    @Nested
    inner class VariousExceptionTypesTest {

        @Test
        fun `다양한 예외 타입들이 올바르게 처리된다`() {
            val exceptions = listOf(
                RuntimeException("런타임 예외"),
                IllegalArgumentException("잘못된 인자"),
                IllegalStateException("잘못된 상태"),
                NullPointerException("널 포인터"),
                UnsupportedOperationException("미지원 연산")
            )

            exceptions.forEach { expectedException ->
                // given
                val testCase = TestCase.given<String> {
                    throw expectedException
                }

                // when & then
                var caughtException: Exception? = null
                testCase.catch { exception ->
                    caughtException = exception
                }

                assertEquals(expectedException, caughtException)
                assertEquals(expectedException.message, caughtException?.message)
            }
        }
    }

    @Nested
    inner class ExceptionChainingTest {

        @Test
        fun `given 예외 후 when catch 그리고 다시 then catch 체이닝 테스트`() {
            // given
            val expectedException = RuntimeException("체이닝 테스트")

            var givenCatchCalled = false
            var whenCatchCalled = false

            // when & then
            val testCase = TestCase.given<String> {
                throw expectedException
            }

            // given 단계 catch
            testCase.catch {
                givenCatchCalled = true
            }

            // when 단계로 진행
            val whenResult = testCase.`when` { data ->
                data.uppercase()
            }

            // when 단계 catch
            whenResult.catch {
                whenCatchCalled = true
            }

            // then
            assertTrue(givenCatchCalled)
            assertTrue(whenCatchCalled)
        }
    }
}
