package com.snc.zero.core.extensions.random

import java.util.*
import kotlin.jvm.Throws
import kotlin.math.ln

@Throws(IllegalFormatException::class)
fun <T> List<T>.getRandomItem(): T {
    val max = size - 1
    val min = 0
    val index = SplittableRandom().nextInt(max - min + 1) + min
    return get(index)
}

fun <T> MutableMap<T, Double>.getWeightedRandom(): T? {
    var result: T? = null
    var bestValue = Double.MAX_VALUE
    val rand = SplittableRandom()
    for (element in this.keys) {
        val value = -ln(rand.nextDouble()) / this.getOrDefault(element, 0.0)
        if (value < bestValue) {
            bestValue = value
            result = element
        }
    }
    return result
}

fun randomInt(min: Int = 0, bound: Int): Int {
    val random = SplittableRandom()
    return random.nextInt(bound) + min
}

fun randomString(
    length: Int,
    isDigit: Boolean = false,
    isUpperCase: Boolean = false,
    isLowerCase: Boolean = false,
    isLetter: Boolean = false,
    isHangul: Boolean = false
): String {
    val lowercase = ('a'..'z')
    val uppercase = ('A'..'Z')
    val digit = ('0'..'9')
    val hangul = "가각간갇갈감갑갓강갖갛거걱건걷걸검겁것겅겆겋고곡곤곧골곰곱곳공곶구국군굳굴굼굽굿궁궂그극근귿글금급긋긍기긱긴긷길김깁깃깅깆나낙난낟날남납낫낭낮낳너넉넌널넘넙넛넝넣노녹논놀놈놉놋농놓누눅눈눋눌눔눕눗눙느늑는늘늠늡늣능늦니닉닌닐님닙닛닝다닥단닫달담답닷당닺닿더덕던덛덜덤덥덧덩도독돈돋돌돔돕돗동두둑둔둘둠둡둣둥드득든듣들듬듭듯등디딕딘딛딜딤딥딧딩딪라락란랄람랍랏랑랒랗러럭런럴럼럽럿렁렇로록론롤롬롭롯롱루룩룬룰룸룹룻룽르륵른를름릅릇릉릊리릭린릴림립릿링마막만맏말맘맙맛망맞맣머먹먼멀멈멉멋멍멎멓모목몬몰몸몹못몽무묵문묻물뭄뭅뭇뭉뭏므믄믈믐믓미믹민믿밀밈밉밋밍바박반받발밤밥밧방버벅번벋벌범법벗벙벚보복본볼봄봅봇봉부북분붇불붐붑붓붕브븍븐블븜븝븟비빅빈빌빔빕빗빙빚사삭산삳살삼삽삿상서석선섣설섬섭섯성소속손솔솜솝솟송수숙순숟술숨숩숫숭스슥슨슬슴습슷승시식신싣실심십싯싱아악안알암압앗앙어억언얻얼엄업엇엉엊엌오옥온올옴옵옷옹우욱운울움웁웃웅으윽은을음읍읏응읒읔읗이익인일임입잇잉잊자작잔잗잘잠잡잣장잦저적전절점접젓정젖조족존졸좀좁좃종좆좋주죽준줄줌줍줏중즈즉즌즐즘즙즛증지직진짇질짐집짓징짖차착찬찰참찹찻창찾처척천철첨첩첫청초촉촌촐촘촙촛총추축춘출춤춥춧충츠측츤츨츰츱츳층치칙친칟칠침칩칫칭카칵칸칼캄캅캇캉커컥컨컫컬컴컵컷컹코콕콘콜콤콥콧콩쿠쿡쿤쿨쿰쿱쿳쿵크큭큰클큼큽킁키킥킨킬킴킵킷킹타탁탄탈탐탑탓탕터턱턴털텀텁텃텅토톡톤톨톰톱톳통투툭툰툴툼툽툿퉁트특튼튿틀틈틉틋티틱틴틸팀팁팃팅파팍판팔팜팝팟팡퍼퍽펀펄펌펍펏펑포폭폰폴폼폽폿퐁푸푹푼푿풀품풉풋풍프픈플픔픕픗피픽핀필핌핍핏핑하학한할함합핫항허헉헌헐험헙헛헝호혹혼홀홈홉홋홍후훅훈훌훔훗훙흐흑흔흗흘흠흡흣흥히힉힌힐힘힙힛힝"

    val charsets = mutableListOf<Char>()
    if (isLowerCase || isLetter) {
        charsets.addAll(lowercase)
    }
    if (isUpperCase || isLetter) {
        charsets.addAll(uppercase)
    }
    if (isDigit) {
        charsets.addAll(digit)
    }
    if (isHangul) {
        for (c in hangul.toCharArray()) {
            charsets.add(c)
        }
    }
    if (charsets.isEmpty()) {
        charsets.addAll(lowercase)
        charsets.addAll(uppercase)
        charsets.addAll(digit)
    }
    return (1..length)
        .map { charsets.random() }
        .joinToString("")
}
