package com.snc.test.age.calculator

import com.snc.zero.age.calculator.AgeCalculator
import com.snc.zero.extensions.text.padStart
import com.snc.zero.test.base.BaseBehaviorSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class AgeCalculatorTests : BaseBehaviorSpec({

    val yearOfBirth = 1980
    val monthOfBirth = 4
    val dayOfBirth = 9
    val yearOfTarget = 2024
    val monthOfTarget = 4
    val dayOfTarget = 8

    Context("만나이 계산") {
        Given("생년월일과 특정날짜") {
            val birthDate = LocalDate.of(yearOfBirth, monthOfBirth, dayOfBirth) // 생년월일 예시
            val currentDate = LocalDate.of(yearOfTarget, monthOfTarget, dayOfTarget) //LocalDate.now() // 현재 날짜
            When("생년월일과 날짜와 비교하여 만나이 계산") {
                val manAge = AgeCalculator.calculateManAge(birthDate, currentDate)
                println("만나이는 ${manAge}세")
                Then("만나이는 43세 이다.") {
                    manAge shouldBe 43
                }
            }
        }
    }

    Context("보험나이 계산") {
        Given("생년월일과 특정날짜") {
            val birthDate = LocalDate.of(yearOfBirth, monthOfBirth, dayOfBirth) // 생년월일 예시
            val currentDate = LocalDate.of(yearOfTarget, monthOfTarget, dayOfTarget) //LocalDate.now() // 현재 날짜
            When("생년월일과 날짜와 비교하여 보험나이 계산") {
                val insAge = AgeCalculator.calculateManAge(birthDate, currentDate)
                println("보험나이 ${insAge}세")
                Then("보험나이 43세 이다.") {
                    insAge shouldBe 43
                }
            }
        }
    }

    Context("통합 계산") {
        Given("생년월일과 특정날짜") {
            val birthDate = "$yearOfBirth${monthOfBirth.padStart(2, '0')}${dayOfBirth.padStart(2, '0')}" // 생년월일 예시
            val currentDate =
                "$yearOfTarget${monthOfTarget.padStart(2, '0')}${
                    dayOfTarget.padStart(
                        2,
                        '0'
                    )
                }" //LocalDate.now() // 현재 날짜

            When("생년월일과 날짜와 비교하여 통합나이 계산") {
                val age = AgeCalculator.calculateManInsAge(birthDate, currentDate)
                println("통합나이 ${age[0]}세")
                Then("통합나이 43세 이다.") {
                    age[0] shouldBe 43
                }
            }

            When("생년월일과 날짜와 비교하여 통합 계산 ChatGPT") {
                val age = AgeCalculator.calculateManInsAge(
                    yearOfBirth,
                    monthOfBirth,
                    dayOfBirth,
                    yearOfTarget,
                    monthOfTarget,
                    dayOfTarget
                )
                println("통합나이 ${age[0]}세")
                Then("통합나이 43세 이다.") {
                    age[0] shouldBe "43"
                }
            }
        }
    }

    Context("다양한 조건 케이스") {
        Given("생년월일과 특정날짜") {
            When("생일 지나지 않았을 경우") {
                val birthDate = LocalDate.of(2000, 6, 15)
                val targetDate = LocalDate.of(2020, 6, 1)
                val age = AgeCalculator.calculateManAge(birthDate, targetDate)
                Then("나이는 19세이다.") {
                    age shouldBe 19
                }
            }

            When("생일 지난 경우") {
                val birthDate = LocalDate.of(2000, 6, 1)
                val targetDate = LocalDate.of(2020, 6, 15)
                val age = AgeCalculator.calculateManAge(birthDate, targetDate)
                Then("나이는 20세이다.") {
                    age shouldBe 20
                }
            }

            When("생일로부터 6개월 전") {
                val birthDate = LocalDate.of(2000, 1, 1)
                val targetDate = LocalDate.of(2020, 6, 30)
                val age = AgeCalculator.calculateInsAge(birthDate, targetDate)
                Then("나이는 20세이다.") {
                    age shouldBe 20
                }
            }

            When("생일로부터 6개월 지남") {
                val birthDate = LocalDate.of(2000, 1, 1)
                val targetDate = LocalDate.of(2020, 7, 2)
                val age = AgeCalculator.calculateInsAge(birthDate, targetDate)
                Then("나이는 21세이다.") {
                    age shouldBe 21
                }
            }

            When("정상 입력") {
                val result = AgeCalculator.calculateManInsAge(1990, 5, 20, 2020, 6, 20)
                Then("나이는 값들 상세 체크해본다.") {
                    result[0] shouldBe "30" // manAge
                    result[1] shouldBe "30" // insAge
                    result[2].length shouldBe 8 // seniorityDate
                    result[3].length shouldBe 8 // targetDate
                    result[3].length shouldBe 8 // targetDate
                    result[4].toLong() shouldBeGreaterThan 0L // difference
                }
            }

            When("존재하지 않는 날짜") {
                val result = AgeCalculator.calculateManInsAge(1990, 2, 30, 2020, 6, 20)
                Then("결과는 비어있다.") {
                    (result.isEmpty()) shouldBe true
                }
            }
        }
    }
})
