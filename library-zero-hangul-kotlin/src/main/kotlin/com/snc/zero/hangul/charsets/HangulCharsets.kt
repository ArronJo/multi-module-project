package com.snc.zero.hangul.charsets

import java.util.Locale

/**
 * https://www.unicode.org/charts/
 *
 * 0x3130 ~ 318E: Hangul Compatibility Jamo
 * https://www.unicode.org/charts/PDF/U3130.pdf
 * https://en.wikipedia.org/wiki/Hangul_Compatibility_Jamo
 *
 * 0x1100 ~ 11FF: Hangul Jamo
 * https://www.unicode.org/charts/PDF/U1100.pdf
 * https://en.wikipedia.org/wiki/Hangul_Jamo_(Unicode_block)
 *
 * 0xA960 ~ A97F: Hangul Jamo Extended-A
 * https://www.unicode.org/charts/PDF/UA960.pdf
 * https://en.wikipedia.org/wiki/Hangul_Jamo_Extended-A
 *
 * 0xAC00 ~ D7AF: Hangul Syllables
 * https://www.unicode.org/charts/PDF/UAC00.pdf
 * https://en.wikipedia.org/wiki/Hangul_Syllables
 *
 * 0xD7B0 ~ D7FF: Hangul Jamo Extended-B
 * https://www.unicode.org/charts/PDF/UD7B0.pdf
 * https://en.wikipedia.org/wiki/Hangul_Jamo_Extended-B
 *
 * 0xFF00 ~ FFEF: Halfwidth and Fullwidth Forms
 * https://www.unicode.org/charts/PDF/UFF00.pdf
 * https://en.wikipedia.org/wiki/Halfwidth_and_Fullwidth_Forms_(Unicode_block)
 */
class HangulCharsets private constructor() {

