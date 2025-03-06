package com.snc.test.engtokor

import com.snc.zero.hangul.engtokor.EngToKor
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class EngToKorTest : BaseJUnit5Test() {

    @Test
    fun `영타 - 한글 변환 - 1`() {
        val input = "dkssudgktpdy"
        val output = EngToKor().en2ko(input)
        logger.debug { "입력: $input" }
        logger.debug { "출력: $output" }
    }

    @Test
    fun `단순 자음 모음 변환`() {
        assertEquals("ㅎ", EngToKor().en2ko("g"))
        assertEquals("ㅏ", EngToKor().en2ko("k"))
        assertEquals("ㄴ", EngToKor().en2ko("s"))
    }

    @Test
    fun `기본 단어 변환`() {
        assertEquals("안녕", EngToKor().en2ko("dkssud"))
        assertEquals("한글", EngToKor().en2ko("gksrmf"))
        assertEquals("테스트", EngToKor().en2ko("xptmxm"))
    }

    @Test
    fun `복잡한 단어 변환`() {
        assertEquals("안녕하세요", EngToKor().en2ko("dkssudgktpdy"))
        assertEquals("반갑습니다", EngToKor().en2ko("qksrkqtmqslek"))
        assertEquals("코틀린테스트", EngToKor().en2ko("zhxmfflsxptmxm"))
    }

    @Test
    fun `대소문자 혼합 입력`() {
        assertEquals("DㅏS녀DㅎK솅Y", EngToKor().en2ko("DkSsuDgKtPdY"))
        assertEquals("Xㅔ쓰Xㅡ", EngToKor().en2ko("XpTmXm"))
    }

    @Test
    fun `받침 있는 글자 변환`() {
        assertEquals("값", EngToKor().en2ko("rkqt"))
        assertEquals("닭", EngToKor().en2ko("ekfr"))
        assertEquals("밟다", EngToKor().en2ko("qkfqek"))
    }

    @Test
    fun `겹받침 변환`() {
        assertEquals("늙다", EngToKor().en2ko("smfrek"))
        assertEquals("읊다", EngToKor().en2ko("dmfvek"))
        assertEquals("핥다", EngToKor().en2ko("gkfxek"))
    }

    @Test
    fun `이중 모음 변환`() {
        assertEquals("ㅘ", EngToKor().en2ko("hk"))
        assertEquals("ㅚ", EngToKor().en2ko("hl"))
        assertEquals("ㅟ", EngToKor().en2ko("nl"))
    }

    @Test
    fun `빈 문자열 입력`() {
        assertEquals("", EngToKor().en2ko(""))
    }

    @Test
    fun `특수 문자 및 숫자 포함 입력`() {
        assertEquals("안녕1 2 3", EngToKor().en2ko("dkssud1 2 3"))
        assertEquals("테스트!", EngToKor().en2ko("xptmxm!"))
    }

    @Test
    fun `연속된 자음 입력`() {
        assertEquals("ㄱㄴㄷㄹ", EngToKor().en2ko("rsef"))
    }

    @Test
    fun `연속된 모음 입력`() {
        assertEquals("ㅏㅑㅓㅕ", EngToKor().en2ko("kiju"))
    }

    @Test
    fun `긴 문장 변환`() {
        assertEquals(
            "안녕하세요 반갑습니다 코틀린으로 테스트 중입니다",
            EngToKor().en2ko("dkssudgktpdy qksrkqtmqslek zhxmfflsdmfh xptmxm wnddlqslek")
        )
    }

    @Test
    fun `초성없이 중성만 입력`() {
        assertEquals("ㅏ", EngToKor().en2ko("k"))
        assertEquals("ㅓㅏ", EngToKor().en2ko("jk"))
    }

    @Test
    fun `중성없이 초성과 종성만 입력`() {
        assertEquals("ㄱㄴ", EngToKor().en2ko("rs"))
        assertEquals("ㅂㅇ", EngToKor().en2ko("qd"))
    }

    @Test
    fun `중성 다음에 다시 중성 입력`() {
        assertEquals("가ㅏ", EngToKor().en2ko("rkk"))
        assertEquals("노ㅜ", EngToKor().en2ko("shn"))
    }

    @Test
    fun `초성을 연속해서 입력`() {
        assertEquals("ㄱㄴㄷㄹ마", EngToKor().en2ko("rsefak"))
    }

    @Test
    fun `받침 다음에 다시 받침 입력`() {
        assertEquals("갃ㅅ", EngToKor().en2ko("rkrtt"))
        assertEquals("곧ㄱ", EngToKor().en2ko("rher"))
    }

    @Test
    fun `복잡한 오토마타 깨짐 케이스`() {
        assertEquals("가낟랄마", EngToKor().en2ko("rkskefkfak"))
        assertEquals("바북사", EngToKor().en2ko("qkqnrtk"))
    }

    @Test
    fun `한글과 영어 혼용`() {
        assertEquals("아누ㅛㅐㅕㅜㅎ", EngToKor().en2ko("dksnyoung"))
        assertEquals("ㅗ디ㅣогㅏ세요", EngToKor().en2ko("hellогktpdy"))
    }

    @Test
    fun `불가능한 자음 조합`() {
        assertEquals("ㄳㄷ", EngToKor().en2ko("rte"))
        assertEquals("ㅂㅈㄹ", EngToKor().en2ko("qwf"))
    }

    @Test
    fun `기타 조합`() {
        val en2koArray = listOf(
            "와", "왜", "외", "워", "웨", "위", "긔",
            "핛", "핝", "핞", "핡", "핢", "핣", "핤", "핥", "핧", "힚"
        )
        val engToKor = EngToKor()
        en2koArray.forEach {
            engToKor.en2ko(it)
        }
    }
}
