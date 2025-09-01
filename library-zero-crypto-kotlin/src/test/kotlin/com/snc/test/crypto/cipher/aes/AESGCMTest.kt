package com.snc.test.crypto.cipher.aes

import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

@DisplayName("AES-GCM 암호화 테스트 (보안 강화)")
class AESGCMTest : BaseJUnit5Test() {

    /**
     * 기본적인 암복호화 기능을 테스트합니다.
     * - 표준 암복호화 동작
     * - 다양한 입력 데이터 처리
     * - AAD(Additional Authenticated Data) 기능
     * - 기본적인 오류 상황 처리
     *
     * @see AESGCMBasicTest
     */
    @Nested
    inner class BasicFunctionality : AESGCMBasicTest()

    /**
     * 키 생성과 관련된 모든 기능을 테스트합니다.
     * - 지원되는 키 길이 (128, 192, 256비트)
     * - 지원되지 않는 키 길이 예외 처리
     * - 키의 무작위성과 품질
     * - 키 호환성
     *
     * @see AESGCMKeyTest
     */
    @Nested
    inner class KeyGeneration : AESGCMKeyTest()

    /**
     * 메타데이터와 해시 관련 보안 기능을 테스트합니다.
     * - EncryptedMetadata 클래스 기능
     * - 강화된 해시 생성 및 검증
     * - 가변 Salt 길이 메타데이터
     * - PBKDF2 해시 함수
     *
     * @see AESGCMMetadataTest
     */
    @Nested
    inner class MetadataAndHashing : AESGCMMetadataTest()

    /**
     * 보안성 관련 테스트를 수행합니다.
     * - 암호문 무작위성
     * - 데이터 무결성 검증
     * - PBKDF2 보안 강화
     * - 레인보우 테이블 공격 저항성
     * - 타임스탬프 보안
     * - 버전 호환성
     *
     * @see AESGCMSecurityTest
     */
    @Nested
    inner class SecurityFeatures : AESGCMSecurityTest()

    /**
     * 다양한 매개변수 조합을 테스트합니다.
     * - 커스텀 키 크기, IV, 태그 크기
     * - Salt 크기 및 해시 반복 횟수
     * - 매개변수 조합 및 유효성 검증
     *
     * @see AESGCMParameterTest
     */
    @Nested
    inner class ParameterHandling : AESGCMParameterTest()

    /**
     * 성능과 대용량 데이터 처리 능력을 테스트합니다.
     * - 대용량 데이터 처리
     * - 높은 반복 횟수 성능
     * - 연속 암호화 스트레스 테스트
     * - 메모리 사용량 모니터링
     * - 보안 레벨별 성능 비교
     *
     * @see AESGCMPerformanceTest
     */
    @Nested
    inner class PerformanceAndStress : AESGCMPerformanceTest()
}