    companion object {

        val COMPAT_CHOSEONG_COLLECTION = charArrayOf(
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        )
        val COMPAT_JUNGSEONG_COLLECTION = charArrayOf(
            'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
            'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ',
            'ㅣ'
        )
        val COMPAT_JONGSEONG_COLLECTION = charArrayOf(
            ' ',
            'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ',
            'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ',
            'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        )

        private const val CHOSEONG_COUNT = 19
        const val JUNGSEONG_COUNT = 21
        const val JONGSEONG_COUNT = 28
        private const val HANGUL_SYLLABLE_COUNT = CHOSEONG_COUNT * JUNGSEONG_COUNT * JONGSEONG_COUNT
        const val HANGUL_SYLLABLES_BASE = 0xAC00
        const val HANGUL_SYLLABLES_END = HANGUL_SYLLABLES_BASE + HANGUL_SYLLABLE_COUNT

        /**
         * 문자열을 유니코드 값으로 변경
         *
         * @param str - 문자열
         * @return - UNICODE int[]
         */
        fun unicode(str: String): IntArray {
            val unicode = IntArray(str.length)
            val buff = StringBuilder()
            for (i in str.indices) {
                unicode[i] = str.codePointAt(i)
                buff.append(String.format(Locale.getDefault(), "U+%04x", unicode[i]))
            }
            return unicode
        }

        /* ========================================== */

        /*
         * 정규식 등을 위한 정의
         */
        const val HANGUL_CHO = "\\u1100-\\u115E\\u3131-\\u314E" // "\\u1100-\\u115Eㄱ-ㅎ"
        const val HANGUL_JUNG = "\\u1161-\\u11A7\\u314F-\\u3163\\u3187-\\u318E\\uD7B0-\\uD7C6" // "\\u1161-\\u11A7ㅏ-ㅣㆇ-ㆎ\\uD7B0-\\uD7C6"
        const val HANGUL_JONG = "\\u11A8-\\u11FF\\u3165-\\u3186\\uA960-\\uA97C\\uD7CB-\\uD7FB" // "\u11A8-\u11FFㅥ-ㆆ\\uA960-\\uA97C\\uD7CB-\\uD7FB"
        const val HANGUL_AREA = "\\u00B7\\u119E\\u11A2\\u2022\\u2024\\u2025\\u2219\\u302E\\u318D" // "·ᆞᆢ•․‥∙〮ㆍ"
        const val HANGUL_COMPATIBILITY = "\\uAC00-\\uD7A3" // "가-힣"

        // 한글 패턴 총 집합 정규식
        const val PATTERN_HANGUL = "[$HANGUL_CHO$HANGUL_JUNG$HANGUL_JONG$HANGUL_JUNG$HANGUL_AREA$HANGUL_COMPATIBILITY]+"

        /*
            SpoqaHanSansNeo 에서 한글 입력 Automata 를 위한 구현 폰트들로 2,350 자 정도 된다.
            : 꼭 필요한 2,350자만을 추려 용량을 가볍게 줄였고 웹 환경에서 보다 편리하게 사용 가능합니다.
            : 한글 2,350자, 영문 94자 KS약물 986자
         */
        // "\\uAC00\\uAC01\\uAC04\\uAC07\\uAC08\\uAC09\\uAC0A\\uAC0B\\uAC10\\uAC11\\uAC12\\uAC13\\uAC14\\uAC15\\uAC16\\uAC17\\uAC19\\uAC1A\\uAC1B\\uAC1C\\uAC1D\\uAC20\\uAC23\\uAC24\\uAC2C\\uAC2D\\uAC2F\\uAC30\\uAC31\\uAC38\\uAC39\\uAC3C\\uAC40\\uAC4B\\uAC4D\\uAC54\\uAC58\\uAC5C\\uAC65\\uAC70\\uAC71\\uAC74\\uAC77\\uAC78\\uAC7A\\uAC80\\uAC81\\uAC83\\uAC84\\uAC85\\uAC86\\uAC89\\uAC8A\\uAC8B\\uAC8C\\uAC90\\uAC94\\uAC9C\\uAC9D\\uAC9F\\uACA0\\uACA1\\uACA8\\uACA9\\uACAA\\uACAC\\uACAF\\uACB0\\uACB7\\uACB8\\uACB9\\uACBB\\uACBC\\uACBD\\uACC1\\uACC4\\uACC8\\uACCC\\uACD5\\uACD7\\uACE0\\uACE1\\uACE4\\uACE7\\uACE8\\uACEA\\uACEC\\uACEF\\uACF0\\uACF1\\uACF3\\uACF5\\uACF6\\uACFC\\uACFD\\uAD00\\uAD04\\uAD06\\uAD0C\\uAD0D\\uAD0F\\uAD10\\uAD11\\uAD18\\uAD1C\\uAD20\\uAD22\\uAD29\\uAD2C\\uAD2D\\uAD34\\uAD35\\uAD38\\uAD3C\\uAD44\\uAD45\\uAD47\\uAD49\\uAD50\\uAD54\\uAD58\\uAD60\\uAD61\\uAD63\\uAD65\\uAD6C\\uAD6D\\uAD70\\uAD73\\uAD74\\uAD75\\uAD76\\uAD7B\\uAD7C\\uAD7D\\uAD7F\\uAD81\\uAD82\\uAD88\\uAD89\\uAD8C\\uAD90\\uAD9C\\uAD9D\\uADA4\\uADB7\\uADB8\\uADC0\\uADC1\\uADC4\\uADC8\\uADD0\\uADD1\\uADD3\\uADD5\\uADDC\\uADE0\\uADE4\\uADEC\\uADF8\\uADF9\\uADFC\\uADFF\\uAE00\\uAE01\\uAE02\\uAE07\\uAE08\\uAE09\\uAE0B\\uAE0D\\uAE13\\uAE14\\uAE30\\uAE31\\uAE34\\uAE37\\uAE38\\uAE3A\\uAE40\\uAE41\\uAE43\\uAE44\\uAE45\\uAE46\\uAE4A\\uAE4C\\uAE4D\\uAE4E\\uAE50\\uAE54\\uAE56\\uAE5C\\uAE5D\\uAE5F\\uAE60\\uAE61\\uAE65\\uAE68\\uAE69\\uAE6C\\uAE6F\\uAE70\\uAE78\\uAE79\\uAE7B\\uAE7C\\uAE7D\\uAE84\\uAE85\\uAE86\\uAE8C\\uAE8D\\uAEBC\\uAEBD\\uAEBE\\uAEC0\\uAEC4\\uAECC\\uAECD\\uAECF\\uAED0\\uAED1\\uAED3\\uAED5\\uAED8\\uAED9\\uAEDC\\uAEE8\\uAEEB\\uAEED\\uAEF4\\uAEF8\\uAEFC\\uAF07\\uAF08\\uAF09\\uAF0D\\uAF10\\uAF2C\\uAF2D\\uAF30\\uAF32\\uAF33\\uAF34\\uAF3C\\uAF3D\\uAF3F\\uAF41\\uAF42\\uAF43\\uAF45\\uAF48\\uAF49\\uAF50\\uAF5C\\uAF5D\\uAF64\\uAF65\\uAF78\\uAF79\\uAF80\\uAF84\\uAF88\\uAF90\\uAF91\\uAF95\\uAF9C\\uAFB8\\uAFB9\\uAFBC\\uAFC0\\uAFC7\\uAFC8\\uAFC9\\uAFCB\\uAFCD\\uAFCE\\uAFD4\\uAFD8\\uAFDC\\uAFE8\\uAFE9\\uAFF0\\uAFF1\\uAFF4\\uAFF8\\uB000\\uB001\\uB004\\uB00C\\uB010\\uB014\\uB01C\\uB01D\\uB028\\uB030\\uB03C\\uB044\\uB045\\uB048\\uB04A\\uB04C\\uB04E\\uB053\\uB054\\uB055\\uB057\\uB059\\uB05D\\uB07C\\uB07D\\uB080\\uB084\\uB08C\\uB08D\\uB08F\\uB091\\uB098\\uB099\\uB09A\\uB09C\\uB09F\\uB0A0\\uB0A1\\uB0A2\\uB0A8\\uB0A9\\uB0AB\\uB0AC\\uB0AD\\uB0AE\\uB0AF\\uB0B1\\uB0B3\\uB0B4\\uB0B5\\uB0B8\\uB0BB\\uB0BC\\uB0C4\\uB0C5\\uB0C7\\uB0C8\\uB0C9\\uB0D0\\uB0D1\\uB0D4\\uB0D7\\uB0D8\\uB0E0\\uB0E1\\uB0E3\\uB0E5\\uB0EC\\uB108\\uB109\\uB10B\\uB10C\\uB10F\\uB110\\uB112\\uB113\\uB118\\uB119\\uB11B\\uB11C\\uB11D\\uB122\\uB123\\uB124\\uB125\\uB128\\uB12B\\uB12C\\uB134\\uB135\\uB137\\uB138\\uB139\\uB140\\uB141\\uB144\\uB147\\uB148\\uB150\\uB151\\uB154\\uB155\\uB158\\uB15C\\uB160\\uB171\\uB178\\uB179\\uB17C\\uB180\\uB181\\uB182\\uB188\\uB189\\uB18B\\uB18D\\uB191\\uB192\\uB193\\uB194\\uB198\\uB19C\\uB1A8\\uB1B0\\uB1C4\\uB1CC\\uB1D0\\uB1D4\\uB1DC\\uB1DD\\uB1DF\\uB1E1\\uB1E8\\uB1E9\\uB1EC\\uB1F0\\uB1F8\\uB1F9\\uB1FB\\uB1FD\\uB204\\uB205\\uB208\\uB20B\\uB20C\\uB20D\\uB214\\uB215\\uB217\\uB219\\uB21D\\uB220\\uB234\\uB23C\\uB258\\uB25C\\uB260\\uB268\\uB269\\uB274\\uB275\\uB27B\\uB27C\\uB284\\uB285\\uB289\\uB290\\uB291\\uB294\\uB297\\uB298\\uB299\\uB29A\\uB2A0\\uB2A1\\uB2A3\\uB2A5\\uB2A6\\uB2A7\\uB2AA\\uB2AC\\uB2B0\\uB2B4\\uB2BC\\uB2C1\\uB2C8\\uB2C9\\uB2CC\\uB2CF\\uB2D0\\uB2D2\\uB2D8\\uB2D9\\uB2DB\\uB2DD\\uB2E2\\uB2E4\\uB2E5\\uB2E6\\uB2E8\\uB2EB\\uB2EC\\uB2ED\\uB2EE\\uB2EF\\uB2F3\\uB2F4\\uB2F5\\uB2F7\\uB2F8\\uB2F9\\uB2FA\\uB2FB\\uB2FD\\uB2FF\\uB300\\uB301\\uB304\\uB308\\uB310\\uB311\\uB313\\uB314\\uB315\\uB31C\\uB320\\uB354\\uB355\\uB356\\uB358\\uB35B\\uB35C\\uB35E\\uB35F\\uB364\\uB365\\uB367\\uB369\\uB36B\\uB36E\\uB370\\uB371\\uB374\\uB378\\uB380\\uB381\\uB383\\uB384\\uB385\\uB38C\\uB390\\uB394\\uB3A0\\uB3A1\\uB3A8\\uB3AC\\uB3C4\\uB3C5\\uB3C8\\uB3CB\\uB3CC\\uB3CE\\uB3D0\\uB3D4\\uB3D5\\uB3D7\\uB3D9\\uB3DB\\uB3DD\\uB3E0\\uB3E4\\uB3E8\\uB3FC\\uB410\\uB418\\uB41C\\uB420\\uB428\\uB429\\uB42B\\uB42D\\uB434\\uB450\\uB451\\uB454\\uB457\\uB458\\uB45A\\uB460\\uB461\\uB463\\uB465\\uB46C\\uB480\\uB488\\uB499\\uB49D\\uB4A4\\uB4A8\\uB4AC\\uB4B5\\uB4B7\\uB4B9\\uB4C0\\uB4C4\\uB4C8\\uB4D0\\uB4D5\\uB4DC\\uB4DD\\uB4E0\\uB4E3\\uB4E4\\uB4E6\\uB4EC\\uB4ED\\uB4EF\\uB4F1\\uB4F8\\uB514\\uB515\\uB518\\uB51B\\uB51C\\uB524\\uB525\\uB527\\uB528\\uB529\\uB52A\\uB52E\\uB530\\uB531\\uB534\\uB537\\uB538\\uB540\\uB541\\uB543\\uB544\\uB545\\uB54B\\uB54C\\uB54D\\uB550\\uB554\\uB55C\\uB55D\\uB55F\\uB560\\uB561\\uB5A0\\uB5A1\\uB5A4\\uB5A8\\uB5AA\\uB5AB\\uB5B0\\uB5B1\\uB5B3\\uB5B4\\uB5B5\\uB5BB\\uB5BC\\uB5BD\\uB5C0\\uB5C4\\uB5CC\\uB5CD\\uB5CF\\uB5D0\\uB5D1\\uB5D8\\uB5EC\\uB610\\uB611\\uB614\\uB618\\uB620\\uB621\\uB623\\uB625\\uB62C\\uB62D\\uB630\\uB634\\uB647\\uB648\\uB65C\\uB664\\uB668\\uB69C\\uB69D\\uB6A0\\uB6A4\\uB6A7\\uB6AB\\uB6AC\\uB6B1\\uB6D4\\uB6F0\\uB6F4\\uB6F8\\uB700\\uB701\\uB705\\uB728\\uB729\\uB72C\\uB72F\\uB730\\uB733\\uB738\\uB739\\uB73B\\uB73D\\uB73E\\uB744\\uB748\\uB74C\\uB754\\uB755\\uB760\\uB764\\uB768\\uB770\\uB771\\uB773\\uB775\\uB77C\\uB77D\\uB780\\uB783\\uB784\\uB78C\\uB78D\\uB78F\\uB790\\uB791\\uB792\\uB796\\uB797\\uB798\\uB799\\uB79C\\uB79F\\uB7A0\\uB7A8\\uB7A9\\uB7AB\\uB7AC\\uB7AD\\uB7B2\\uB7B4\\uB7B5\\uB7B8\\uB7C7\\uB7C9\\uB7D4\\uB7EC\\uB7ED\\uB7F0\\uB7F2\\uB7F3\\uB7F4\\uB7FC\\uB7FD\\uB7FF\\uB800\\uB801\\uB807\\uB808\\uB809\\uB80C\\uB810\\uB818\\uB819\\uB81B\\uB81C\\uB81D\\uB824\\uB825\\uB828\\uB82B\\uB82C\\uB834\\uB835\\uB837\\uB838\\uB839\\uB840\\uB844\\uB851\\uB853\\uB85C\\uB85D\\uB860\\uB863\\uB864\\uB86C\\uB86D\\uB86F\\uB871\\uB878\\uB879\\uB87C\\uB88D\\uB894\\uB8A8\\uB8B0\\uB8B4\\uB8B8\\uB8C0\\uB8C1\\uB8C3\\uB8C5\\uB8CC\\uB8D0\\uB8D4\\uB8DD\\uB8DF\\uB8E1\\uB8E8\\uB8E9\\uB8EC\\uB8F0\\uB8F8\\uB8F9\\uB8FB\\uB8FD\\uB904\\uB918\\uB920\\uB924\\uB93C\\uB93D\\uB940\\uB944\\uB94C\\uB94F\\uB951\\uB958\\uB959\\uB95C\\uB960\\uB968\\uB969\\uB96B\\uB96D\\uB974\\uB975\\uB978\\uB97C\\uB984\\uB985\\uB987\\uB989\\uB98A\\uB98D\\uB98E\\uB9AC\\uB9AD\\uB9B0\\uB9B4\\uB9BC\\uB9BD\\uB9BF\\uB9C1\\uB9C8\\uB9C9\\uB9CC\\uB9CE\\uB9CF\\uB9D0\\uB9D1\\uB9D2\\uB9D8\\uB9D9\\uB9DB\\uB9DC\\uB9DD\\uB9DE\\uB9DF\\uB9E1\\uB9E3\\uB9E4\\uB9E5\\uB9E8\\uB9EB\\uB9EC\\uB9F4\\uB9F5\\uB9F7\\uB9F8\\uB9F9\\uB9FA\\uBA00\\uBA01\\uBA04\\uBA08\\uBA15\\uBA38\\uBA39\\uBA3C\\uBA40\\uBA42\\uBA48\\uBA49\\uBA4B\\uBA4D\\uBA4E\\uBA53\\uBA54\\uBA55\\uBA58\\uBA5C\\uBA64\\uBA65\\uBA67\\uBA68\\uBA69\\uBA70\\uBA71\\uBA74\\uBA78\\uBA83\\uBA84\\uBA85\\uBA87\\uBA8C\\uBAA8\\uBAA9\\uBAAB\\uBAAC\\uBAB0\\uBAB1\\uBAB2\\uBAB8\\uBAB9\\uBABB\\uBABD\\uBAC4\\uBAC8\\uBAD8\\uBAD9\\uBAE0\\uBAF4\\uBAFC\\uBB00\\uBB04\\uBB0D\\uBB0F\\uBB11\\uBB18\\uBB1C\\uBB20\\uBB29\\uBB2B\\uBB34\\uBB35\\uBB36\\uBB38\\uBB3B\\uBB3C\\uBB3D\\uBB3E\\uBB44\\uBB45\\uBB47\\uBB49\\uBB4D\\uBB4F\\uBB50\\uBB54\\uBB58\\uBB61\\uBB63\\uBB65\\uBB6C\\uBB88\\uBB8A\\uBB8C\\uBB90\\uBBA4\\uBBA8\\uBBAC\\uBBB4\\uBBB7\\uBBB9\\uBBC0\\uBBC1\\uBBC4\\uBBC8\\uBBD0\\uBBD3\\uBBD5\\uBBDC\\uBBF8\\uBBF9\\uBBFC\\uBBFF\\uBC00\\uBC02\\uBC08\\uBC09\\uBC0B\\uBC0C\\uBC0D\\uBC0F\\uBC11\\uBC14\\uBC15\\uBC16\\uBC17\\uBC18\\uBC1B\\uBC1C\\uBC1D\\uBC1E\\uBC1F\\uBC24\\uBC25\\uBC27\\uBC29\\uBC2D\\uBC30\\uBC31\\uBC34\\uBC37\\uBC38\\uBC40\\uBC41\\uBC43\\uBC44\\uBC45\\uBC49\\uBC4C\\uBC4D\\uBC50\\uBC5C\\uBC5D\\uBC61\\uBC84\\uBC85\\uBC88\\uBC8B\\uBC8C\\uBC8E\\uBC94\\uBC95\\uBC97\\uBC99\\uBC9A\\uBCA0\\uBCA1\\uBCA4\\uBCA7\\uBCA8\\uBCB0\\uBCB1\\uBCB3\\uBCB4\\uBCB5\\uBCBC\\uBCBD\\uBCC0\\uBCC4\\uBCCC\\uBCCD\\uBCCF\\uBCD0\\uBCD1\\uBCD5\\uBCD8\\uBCDC\\uBCF4\\uBCF5\\uBCF6\\uBCF8\\uBCFB\\uBCFC\\uBCFF\\uBD04\\uBD05\\uBD07\\uBD09\\uBD10\\uBD14\\uBD24\\uBD25\\uBD2C\\uBD40\\uBD48\\uBD49\\uBD4C\\uBD50\\uBD58\\uBD59\\uBD64\\uBD68\\uBD74\\uBD80\\uBD81\\uBD84\\uBD87\\uBD88\\uBD89\\uBD8A\\uBD90\\uBD91\\uBD93\\uBD95\\uBD99\\uBD9A\\uBD9C\\uBDA0\\uBDA4\\uBDB0\\uBDB4\\uBDB8\\uBDC1\\uBDD4\\uBDD5\\uBDD8\\uBDDC\\uBDE9\\uBDF0\\uBDF4\\uBDF8\\uBE00\\uBE03\\uBE05\\uBE0C\\uBE0D\\uBE10\\uBE14\\uBE1C\\uBE1D\\uBE1F\\uBE21\\uBE28\\uBE44\\uBE45\\uBE48\\uBE4B\\uBE4C\\uBE4E\\uBE54\\uBE55\\uBE57\\uBE59\\uBE5A\\uBE5B\\uBE60\\uBE61\\uBE64\\uBE67\\uBE68\\uBE6A\\uBE70\\uBE71\\uBE73\\uBE74\\uBE75\\uBE7B\\uBE7C\\uBE7D\\uBE80\\uBE84\\uBE8C\\uBE8D\\uBE8F\\uBE90\\uBE91\\uBE98\\uBE99\\uBE9C\\uBEA8\\uBED0\\uBED1\\uBED4\\uBED7\\uBED8\\uBEE0\\uBEE3\\uBEE4\\uBEE5\\uBEEC\\uBF01\\uBF08\\uBF09\\uBF18\\uBF19\\uBF1B\\uBF1C\\uBF1D\\uBF40\\uBF41\\uBF44\\uBF48\\uBF50\\uBF51\\uBF53\\uBF55\\uBF94\\uBFB0\\uBFB1\\uBFC5\\uBFCC\\uBFCD\\uBFD0\\uBFD4\\uBFD5\\uBFDC\\uBFDD\\uBFDF\\uBFE1\\uC020\\uC03C\\uC051\\uC058\\uC05C\\uC060\\uC068\\uC069\\uC06D\\uC090\\uC091\\uC094\\uC098\\uC0A0\\uC0A1\\uC0A3\\uC0A5\\uC0AC\\uC0AD\\uC0AF\\uC0B0\\uC0B3\\uC0B4\\uC0B5\\uC0B6\\uC0BC\\uC0BD\\uC0BF\\uC0C0\\uC0C1\\uC0C5\\uC0C8\\uC0C9\\uC0CC\\uC0CF\\uC0D0\\uC0D8\\uC0D9\\uC0DB\\uC0DC\\uC0DD\\uC0E4\\uC0E5\\uC0E8\\uC0EC\\uC0F4\\uC0F5\\uC0F7\\uC0F9\\uC0FE\\uC100\\uC101\\uC104\\uC108\\uC110\\uC115\\uC11C\\uC11D\\uC11E\\uC11F\\uC120\\uC123\\uC124\\uC126\\uC127\\uC12C\\uC12D\\uC12F\\uC130\\uC131\\uC136\\uC138\\uC139\\uC13C\\uC13F\\uC140\\uC148\\uC149\\uC14B\\uC14C\\uC14D\\uC154\\uC155\\uC158\\uC15C\\uC164\\uC165\\uC167\\uC168\\uC169\\uC170\\uC171\\uC174\\uC178\\uC180\\uC181\\uC185\\uC18C\\uC18D\\uC18E\\uC190\\uC193\\uC194\\uC196\\uC19C\\uC19D\\uC19F\\uC1A1\\uC1A5\\uC1A8\\uC1A9\\uC1AC\\uC1B0\\uC1BD\\uC1C4\\uC1C8\\uC1CC\\uC1D4\\uC1D7\\uC1D8\\uC1E0\\uC1E4\\uC1E8\\uC1F0\\uC1F1\\uC1F3\\uC1F5\\uC1FC\\uC1FD\\uC200\\uC204\\uC20C\\uC20D\\uC20F\\uC211\\uC216\\uC218\\uC219\\uC21C\\uC21F\\uC220\\uC228\\uC229\\uC22B\\uC22D\\uC22F\\uC231\\uC232\\uC234\\uC248\\uC250\\uC251\\uC254\\uC258\\uC260\\uC265\\uC26C\\uC26D\\uC270\\uC274\\uC27C\\uC27D\\uC27F\\uC281\\uC288\\uC289\\uC28C\\uC290\\uC298\\uC29B\\uC29D\\uC2A4\\uC2A5\\uC2A8\\uC2AC\\uC2AD\\uC2B4\\uC2B5\\uC2B7\\uC2B9\\uC2DC\\uC2DD\\uC2E0\\uC2E3\\uC2E4\\uC2E5\\uC2EB\\uC2EC\\uC2ED\\uC2EF\\uC2F1\\uC2F3\\uC2F6\\uC2F8\\uC2F9\\uC2FB\\uC2FC\\uC2FF\\uC300\\uC308\\uC309\\uC30C\\uC30D\\uC313\\uC314\\uC315\\uC318\\uC31C\\uC324\\uC325\\uC328\\uC329\\uC345\\uC368\\uC369\\uC36C\\uC370\\uC372\\uC378\\uC379\\uC37C\\uC37D\\uC384\\uC388\\uC38C\\uC394\\uC3A0\\uC3A4\\uC3B5\\uC3BC\\uC3C0\\uC3D8\\uC3D9\\uC3DC\\uC3DF\\uC3E0\\uC3E2\\uC3E8\\uC3E9\\uC3ED\\uC3F4\\uC3F5\\uC3F8\\uC3FC\\uC408\\uC410\\uC424\\uC42C\\uC430\\uC434\\uC43C\\uC43D\\uC448\\uC45D\\uC464\\uC465\\uC468\\uC46C\\uC474\\uC475\\uC479\\uC480\\uC490\\uC494\\uC49C\\uC4AC\\uC4B8\\uC4BC\\uC4E9\\uC4F0\\uC4F1\\uC4F4\\uC4F8\\uC4FA\\uC4FF\\uC500\\uC501\\uC503\\uC50C\\uC510\\uC514\\uC51C\\uC528\\uC529\\uC52C\\uC530\\uC538\\uC539\\uC53B\\uC53D\\uC53F\\uC544\\uC545\\uC548\\uC549\\uC54A\\uC54B\\uC54C\\uC54D\\uC54E\\uC553\\uC554\\uC555\\uC557\\uC558\\uC559\\uC55C\\uC55D\\uC55E\\uC560\\uC561\\uC564\\uC568\\uC570\\uC571\\uC573\\uC574\\uC575\\uC57C\\uC57D\\uC580\\uC584\\uC587\\uC58C\\uC58D\\uC58F\\uC591\\uC595\\uC597\\uC598\\uC59C\\uC5A0\\uC5A9\\uC5AC\\uC5AD\\uC5B4\\uC5B5\\uC5B8\\uC5B9\\uC5BB\\uC5BC\\uC5BD\\uC5BE\\uC5C4\\uC5C5\\uC5C6\\uC5C7\\uC5C8\\uC5C9\\uC5CA\\uC5CC\\uC5CE\\uC5D0\\uC5D1\\uC5D4\\uC5D8\\uC5E0\\uC5E1\\uC5E3\\uC5E5\\uC5EC\\uC5ED\\uC5EE\\uC5F0\\uC5F4\\uC5F6\\uC5F7\\uC5FC\\uC5FD\\uC5FE\\uC5FF\\uC600\\uC601\\uC605\\uC606\\uC607\\uC608\\uC60C\\uC60F\\uC610\\uC618\\uC619\\uC61B\\uC61C\\uC61D\\uC624\\uC625\\uC626\\uC628\\uC62B\\uC62C\\uC62D\\uC62E\\uC630\\uC633\\uC634\\uC635\\uC637\\uC639\\uC63B\\uC640\\uC641\\uC644\\uC648\\uC650\\uC651\\uC653\\uC654\\uC655\\uC658\\uC65C\\uC65D\\uC660\\uC66C\\uC66D\\uC66F\\uC670\\uC671\\uC678\\uC679\\uC67C\\uC680\\uC688\\uC689\\uC68B\\uC68D\\uC694\\uC695\\uC698\\uC69C\\uC6A4\\uC6A5\\uC6A7\\uC6A9\\uC6B0\\uC6B1\\uC6B4\\uC6B7\\uC6B8\\uC6B9\\uC6BA\\uC6C0\\uC6C1\\uC6C3\\uC6C5\\uC6C7\\uC6CC\\uC6CD\\uC6D0\\uC6D4\\uC6DC\\uC6DD\\uC6DF\\uC6E0\\uC6E1\\uC6E8\\uC6E9\\uC6EC\\uC6F0\\uC6F8\\uC6F9\\uC6FB\\uC6FD\\uC704\\uC705\\uC708\\uC70C\\uC714\\uC715\\uC717\\uC719\\uC720\\uC721\\uC724\\uC728\\uC730\\uC731\\uC733\\uC735\\uC737\\uC73C\\uC73D\\uC73E\\uC740\\uC744\\uC74A\\uC74C\\uC74D\\uC74F\\uC751\\uC752\\uC753\\uC754\\uC755\\uC756\\uC757\\uC758\\uC75C\\uC760\\uC768\\uC769\\uC76B\\uC76D\\uC774\\uC775\\uC778\\uC77B\\uC77C\\uC77D\\uC77E\\uC783\\uC784\\uC785\\uC787\\uC788\\uC789\\uC78A\\uC78C\\uC78D\\uC78E\\uC790\\uC791\\uC794\\uC796\\uC797\\uC798\\uC79A\\uC7A0\\uC7A1\\uC7A3\\uC7A4\\uC7A5\\uC7A6\\uC7AC\\uC7AD\\uC7B0\\uC7B4\\uC7BC\\uC7BD\\uC7BF\\uC7C0\\uC7C1\\uC7C8\\uC7C9\\uC7CC\\uC7CE\\uC7D0\\uC7D8\\uC7DD\\uC7E4\\uC7E8\\uC7EC\\uC7F5\\uC800\\uC801\\uC804\\uC807\\uC808\\uC809\\uC80A\\uC810\\uC811\\uC813\\uC815\\uC816\\uC81C\\uC81D\\uC820\\uC824\\uC82C\\uC82D\\uC82F\\uC831\\uC838\\uC83C\\uC840\\uC848\\uC849\\uC84C\\uC84D\\uC854\\uC870\\uC871\\uC874\\uC878\\uC87A\\uC880\\uC881\\uC883\\uC885\\uC886\\uC887\\uC88B\\uC88C\\uC88D\\uC894\\uC89D\\uC89F\\uC8A1\\uC8A8\\uC8AC\\uC8BC\\uC8BD\\uC8C4\\uC8C8\\uC8CC\\uC8D4\\uC8D5\\uC8D7\\uC8D9\\uC8E0\\uC8E1\\uC8E4\\uC8F5\\uC8FC\\uC8FD\\uC900\\uC904\\uC905\\uC906\\uC90C\\uC90D\\uC90F\\uC911\\uC918\\uC92C\\uC934\\uC950\\uC951\\uC954\\uC958\\uC960\\uC961\\uC963\\uC96C\\uC970\\uC974\\uC97C\\uC988\\uC989\\uC98C\\uC990\\uC992\\uC998\\uC999\\uC99B\\uC99D\\uC9A4\\uC9C0\\uC9C1\\uC9C4\\uC9C7\\uC9C8\\uC9CA\\uC9D0\\uC9D1\\uC9D3\\uC9D5\\uC9D6\\uC9D9\\uC9DA\\uC9DC\\uC9DD\\uC9E0\\uC9E2\\uC9E3\\uC9E4\\uC9E7\\uC9EC\\uC9ED\\uC9EF\\uC9F0\\uC9F1\\uC9F2\\uC9F8\\uC9F9\\uC9FC\\uCA00\\uCA08\\uCA09\\uCA0B\\uCA0C\\uCA0D\\uCA14\\uCA18\\uCA29\\uCA4C\\uCA4D\\uCA50\\uCA54\\uCA5C\\uCA5D\\uCA5F\\uCA60\\uCA61\\uCA68\\uCA7D\\uCA84\\uCA98\\uCABC\\uCABD\\uCAC0\\uCAC3\\uCAC4\\uCACC\\uCACD\\uCACF\\uCAD1\\uCAD2\\uCAD3\\uCAD8\\uCAD9\\uCAE0\\uCAEC\\uCAF4\\uCB08\\uCB10\\uCB14\\uCB18\\uCB20\\uCB21\\uCB32\\uCB41\\uCB48\\uCB49\\uCB4C\\uCB50\\uCB58\\uCB59\\uCB5D\\uCB64\\uCB78\\uCB79\\uCB93\\uCB9C\\uCBB8\\uCBD4\\uCBE4\\uCBE7\\uCBE9\\uCC0C\\uCC0D\\uCC10\\uCC13\\uCC14\\uCC1C\\uCC1D\\uCC1F\\uCC21\\uCC22\\uCC27\\uCC28\\uCC29\\uCC2C\\uCC2E\\uCC30\\uCC38\\uCC39\\uCC3B\\uCC3C\\uCC3D\\uCC3E\\uCC44\\uCC45\\uCC48\\uCC4C\\uCC54\\uCC55\\uCC57\\uCC58\\uCC59\\uCC60\\uCC64\\uCC66\\uCC68\\uCC70\\uCC75\\uCC98\\uCC99\\uCC9C\\uCCA0\\uCCA8\\uCCA9\\uCCAB\\uCCAC\\uCCAD\\uCCB4\\uCCB5\\uCCB8\\uCCBC\\uCCC4\\uCCC5\\uCCC7\\uCCC9\\uCCD0\\uCCD4\\uCCE4\\uCCEC\\uCCF0\\uCD01\\uCD08\\uCD09\\uCD0C\\uCD10\\uCD18\\uCD19\\uCD1B\\uCD1D\\uCD24\\uCD28\\uCD2C\\uCD39\\uCD40\\uCD5C\\uCD60\\uCD64\\uCD6C\\uCD6D\\uCD6F\\uCD71\\uCD78\\uCD88\\uCD94\\uCD95\\uCD98\\uCD9C\\uCDA4\\uCDA5\\uCDA7\\uCDA9\\uCDB0\\uCDC4\\uCDCC\\uCDCD\\uCDD0\\uCDE8\\uCDEC\\uCDF0\\uCDF8\\uCDF9\\uCDFB\\uCDFD\\uCE04\\uCE08\\uCE0C\\uCE14\\uCE19\\uCE20\\uCE21\\uCE24\\uCE28\\uCE30\\uCE31\\uCE33\\uCE35\\uCE58\\uCE59\\uCE5C\\uCE5F\\uCE60\\uCE61\\uCE62\\uCE68\\uCE69\\uCE6B\\uCE6D\\uCE6E\\uCE70\\uCE74\\uCE75\\uCE78\\uCE7B\\uCE7C\\uCE84\\uCE85\\uCE87\\uCE89\\uCE90\\uCE91\\uCE94\\uCE98\\uCEA0\\uCEA1\\uCEA3\\uCEA4\\uCEA5\\uCEA8\\uCEAC\\uCEAD\\uCEB0\\uCEC1\\uCEC4\\uCEE4\\uCEE5\\uCEE8\\uCEEB\\uCEEC\\uCEF4\\uCEF5\\uCEF7\\uCEF8\\uCEF9\\uCF00\\uCF01\\uCF04\\uCF08\\uCF10\\uCF11\\uCF13\\uCF15\\uCF18\\uCF1C\\uCF20\\uCF24\\uCF2C\\uCF2D\\uCF2F\\uCF30\\uCF31\\uCF38\\uCF54\\uCF55\\uCF58\\uCF5B\\uCF5C\\uCF64\\uCF65\\uCF67\\uCF69\\uCF70\\uCF71\\uCF74\\uCF78\\uCF80\\uCF83\\uCF85\\uCF8C\\uCFA1\\uCFA8\\uCFB0\\uCFC4\\uCFC8\\uCFE0\\uCFE1\\uCFE4\\uCFE8\\uCFF0\\uCFF1\\uCFF3\\uCFF5\\uCFFC\\uCFFD\\uD000\\uD004\\uD00C\\uD011\\uD018\\uD01C\\uD020\\uD02D\\uD034\\uD035\\uD038\\uD03C\\uD044\\uD045\\uD047\\uD049\\uD050\\uD054\\uD058\\uD060\\uD06C\\uD06D\\uD070\\uD072\\uD074\\uD07C\\uD07D\\uD081\\uD084\\uD0A4\\uD0A5\\uD0A8\\uD0AC\\uD0B4\\uD0B5\\uD0B7\\uD0B9\\uD0C0\\uD0C1\\uD0C4\\uD0C7\\uD0C8\\uD0C9\\uD0D0\\uD0D1\\uD0D3\\uD0D4\\uD0D5\\uD0DC\\uD0DD\\uD0E0\\uD0E4\\uD0EC\\uD0ED\\uD0EF\\uD0F0\\uD0F1\\uD0F8\\uD10D\\uD130\\uD131\\uD134\\uD138\\uD13A\\uD13B\\uD140\\uD141\\uD143\\uD144\\uD145\\uD14C\\uD14D\\uD150\\uD154\\uD15C\\uD15D\\uD15F\\uD161\\uD168\\uD16C\\uD17C\\uD184\\uD188\\uD1A0\\uD1A1\\uD1A4\\uD1A7\\uD1A8\\uD1B0\\uD1B1\\uD1B3\\uD1B5\\uD1BA\\uD1BC\\uD1C0\\uD1D8\\uD1F4\\uD1F8\\uD1FB\\uD207\\uD209\\uD210\\uD22C\\uD22D\\uD230\\uD234\\uD236\\uD23C\\uD23D\\uD23F\\uD241\\uD248\\uD25C\\uD264\\uD277\\uD280\\uD281\\uD284\\uD288\\uD290\\uD291\\uD295\\uD29C\\uD2A0\\uD2A4\\uD2AC\\uD2B1\\uD2B8\\uD2B9\\uD2BC\\uD2BF\\uD2C0\\uD2C2\\uD2C8\\uD2C9\\uD2CB\\uD2D4\\uD2D8\\uD2DC\\uD2E4\\uD2E5\\uD2F0\\uD2F1\\uD2F4\\uD2F8\\uD300\\uD301\\uD303\\uD305\\uD30C\\uD30D\\uD30E\\uD310\\uD313\\uD314\\uD316\\uD31C\\uD31D\\uD31F\\uD320\\uD321\\uD324\\uD325\\uD328\\uD329\\uD32C\\uD32F\\uD330\\uD338\\uD339\\uD33B\\uD33C\\uD33D\\uD344\\uD345\\uD37C\\uD37D\\uD380\\uD384\\uD38C\\uD38D\\uD38F\\uD390\\uD391\\uD398\\uD399\\uD39C\\uD3A0\\uD3A8\\uD3A9\\uD3AB\\uD3AD\\uD3B4\\uD3B5\\uD3B8\\uD3BC\\uD3C4\\uD3C5\\uD3C8\\uD3C9\\uD3D0\\uD3D8\\uD3E1\\uD3E3\\uD3EC\\uD3ED\\uD3F0\\uD3F4\\uD3FC\\uD3FD\\uD3FF\\uD401\\uD408\\uD409\\uD41D\\uD440\\uD444\\uD45C\\uD460\\uD464\\uD46D\\uD46F\\uD478\\uD479\\uD47C\\uD47F\\uD480\\uD482\\uD488\\uD489\\uD48B\\uD48D\\uD494\\uD4A9\\uD4CC\\uD4D0\\uD4D4\\uD4DC\\uD4DF\\uD4E8\\uD4EC\\uD4F0\\uD4F8\\uD4FB\\uD4FD\\uD504\\uD508\\uD50C\\uD514\\uD515\\uD517\\uD53C\\uD53D\\uD540\\uD544\\uD54C\\uD54D\\uD54F\\uD551\\uD558\\uD559\\uD55C\\uD560\\uD565\\uD568\\uD569\\uD56B\\uD56D\\uD570\\uD573\\uD574\\uD575\\uD578\\uD57B\\uD57C\\uD584\\uD585\\uD587\\uD588\\uD589\\uD58F\\uD590\\uD594\\uD5A3\\uD5A5\\uD5C8\\uD5C9\\uD5CC\\uD5D0\\uD5D2\\uD5D7\\uD5D8\\uD5D9\\uD5DB\\uD5DD\\uD5E0\\uD5E1\\uD5E3\\uD5E4\\uD5E5\\uD5E8\\uD5EC\\uD5F4\\uD5F5\\uD5F7\\uD5F9\\uD5FF\\uD600\\uD601\\uD604\\uD608\\uD610\\uD611\\uD613\\uD614\\uD615\\uD61C\\uD620\\uD624\\uD62D\\uD638\\uD639\\uD63C\\uD640\\uD645\\uD648\\uD649\\uD64B\\uD64D\\uD651\\uD654\\uD655\\uD658\\uD65C\\uD665\\uD667\\uD668\\uD669\\uD670\\uD671\\uD674\\uD683\\uD685\\uD68C\\uD68D\\uD690\\uD694\\uD69D\\uD69F\\uD6A1\\uD6A8\\uD6AC\\uD6B0\\uD6B9\\uD6BB\\uD6BD\\uD6C4\\uD6C5\\uD6C8\\uD6CC\\uD6D1\\uD6D4\\uD6D5\\uD6D7\\uD6D9\\uD6E0\\uD6E4\\uD6E8\\uD6F0\\uD6F5\\uD6FC\\uD6FD\\uD700\\uD704\\uD711\\uD718\\uD719\\uD71C\\uD720\\uD728\\uD729\\uD72B\\uD72D\\uD734\\uD735\\uD738\\uD73C\\uD744\\uD747\\uD749\\uD750\\uD751\\uD754\\uD756\\uD757\\uD758\\uD759\\uD75D\\uD760\\uD761\\uD763\\uD765\\uD769\\uD76C\\uD770\\uD774\\uD77C\\uD77D\\uD781\\uD788\\uD789\\uD78C\\uD790\\uD798\\uD799\\uD79B\\uD79D\\uD7A3"
        const val HANGUL_FILTER_2350 =
            "가각간갇갈갉갊갋감갑값갓갔강갖갗같갚갛개객갠갣갤갬갭갯갰갱갸갹갼걀걋걍걔걘걜걥거걱건걷걸걺검겁것겄겅겆겉겊겋게겐겔겜겝겟겠겡겨격겪견겯결겷겸겹겻겼경곁계곈곌곕곗고곡곤곧골곪곬곯곰곱곳공곶과곽관괄괆괌괍괏괐광괘괜괠괢괩괬괭괴괵괸괼굄굅굇굉교굔굘굠굡굣굥구국군굳굴굵굶굻굼굽굿궁궂궈궉권궐궜궝궤궷궸귀귁귄귈귐귑귓귕규균귤귬그극근귿글긁긂긇금급긋긍긓긔기긱긴긷길긺김깁깃깄깅깆깊" +
                "까깍깎깐깔깖깜깝깟깠깡깥깨깩깬깯깰깸깹깻깼깽꺄꺅꺆꺌꺍꺼꺽꺾껀껄껌껍껏껐껑껓껕께껙껜껨껫껭껴껸껼꼇꼈꼉꼍꼐꼬꼭꼰꼲꼳꼴꼼꼽꼿꽁꽂꽃꽅꽈꽉꽐꽜꽝꽤꽥꽸꽹꾀꾄꾈꾐꾑꾕꾜꾸꾹꾼꿀꿇꿈꿉꿋꿍꿎꿔꿘꿜꿨꿩꿰꿱꿴꿸뀀뀁뀄뀌뀐뀔뀜뀝뀨뀰뀼끄끅끈끊끌끎끓끔끕끗끙끝끼끽낀낄낌낍낏낑" +
                "나낙낚난낟날낡낢남납낫났낭낮낯낱낳내낵낸낻낼냄냅냇냈냉냐냑냔냗냘냠냡냣냥냬너넉넋넌넏널넒넓넘넙넛넜넝넢넣네넥넨넫넬넴넵넷넸넹녀녁년녇녈념녑녔녕녘녜녠녱노녹논놀놁놂놈놉놋농놑높놓놔놘놜놨놰뇄뇌뇐뇔뇜뇝뇟뇡뇨뇩뇬뇰뇸뇹뇻뇽누눅눈눋눌눍눔눕눗눙눝눠눴눼뉘뉜뉠뉨뉩뉴뉵뉻뉼늄늅늉느늑는늗늘늙늚늠늡늣능늦늧늪늬늰늴늼닁니닉닌닏닐닒님닙닛닝닢" +
                "다닥닦단닫달닭닮닯닳담답닷닸당닺닻닽닿대댁댄댈댐댑댓댔댕댜댠더덕덖던덛덜덞덟덤덥덧덩덫덮데덱덴델뎀뎁뎃뎄뎅뎌뎐뎔뎠뎡뎨뎬도독돈돋돌돎돐돔돕돗동돛돝돠돤돨돼됐되된될됨됩됫됭됴두둑둔둗둘둚둠둡둣둥둬뒀뒈뒙뒝뒤뒨뒬뒵뒷뒹듀듄듈듐듕드득든듣들듦듬듭듯등듸디딕딘딛딜딤딥딧딨딩딪딮" +
                "따딱딴딷딸땀땁땃땄땅땋때땍땐땔땜땝땟땠땡떠떡떤떨떪떫떰떱떳떴떵떻떼떽뗀뗄뗌뗍뗏뗐뗑뗘뗬또똑똔똘똠똡똣똥똬똭똰똴뙇뙈뙜뙤뙨뚜뚝뚠뚤뚧뚫뚬뚱뛔뛰뛴뛸뜀뜁뜅뜨뜩뜬뜯뜰뜳뜸뜹뜻뜽뜾띄띈띌띔띕띠띤띨띰띱띳띵" +
                "라락란랃랄람랍랏랐랑랒랖랗래랙랜랟랠램랩랫랬랭랲랴략랸럇량럔러럭런럲럳럴럼럽럿렀렁렇레렉렌렐렘렙렛렜렝려력련렫렬렴렵렷렸령례롄롑롓로록론롣롤롬롭롯롱롸롹롼뢍뢔뢨뢰뢴뢸룀룁룃룅료룐룔룝룟룡루룩룬룰룸룹룻룽뤄뤘뤠뤤뤼뤽륀륄륌륏륑류륙륜률륨륩륫륭르륵른를름릅릇릉릊릍릎리릭린릴림립릿링" +
                "마막만많맏말맑맒맘맙맛맜망맞맟맡맣매맥맨맫맬맴맵맷맸맹맺먀먁먄먈먕머먹먼멀멂멈멉멋멍멎멓메멕멘멜멤멥멧멨멩며멱면멸몃몄명몇몌모목몫몬몰몱몲몸몹못몽뫄뫈뫘뫙뫠뫴뫼묀묄묍묏묑묘묜묠묩묫무묵묶문묻물묽묾뭄뭅뭇뭉뭍뭏뭐뭔뭘뭡뭣뭥뭬뮈뮊뮌뮐뮤뮨뮬뮴뮷뮹므믁믄믈믐믓믕믜미믹민믿밀밂밈밉밋밌밍및밑" +
                "바박밖밗반받발밝밞밟밤밥밧방밭배백밴밷밸뱀뱁뱃뱄뱅뱉뱌뱍뱐뱜뱝뱡버벅번벋벌벎범법벗벙벚베벡벤벧벨벰벱벳벴벵벼벽변별볌볍볏볐병볕볘볜보복볶본볻볼볿봄봅봇봉봐봔봤봥봬뵀뵈뵉뵌뵐뵘뵙뵤뵨뵴부북분붇불붉붊붐붑붓붕붙붚붜붠붤붰붴붸뷁뷔뷕뷘뷜뷩뷰뷴뷸븀븃븅브븍븐블븜븝븟븡븨비빅빈빋빌빎빔빕빗빙빚빛" +
                "빠빡빤빧빨빪빰빱빳빴빵빻빼빽뺀뺄뺌뺍뺏뺐뺑뺘뺙뺜뺨뻐뻑뻔뻗뻘뻠뻣뻤뻥뻬뼁뼈뼉뼘뼙뼛뼜뼝뽀뽁뽄뽈뽐뽑뽓뽕뾔뾰뾱뿅뿌뿍뿐뿔뿕뿜뿝뿟뿡쀠쀼쁑쁘쁜쁠쁨쁩쁭삐삑삔삘삠삡삣삥" +
                "사삭삯산삳살삵삶삼삽삿샀상샅새색샌샏샐샘샙샛샜생샤샥샨샬샴샵샷샹샾섀섁섄섈섐섕서석섞섟선섣설섦섧섬섭섯섰성섶세섹센섿셀셈셉셋셌셍셔셕션셜셤셥셧셨셩셰셱셴셸솀솁솅소속솎손솓솔솖솜솝솟송솥솨솩솬솰솽쇄쇈쇌쇔쇗쇘쇠쇤쇨쇰쇱쇳쇵쇼쇽숀숄숌숍숏숑숖수숙순숟술숨숩숫숭숯숱숲숴쉈쉐쉑쉔쉘쉠쉥쉬쉭쉰쉴쉼쉽쉿슁슈슉슌슐슘슛슝스슥슨슬슭슴습슷승시식신싣실싥싫심십싯싱싳싶" +
                "싸싹싻싼싿쌀쌈쌉쌌쌍쌓쌔쌕쌘쌜쌤쌥쌨쌩썅써썩썬썰썲썸썹썼썽쎄쎈쎌쎔쎠쎤쎵쎼쏀쏘쏙쏜쏟쏠쏢쏨쏩쏭쏴쏵쏸쏼쐈쐐쐤쐬쐰쐴쐼쐽쑈쑝쑤쑥쑨쑬쑴쑵쑹쒀쒐쒔쒜쒬쒸쒼쓩쓰쓱쓴쓸쓺쓿씀씁씃씌씐씔씜씨씩씬씰씸씹씻씽씿" +
                "아악안앉않앋알앍앎앓암압앗았앙앜앝앞애액앤앨앰앱앳앴앵야약얀얄얇얌얍얏양얕얗얘얜얠얩얬얭어억언얹얻얼얽얾엄업없엇었엉엊엌엎에엑엔엘엠엡엣엥여역엮연열엶엷염엽엾엿였영옅옆옇예옌옏옐옘옙옛옜옝오옥옦온옫올옭옮옰옳옴옵옷옹옻와왁완왈왐왑왓왔왕왘왜왝왠왬왭왯왰왱외왹왼욀욈욉욋욍요욕욘욜욤욥욧용우욱운욷울욹욺움웁웃웅웇워웍원월웜웝웟웠웡웨웩웬웰웸웹웻웽위윅윈윌윔윕윗윙유육윤율윰윱윳융윷으윽윾은을읊음읍읏응읒읓읔읕읖읗의읜읠읨읩읫읭이익인읻일읽읾잃임입잇있잉잊잌잍잎" +
                "자작잔잖잗잘잚잠잡잣잤장잦재잭잰잴잼잽잿쟀쟁쟈쟉쟌쟎쟐쟘쟝쟤쟨쟬쟵저적전젇절젉젊점접젓정젖제젝젠젤젬젭젯젱져젼졀졈졉졌졍졔조족존졸졺좀좁좃종좆좇좋좌좍좔좝좟좡좨좬좼좽죄죈죌죔죕죗죙죠죡죤죵주죽준줄줅줆줌줍줏중줘줬줴쥐쥑쥔쥘쥠쥡쥣쥬쥰쥴쥼즈즉즌즐즒즘즙즛증즤지직진짇질짊짐집짓징짖짙짚" +
                "짜짝짠짢짣짤짧짬짭짯짰짱짲째짹짼쨀쨈쨉쨋쨌쨍쨔쨘쨩쩌쩍쩐쩔쩜쩝쩟쩠쩡쩨쩽쪄쪘쪼쪽쫀쫃쫄쫌쫍쫏쫑쫒쫓쫘쫙쫠쫬쫴쬈쬐쬔쬘쬠쬡쬲쭁쭈쭉쭌쭐쭘쭙쭝쭤쭸쭹쮓쮜쮸쯔쯤쯧쯩찌찍찐찓찔찜찝찟찡찢찧" +
                "차착찬찮찰참찹찻찼창찾채책챈챌챔챕챗챘챙챠챤챦챨챰챵처척천철첨첩첫첬청체첵첸첼쳄쳅쳇쳉쳐쳔쳤쳬쳰촁초촉촌촐촘촙촛총촤촨촬촹쵀최쵠쵤쵬쵭쵯쵱쵸춈추축춘출춤춥춧충춰췄췌췍췐취췬췰췸췹췻췽츄츈츌츔츙츠측츤츨츰츱츳층치칙친칟칠칡칢침칩칫칭칮칰" +
                "카칵칸칻칼캄캅캇캉캐캑캔캘캠캡캣캤캥캨캬캭캰컁컄커컥컨컫컬컴컵컷컸컹케켁켄켈켐켑켓켕켘켜켠켤켬켭켯켰켱켸코콕콘콛콜콤콥콧콩콰콱콴콸쾀쾃쾅쾌쾡쾨쾰쿄쿈쿠쿡쿤쿨쿰쿱쿳쿵쿼쿽퀀퀄퀌퀑퀘퀜퀠퀭퀴퀵퀸퀼큄큅큇큉큐큔큘큠크큭큰큲클큼큽킁킄키킥킨킬킴킵킷킹" +
                "타탁탄탇탈탉탐탑탓탔탕태택탠탤탬탭탯탰탱탸턍터턱턴털턺턻텀텁텃텄텅테텍텐텔템텝텟텡텨텬텼톄톈토톡톤톧톨톰톱톳통톺톼퇀퇘퇴퇸퇻툇툉툐투툭툰툴툶툼툽툿퉁퉈퉜퉤퉷튀튁튄튈튐튑튕튜튠튤튬튱트특튼튿틀틂틈틉틋틔틘틜틤틥티틱틴틸팀팁팃팅" +
                "파팍팎판팓팔팖팜팝팟팠팡팤팥패팩팬팯팰팸팹팻팼팽퍄퍅퍼퍽펀펄펌펍펏펐펑페펙펜펠펨펩펫펭펴펵편펼폄폅폈평폐폘폡폣포폭폰폴폼폽폿퐁퐈퐉퐝푀푄표푠푤푭푯푸푹푼푿풀풂품풉풋풍풔풩퓌퓐퓔퓜퓟퓨퓬퓰퓸퓻퓽프픈플픔픕픗피픽핀필핌핍핏핑" +
                "하학한할핥함합핫항핰핳해핵핸핻핼햄햅햇했행햏햐햔햣향허헉헌헐헒헗험헙헛헝헠헡헣헤헥헨헬헴헵헷헹헿혀혁현혈혐협혓혔형혜혠혤혭호혹혼홀홅홈홉홋홍홑화확환활홥홧홨황홰홱홴횃횅회획횐횔횝횟횡효횬횰횹횻횽후훅훈훌훑훔훕훗훙훠훤훨훰훵훼훽휀휄휑휘휙휜휠휨휩휫휭휴휵휸휼흄흇흉흐흑흔흖흗흘흙흝흠흡흣흥흩희흰흴흼흽힁히힉힌힐힘힙힛힝힣"

        // 문자 중 '문서 내 사용'하는 문자만 추출
        const val HANGUL_FILTER_DOCUMENT =
            "가각간갇갈갉갊감갑값갓갔강갖갗같갚갛개객갠갤갬갭갯갰갱갸갹갼걀걋걍걔걘걜거걱건걷걸걺검겁것겄겅겆겉겊겋게겐겔겜겝겟겠겡겨격겪견겯결겸겹겻겼경곁계곈곌곕곗고곡곤곧골곪곬곯곰곱곳공곶과곽관괄괆괌괍괏광괘괜괠괩괬괭괴괵괸괼굄굅굇굉교굔굘굡굣구국군굳굴굵굶굻굼굽굿궁궂궈궉권궐궜궝궤궷귀귁귄귈귐귑귓규균귤그극근귿글긁금급긋긍긔기긱긴긷길긺김깁깃깅깆깊" +
                "까깍깎깐깔깖깜깝깟깠깡깥깨깩깬깰깸깹깻깼깽꺄꺅꺌꺼꺽꺾껀껄껌껍껏껐껑께껙껜껨껫껭껴껸껼꼇꼈꼍꼐꼬꼭꼰꼲꼴꼼꼽꼿꽁꽂꽃꽈꽉꽐꽜꽝꽤꽥꽹꾀꾄꾈꾐꾑꾕꾜꾸꾹꾼꿀꿇꿈꿉꿋꿍꿎꿔꿜꿨꿩꿰꿱꿴꿸뀀뀁뀄뀌뀐뀔뀜뀝뀨끄끅끈끊끌끎끓끔끕끗끙끝끼끽낀낄낌낍낏낑" +
                "나낙낚난낟날낡낢남납낫났낭낮낯낱낳내낵낸낼냄냅냇냈냉냐냑냔냘냠냥너넉넋넌널넒넓넘넙넛넜넝넣네넥넨넬넴넵넷넸넹녀녁년녈념녑녔녕녘녜녠노녹논놀놂놈놉놋농높놓놔놘놜놨뇌뇐뇔뇜뇝뇟뇨뇩뇬뇰뇹뇻뇽누눅눈눋눌눔눕눗눙눠눴눼뉘뉜뉠뉨뉩뉴뉵뉼늄늅늉느늑는늘늙늚늠늡늣능늦늪늬늰늴니닉닌닐닒님닙닛닝닢" +
                "다닥닦단닫달닭닮닯닳담답닷닸당닺닻닿대댁댄댈댐댑댓댔댕댜더덕덖던덛덜덞덟덤덥덧덩덫덮데덱덴델뎀뎁뎃뎄뎅뎌뎐뎔뎠뎡뎨뎬도독돈돋돌돎돐돔돕돗동돛돝돠돤돨돼됐되된될됨됩됫됴두둑둔둘둠둡둣둥둬뒀뒈뒝뒤뒨뒬뒵뒷뒹듀듄듈듐듕드득든듣들듦듬듭듯등듸디딕딘딛딜딤딥딧딨딩딪" +
                "따딱딴딸땀땁땃땄땅땋때땍땐땔땜땝땟땠땡떠떡떤떨떪떫떰떱떳떴떵떻떼떽뗀뗄뗌뗍뗏뗐뗑뗘뗬또똑똔똘똥똬똴뙈뙤뙨뚜뚝뚠뚤뚫뚬뚱뛔뛰뛴뛸뜀뜁뜅뜨뜩뜬뜯뜰뜸뜹뜻띄띈띌띔띕띠띤띨띰띱띳띵" +
                "라락란랄람랍랏랐랑랒랖랗래랙랜랠램랩랫랬랭랴략랸럇량러럭런럴럼럽럿렀렁렇레렉렌렐렘렙렛렝려력련렬렴렵렷렸령례롄롑롓로록론롤롬롭롯롱롸롼뢍뢨뢰뢴뢸룀룁룃룅료룐룔룝룟룡루룩룬룰룸룹룻룽뤄뤘뤠뤼뤽륀륄륌륏륑류륙륜률륨륩륫륭르륵른를름릅릇릉릊릍릎리릭린릴림립릿링" +
                "마막만많맏말맑맒맘맙맛망맞맡맣매맥맨맬맴맵맷맸맹맺먀먁먈먕머먹먼멀멂멈멉멋멍멎멓메멕멘멜멤멥멧멨멩며멱면멸몃몄명몇몌모목몫몬몰몲몸몹못몽뫄뫈뫘뫙뫼묀묄묍묏묑묘묜묠묩묫무묵묶문묻물묽묾뭄뭅뭇뭉뭍뭏뭐뭔뭘뭡뭣뭬뮈뮌뮐뮤뮨뮬뮴뮷므믄믈믐믓미믹민믿밀밂밈밉밋밌밍및밑" +
                "바박밖밗반받발밝밞밟밤밥밧방밭배백밴밸뱀뱁뱃뱄뱅뱉뱌뱍뱐뱝버벅번벋벌벎범법벗벙벚베벡벤벧벨벰벱벳벴벵벼벽변별볍볏볐병볕볘볜보복볶본볼봄봅봇봉봐봔봤봬뵀뵈뵉뵌뵐뵘뵙뵤뵨부북분붇불붉붊붐붑붓붕붙붚붜붤붰붸뷔뷕뷘뷜뷩뷰뷴뷸븀븃븅브븍븐블븜븝븟비빅빈빌빎빔빕빗빙빚빛" +
                "빠빡빤빨빪빰빱빳빴빵빻빼빽뺀뺄뺌뺍뺏뺐뺑뺘뺙뺨뻐뻑뻔뻗뻘뻠뻣뻤뻥뻬뼁뼈뼉뼘뼙뼛뼜뼝뽀뽁뽄뽈뽐뽑뽕뾔뾰뿅뿌뿍뿐뿔뿜뿟뿡쀼쁑쁘쁜쁠쁨쁩삐삑삔삘삠삡삣삥" +
                "사삭삯산삳살삵삶삼삽삿샀상샅새색샌샐샘샙샛샜생샤샥샨샬샴샵샷샹섀섄섈섐섕서석섞섟선섣설섦섧섬섭섯섰성섶세섹센셀셈셉셋셌셍셔셕션셜셤셥셧셨셩셰셴셸솅소속솎손솔솖솜솝솟송솥솨솩솬솰솽쇄쇈쇌쇔쇗쇘쇠쇤쇨쇰쇱쇳쇼쇽숀숄숌숍숏숑수숙순숟술숨숩숫숭숯숱숲숴쉈쉐쉑쉔쉘쉠쉥쉬쉭쉰쉴쉼쉽쉿슁슈슉슐슘슛슝스슥슨슬슭슴습슷승시식신싣실싫심십싯싱싶" +
                "싸싹싻싼쌀쌈쌉쌌쌍쌓쌔쌕쌘쌜쌤쌥쌨쌩썅써썩썬썰썲썸썹썼썽쎄쎈쎌쏀쏘쏙쏜쏟쏠쏢쏨쏩쏭쏴쏵쏸쐈쐐쐤쐬쐰쐴쐼쐽쑈쑤쑥쑨쑬쑴쑵쑹쒀쒔쒜쒸쒼쓩쓰쓱쓴쓸쓺쓿씀씁씌씐씔씜씨씩씬씰씸씹씻씽" +
                "아악안앉않알앍앎앓암압앗았앙앝앞애액앤앨앰앱앳앴앵야약얀얄얇얌얍얏양얕얗얘얜얠얩어억언얹얻얼얽얾엄업없엇었엉엊엌엎에엑엔엘엠엡엣엥여역엮연열엶엷염엽엾엿였영옅옆옇예옌옐옘옙옛옜오옥온올옭옮옰옳옴옵옷옹옻와왁완왈왐왑왓왔왕왜왝왠왬왯왱외왹왼욀욈욉욋욍요욕욘욜욤욥욧용우욱운울욹욺움웁웃웅워웍원월웜웝웠웡웨웩웬웰웸웹웽위윅윈윌윔윕윗윙유육윤율윰윱윳융윷으윽은을읊음읍읏응읒읓읔읕읖읗의읜읠읨읫이익인일읽읾잃임입잇있잉잊잎" +
                "자작잔잖잗잘잚잠잡잣잤장잦재잭잰잴잼잽잿쟀쟁쟈쟉쟌쟎쟐쟘쟝쟤쟨쟬저적전절젊점접젓정젖제젝젠젤젬젭젯젱져젼졀졈졉졌졍졔조족존졸졺좀좁좃종좆좇좋좌좍좔좝좟좡좨좼좽죄죈죌죔죕죗죙죠죡죤죵주죽준줄줅줆줌줍줏중줘줬줴쥐쥑쥔쥘쥠쥡쥣쥬쥰쥴쥼즈즉즌즐즘즙즛증지직진짇질짊짐집짓징짖짙짚" +
                "짜짝짠짢짤짧짬짭짯짰짱째짹짼쨀쨈쨉쨋쨌쨍쨔쨘쨩쩌쩍쩐쩔쩜쩝쩟쩠쩡쩨쩽쪄쪘쪼쪽쫀쫄쫌쫍쫏쫑쫓쫘쫙쫠쫬쫴쬈쬐쬔쬘쬠쬡쭁쭈쭉쭌쭐쭘쭙쭝쭤쭸쭹쮜쮸쯔쯤쯧쯩찌찍찐찔찜찝찡찢찧" +
                "차착찬찮찰참찹찻찼창찾채책챈챌챔챕챗챘챙챠챤챦챨챰챵처척천철첨첩첫첬청체첵첸첼쳄쳅쳇쳉쳐쳔쳤쳬쳰촁초촉촌촐촘촙촛총촤촨촬촹최쵠쵤쵬쵭쵯쵱쵸춈추축춘출춤춥춧충춰췄췌췐취췬췰췸췹췻췽츄츈츌츔츙츠측츤츨츰츱츳층치칙친칟칠칡침칩칫칭" +
                "카칵칸칼캄캅캇캉캐캑캔캘캠캡캣캤캥캬캭컁커컥컨컫컬컴컵컷컸컹케켁켄켈켐켑켓켕켜켠켤켬켭켯켰켱켸코콕콘콜콤콥콧콩콰콱콴콸쾀쾅쾌쾡쾨쾰쿄쿠쿡쿤쿨쿰쿱쿳쿵쿼퀀퀄퀑퀘퀭퀴퀵퀸퀼큄큅큇큉큐큔큘큠크큭큰클큼큽킁키킥킨킬킴킵킷킹" +
                "타탁탄탈탉탐탑탓탔탕태택탠탤탬탭탯탰탱탸턍터턱턴털턺텀텁텃텄텅테텍텐텔템텝텟텡텨텬텼톄톈토톡톤톨톰톱톳통톺톼퇀퇘퇴퇸툇툉툐투툭툰툴툼툽툿퉁퉈퉜퉤튀튁튄튈튐튑튕튜튠튤튬튱트특튼튿틀틂틈틉틋틔틘틜틤틥티틱틴틸팀팁팃팅" +
                "파팍팎판팔팖팜팝팟팠팡팥패팩팬팰팸팹팻팼팽퍄퍅퍼퍽펀펄펌펍펏펐펑페펙펜펠펨펩펫펭펴편펼폄폅폈평폐폘폡폣포폭폰폴폼폽폿퐁퐈퐝푀푄표푠푤푭푯푸푹푼푿풀풂품풉풋풍풔풩퓌퓐퓔퓜퓟퓨퓬퓰퓸퓻퓽프픈플픔픕픗피픽핀필핌핍핏핑" +
                "하학한할핥함합핫항해핵핸핼햄햅햇했행햐향허헉헌헐헒험헙헛헝헤헥헨헬헴헵헷헹혀혁현혈혐협혓혔형혜혠혤혭호혹혼홀홅홈홉홋홍홑화확환활홧황홰홱홴횃횅회획횐횔횝횟횡효횬횰횹횻후훅훈훌훑훔훗훙훠훤훨훰훵훼훽휀휄휑휘휙휜휠휨휩휫휭휴휵휸휼흄흇흉흐흑흔흖흗흘흙흠흡흣흥흩희흰흴흼흽힁히힉힌힐힘힙힛힝"

        // 문자 중 '한글 조합 중'인 문자만 추출
        const val HANGUL_FILTER_AUTOMATA =
            "갋갣걥겷괐괞괢굠굥궴궸귕귬긂긇긓긜깄" +
                "깯꺆꺍꺠껓껕꼉꼳꽅꽸꾤꾺꿘꿹꿿뀰뀼끨낻깯꺆꺍꺠껓껕꼉꼳꽅꽸꾤꾺꿘꿹꿿뀰뀼끨" +
                "낻냗냡냣냬넏넢넫녇녱놁놑놰뇄뇡뇸눍눝뉌뉍뉑뉻늗늧늼닁닏" +
                "닽댠댤댬댱됀됏됑됬됭둗둚뒙딮" +
                "딷땨떄떙똠똡똣똭똰뙇뙜뙝뚧뜛뜳뜽뜾띡" +
                "랃랟랲랼럔럲럳렜렫롣롹뢀뢔뤤" +
                "맜맟맫먄몱뫠뫴뭥뮊뮹믁믕믜" +
                "밷뱜뱡벩볌볻볾볿봣봥봵뵴뵹붠붴붹뷁븡븨빋빧뺜뺭뺴뻵뽓뽜뽱뾱뾸뿕쀍쀠쁌쁭" +
                "샏샾섁섿셂셱솀솁솓쇵숖쉅쉟슌슙슠싥싳" +
                "싿쌰쌸썌썻쎔쎕쎙쎠쎤쎵쎼쏼쑐쑝쒐쒝쒬쓍쓔쓘씃씡씿" +
                "앋앜앻얬얭옏옝옦옫왘왤왭왰왴욷웇웟웱웻윾읃읎읩읭읶읻잌잍" +
                "쟙쟵젇젉좐좬죨줵줸쥥즁즒즤즨짣짲쨜쨰쫃쫒쬬쬲쮓쯥찓찟" +
                "쳡쵀쵝췍췝췟츕칢칮칰" +
                "칻캌캨캮캰캴컄켘콛쾃쿈쿜쿽퀌퀜퀠큮큲큿킄" +
                "탇턻톧툶퉷팓팤팯펵퐉풰" +
                "핰핱핳핻햏햔햝햣헗헠헡헣헳헿홤홥홨횽훃훕흝힣"
    }
}
