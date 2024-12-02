package com.snc.test.markdown

import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.markdown.MarkdownConverter
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Test

private val logger = TLogging.logger { }

@Suppress("NonAsciiCharacters")
class MarkdownConverterTest : BaseJUnit5Test() {

    @Test
    fun `마크다운 to 마크업`() {
        val converter = MarkdownConverter()
        val markdown = """
# 마크다운 문법 (Markdown Syntax)

---
## 헤더 Headings

# 부(parts)에 사용됨.
## 장(chapters)에 사용됨.
### 페이지 섹션에 사용함.
#### 하위 섹션에 사용됨.
##### 하위 섹션 아래의 하위 섹션에 사용됨.
###### 문단에


---
## 강조 (Emphasis)

이것은 *이탤릭* 이고, 이것은 **볼드** 이고, ***이텔릭+두껍게*** 입니다.
다음은 _이탤릭_ 이고, 이것은 __볼드__ 이고, ___이텔릭+두껍게___ 입니다.


---
## 특수문자

\*, \`,  \\


---
## 수평 규칙 (Horizontal Rule)

하이픈(Hyphen) 3자이상


---
## 코드 블록 (Code blocks)

```linux
curl -I www.naver.com
```

```css
#button {
  border: none;
}
```

~~~~
This is a 
piece of code 
in a block
~~~~


---
## 인용구 (Blockquotes)

> Blockquotes can also be nested...
>> ...by using additional greater-than signs right next to each other...
> > > ...or with spaces between arrows.
> * Quoted 1
> * Quoted 2
> * List 1
> * List 2


> https://claude.ai
>
> https://chatgpt.com/


---
## 목록

1. 첫번째 아이템
2. 두번째 아이템
#. 세번째 아이템

- 목록 항목 1
- 목록 항목 2
- 목록 항목 3

* 목록 항목 1
* 목록 항목 2
* 목록 항목 3

            
- 앱 이름  (필수)
    - Google Play에 표시되는 앱 이름입니다.  최대 30자까지 입력 가능합니다.

- 간단한 설명 (필수)
    - 간단한 앱 설명입니다. 사용자가 뷰를 펼치면 자세한 설명을 볼 수 있습니다. 최대 80자까지 입력 가능합니다.

- 자세한 설명
    - 앱 상세 설명입니다. 최대 4000 자까지 입력 가능합니다.

1. 구글 플레이 콘솔 접속 & 로그인
2. 왼쪽 메뉴 중 [보고서 다운로드] - [통계] 클릭
3. 오른쪽 위 셀렉트 박스에서 조회할 앱을 선택한다.
4. 조회할 연도와 월을 선택한 후 '개요' 오른쪽의 다운로드 버튼을 클릭한다. 그럼 엑셀을 내려받을 수 있다.
5. 위에서 다운로드한 엑셀 파일은 아래와 같이 일별 통계가 나와있다.
6. 누적 사용자 구하기

문제는 위의 엑셀에 보면 'Total User Installs'라는 항목이 모두 0으로 노출되고 있다는 것이다.


1) 메일 보내기
받는사람: googleplay-developer-support@google.com

2) 회신
이메일을 보내 주셔서 감사합니다. 현재 개발자 고객센터에 있는 문의 양식을 통해 제출한 이메일에만 답변을 보내드릴 수 있습니다.

3) 다시 개발자 고객센터로 문의 --> 답변
RE:[8-4067000022548] Google Play에 관한 메싯지


---
## 이미지 (Images)

이미지를 연결 하려면 다음과 같이하십시오.
[Imgur](https://i.imgur.com/4EjEpQX.png)

이미지를 임베드하려면 다음과 같이하십시오.
![Imgur](https://i.imgur.com/Esghlhd.png)

내부용 이미지를 연결하려면 다음과 같이하십시오.
Format: [Alt Text](Media/images/figure-reference.png)

내부용 이미지를 임베드하려면 다음과 같이하십시오.
Format: ![Alt Text](Media/images/figure-reference.png)


---
## 링크 (Links)

[anys4u](https://anys4u.com)

[마크다운 샘플](doc-markdown-sample.html)
  

---
## 테이블

그걸 판단하기 위해서 API별로 어떤 기능이 추가되었는지를 확인하는게 좋을 것 같습니다. 제가 생각하기에 API별로 채용된 특징 중 성능이나 개발과정에 큰 변화를 가져오는 내용은 다음과 같습니다.

| 버전 | 버전명        | 설명 |
|-----|-------------|-------|
| 21  | Lollipop    | - ART를 기본 런타임환경으로 채용 : Dalvik 런타임이 ART로 변경되면서 구동 퍼포먼스가 향상 되었습니다.
|     |             | - OpenGL ES 3.1 지원 : 그래픽처리에서 더 많은 기능을 사용할 수 있게 되었습니다.
|     |             | - 머테리얼 디자인 지원 : 구글에서 제시한 플랫 디자인을 사용할 수 있게 되었습니다.
| 23  | Marshmallow | - 런타임 퍼미션 채용 : GPS나 파일쓰기 같은 위험한 권한이 필요할 때 앱 실행 중에 권한을 추가적으로 요청함으로써 사용자의 주의를 환기시키는 것이 가능해졌습니다.
| 24  | Nougat      | - ART 엔진 개선 : 기존의 ART 런타임을 ART + JIT 형태로 변경하여 상호보완적인 기능을 수행하도록 하였습니다.
|     |             | - OpenGL ES 3.2 지원 그래픽처리에서 더더 많은 기능을 사용할 수 있게 되었습니다.
| 26  | Oreo        | - 백그라운드 실행제한 : 백그라운드에서 앱이 계속 살아있지 못하도록 제한하므로 API 26부터는 백그라운드를 사용하는 앱의 구조를 근본적으로 다시 설계해야 합니다.

        """.trimIndent()
        logger.debug { converter.convertMarkdownToHtml(markdown) }
    }
}
