import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test
import kotlin.random.Random

/**
 * kotlin.random.Random 을 이용한 추첨
 */
class PrizeDrawing2Test : BaseJUnit5Test() {

    // 경품 데이터 클래스
    data class Prize(val name: String, val weight: Int, var quantity: Int)

    @Test
    fun `PrizeDrawing2 - 1`() {
        // 경품 목록 초기화
        val prizes = listOf(
            Prize("스마트폰", weight = 5, quantity = 1),
            Prize("태블릿", weight = 10, quantity = 2),
            Prize("이어폰", weight = 20, quantity = 5),
            Prize("상품권", weight = 40, quantity = 10),
            Prize("알사탕", weight = 90, quantity = 1000)
        )

        // 추첨할 당첨자 수
        val numberOfWinners = 20

        // 추첨 로직 실행
        val winners = drawPrizes(prizes, numberOfWinners)

        // 결과 출력
        println("Winners: $winners")
    }

    @Test
    fun `PrizeDrawing2 - 2`() {
        // 경품 목록 초기화
        val prizes = listOf(
            Prize("스마트폰", weight = 1, quantity = 1),
            Prize("태블릿", weight = 3, quantity = 2),
            Prize("이어폰", weight = 5, quantity = 5),
            Prize("상품권", weight = 10, quantity = 10),
            Prize("알사탕", weight = 90, quantity = 20)
        )

        val numberOfWinners = 47
        val winners = drawPrizes(prizes, numberOfWinners)
        println("Winners: [${winners.size}/$numberOfWinners: $winners")
    }

    // 추첨 로직 함수
    private fun drawPrizes(prizes: List<Prize>, numberOfWinners: Int): List<String> {
        // 당첨자 목록
        val winners = mutableListOf<String>()

        // 총 가중치 계산
        val totalWeight = prizes.sumOf { it.weight }
        val totalQuantity = prizes.sumOf { it.quantity }

        // 추첨 진행
        var winnerCount = 0
        repeat(numberOfWinners) {
            var winnerFound = false
            while (!winnerFound) {
                if (winnerCount >= totalQuantity) {
                    break
                }
                // 1부터 totalWeight 사이의 무작위 숫자 선택
                val randomNumber = Random.nextInt(1, totalWeight + 1)
                var currentWeight = 0

                for (prize in prizes) {
                    currentWeight += prize.weight
                    if (randomNumber <= currentWeight) {
                        // 해당 경품의 수량이 남아있을 때만 당첨 처리
                        if (prize.quantity > 0) {
                            winners.add(prize.name)
                            prize.quantity--
                            winnerFound = true
                            winnerCount++
                            break
                        }
                    }
                }
            }
        }
        return winners
    }
}
