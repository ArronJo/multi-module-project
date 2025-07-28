package com.snc.zero.test.base

import com.snc.zero.test.timer.TestTimer
import io.kotest.core.spec.style.BehaviorSpec

/**
 * BehaviorSpec을 상속받은 기본 테스트 클래스
 * 공통 설정과 유틸리티 메서드를 제공합니다.
 *
 * 사용법: class AgeCalculatorTests : BaseBehaviorSpec({
 *     Given("...") {
 *         When("...") {
 *             Then("...") {
 *                 // 테스트 로직
 *             }
 *         }
 *     }
 * })
 */
abstract class BaseBehaviorSpec(body: BehaviorSpec.() -> Unit = {}) : BehaviorSpec(body) {

    init {
        val timer = TestTimer()
        var count = 0

        beforeTest { test ->
            if (test.name.prefix?.startsWith("When:") == true) {
                println("\n[S] Task Case [${test.name.prefix}]-${++count} -> ${test.name} ")
                timer.start()
            }
        }

        afterTest { (test, result) ->
            if (test.name.prefix?.startsWith("When:") == true) {
                println("[E] Task Case [${test.name.prefix}]-$count -> ${test.name}, Result '${result.isSuccess}' elapse: ${timer.stop()}")
            }
        }
    }
}
