package com.snc.test.crypto.extensions.hash

import com.snc.zero.extensions.text.toHexString
import com.snc.zero.crypto.extensions.hash.*
import com.snc.zero.logger.jvm.TLogging
import com.snc.zero.test.base.BaseJUnit5Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

private val logger = TLogging.logger { }

class HashExtTest : BaseJUnit5Test() {

    private var data = ""
    private var key = ""
    private var salt = ""
    private var max = 0

    @BeforeEach
    override fun beforeEach(testInfo: TestInfo) {
        super.beforeEach(testInfo)

        // given
        data = "adsjijlasdljksf 0123rfj90-10-1230-23d1-uk0``12s`"
        key = "abc"
        salt = "abc"
        max = 1 //0000
    }

    @Test
    fun `To HmacSha224`() {
        // when
        val v1 = data.toHmacSHA224(key = key).toHexString()
        val v2 = data.toHmacSHA224(key = key, salt = salt).toHexString()
        val v3 = data.toHmacSHA224(key = key, salt = salt, iterationCount = 10).toHexString()
        // then
        logger.debug { "v1: $v1" }
        logger.debug { "v2: $v2" }
        logger.debug { "v3: $v3" }
        assertEquals("a3b10656dd39cd3b3dc50fc3edb788490bbecfabde35ad5b99707074", v1)
        assertEquals("f02a81b1bdc513a4ed5f8b3597f2405c43da06b478fc8e6de46f2504", v2)
        assertEquals("40d2a63fe156f91687bb46aca612e17b18143d266a8bf3d96ee4209d", v3)
    }

    @Test
    fun `To HmacSha256`() {
        // when
        val v1 = data.toHmacSHA256(key = key).toHexString()
        val v2 = data.toHmacSHA256(key = key, salt = salt).toHexString()
        val v3 = data.toHmacSHA256(key = key, salt = salt, iterationCount = 10).toHexString()
        // then
        logger.debug { "HmacSha256 v1: $v1" }
        logger.debug { "HmacSha256 v2: $v2" }
        logger.debug { "HmacSha256 v3: $v3" }
        assertEquals("1d7acfaebbdffa8b51f9083a2040b56c41a89d0fa4527207a537d905117b178f", v1)
        assertEquals("fa7b1eeab49db1fafc4a1204167fbe70385c456e0a92b8c06218e2e3b3c2b874", v2)
        assertEquals("815965c44f9701f643ec9143546554d1a6ce43d7f14e6e6baae930412db51d5a", v3)
    }

    @Test
    fun `To HmacSha384`() {
        // when
        val v1 = data.toHmacSHA384(key = key).toHexString()
        val v2 = data.toHmacSHA384(key = key, salt = salt).toHexString()
        val v3 = data.toHmacSHA384(key = key, salt = salt, iterationCount = 10).toHexString()
        // then
        logger.debug { "HmacSha384 v1: $v1" }
        logger.debug { "HmacSha384 v2: $v2" }
        logger.debug { "HmacSha384 v3: $v3" }
        assertEquals("c9e34a6e052a65892bb8d5df5186b96d0554a865dac09c4b4a93e155ee0e8e3a4d2a8c9689d7f644f61b57637f2c2f81", v1)
        assertEquals("d1a1ff7eeee1561e7956016a0c4eb48b53656879f64acd3b2ba1a63089ad57cfd203c3ad43ceebba4a93f8b1a3850708", v2)
        assertEquals("6b9fa4d9cda514532c435ef374b142532994051c47f47107f3f4f698d3ac45f687938f235f5ccf21abba626cc158db6e", v3)
    }

    @Test
    fun `To HmacSha512`() {
        // when
        val v1 = data.toHmacSHA512(key = key).toHexString()
        val v2 = data.toHmacSHA512(key = key, salt = salt).toHexString()
        val v3 = data.toHmacSHA512(key = key, salt = salt, iterationCount = 10).toHexString()
        // then
        logger.debug { "HmacSHA512 v1: $v1" }
        logger.debug { "HmacSHA512 v2: $v2" }
        logger.debug { "HmacSHA512 v3: $v3" }
        assertEquals("266566de7b547ee99beaf161293f5cefdf193f80664ec9bdd187752aa0fb71539e990c982d61f2e059419a29ef2dacec19821c4a28d1242827c9419263e8d9a5", v1)
        assertEquals("4f82e6fc141515671f1152dd73dbd04400c38d9d1d1eebb6906f4aed689bc938ab5f739da584dbd84b65a30963f3ae32735d2cdb3b482d75af2d79d34c367b76", v2)
        assertEquals("6923ce97e9732b4c2fd86db4f123367aaabb185995698fbf4a2b0a1fc2ed287e0c038e47da67cf302c8001a4bf0863296afadd1982495a97de2b4f90b37b6840", v3)
    }

    @Test
    fun `To Sha224`() {
        // when
        val v1 = data.toSha224().toHexString()
        val v2 = data.toSha224(salt = salt).toHexString()
        val v3 = data.toSha224(salt = salt, iterationCount = 10).toHexString()
        // then
        logger.debug { "Sha224 v1: $v1" }
        logger.debug { "Sha224 v2: $v2" }
        logger.debug { "Sha224 v3: $v3" }
        assertEquals("7a9e71fa6ff8c12e2550dc59884c0f7045c06b216ba56aaa467a7ee3", v1)
        assertEquals("680a4159a49fe986210ba6e46160f8ba987c3d8247bd71ff7f604c1a", v2)
        assertEquals("29d799311fabaed5d9e5f6af004ca9b3ec98054e1596f206a7447a7a", v3)
    }

    @Test
    fun `To Sha256`() {
        // when
        val v1 = data.toSha256().toHexString()
        val v2 = data.toSha256(salt = salt).toHexString()
        val v3 = data.toSha256(salt = salt, iterationCount = 10).toHexString()
        // then
        logger.debug { "Sha256 v1: $v1" }
        logger.debug { "Sha256 v2: $v2" }
        logger.debug { "Sha256 v3: $v3" }
        assertEquals("3efd0942d095fbad6d0cb90cbd3a9c6d17ac862dacd5c2869a0ccfda425d0570", v1)
        assertEquals("562d7552b09cd1ba301f61f6107ae15c839d6d2ef02b0a90a8c85648371d21a0", v2)
        assertEquals("36b620751e99c7c1f97071235b22f6869a33cc77cd179aab02eb5d768ee7cae7", v3)
    }

    @Test
    fun `To Sha384`() {
        // when
        val v1 = data.toSha384().toHexString()
        val v2 = data.toSha384(salt = salt).toHexString()
        val v3 = data.toSha384(salt = salt, iterationCount = 10).toHexString()
        // then
        logger.debug { "Sha384 v1: $v1" }
        logger.debug { "Sha384 v2: $v2" }
        logger.debug { "Sha384 v3: $v3" }
        assertEquals("1c5c47a18ef66781a26a9ece57193b9bef25702f5e94da1c22de7499d4ac66f54658b8d2e638476c8fbde47290d007a1", v1)
        assertEquals("b89c5b4adc05d32902929232838355baf96d34fc40fb933ead9bc3974a2fe373ad22a36addad9ae211a9b592c7ef078b", v2)
        assertEquals("22844e28864b90be94276ac32ee214cfb4d8be57fa0cc4234427a0037eeacfa444939747f6c1e8d00829cada87a789f9", v3)
    }

    @Test
    fun `To Sha512`() {
        // when
        val v1 = data.toSha512().toHexString()
        val v2 = data.toSha512(salt = salt).toHexString()
        val v3 = data.toSha512(salt = salt, iterationCount = 10).toHexString()
        // then
        logger.debug { "Sha512 v1: $v1" }
        logger.debug { "Sha512 v2: $v2" }
        logger.debug { "Sha512 v3: $v3" }
        assertEquals("172943facdf5241d4038bc79a4522019529f9190d4c3ad309ec294a0b6b695b08d2840371ebbaf4fa0e770a52fd39e9198ac00abadc35d397d01ea0ad87cf351", v1)
        assertEquals("6fc8b5ebaf3838fb9b53f260ae8c94643f9c9b27cba852876e7e60072efff2ef36e29beae4503ee09fb761f89e46e2ebe47746744f165ff080f0e308c642ff8c", v2)
        assertEquals("facf70aeedbfc3393046e037108836a8b77e2ec05a9f37b1c85bb9b0f70eafe5b89194ac9fa08402d17a3cd27b4018d7e3092821c1e35d464842053af705c218", v3)
    }

    @Test
    fun `To Shake128`() {
        // when
        val v1 = data.toShake128().toHexString()
        val v2 = data.toShake128(salt = salt).toHexString()
        val v3 = data.toShake128(salt = salt, iterationCount = 10).toHexString()
        // then
        logger.debug { "Shake128 v1: $v1" }
        logger.debug { "Shake128 v2: $v2" }
        logger.debug { "Shake128 v3: $v3" }
        assertEquals("2253a0a3ba83dcffc071806fb05db731", v1)
        assertEquals("75021868ce9fe1439c52f71e3d7e6d8c", v2)
        assertEquals("3c91f49954b50a5ef9609a1f465820ce", v3)
    }

    @Test
    fun `To Shake256`() {
        // when
        val v1 = data.toShake256().toHexString()
        val v2 = data.toShake256(salt = salt).toHexString()
        val v3 = data.toShake256(salt = salt, iterationCount = 10).toHexString()
        // then
        logger.debug { "Shake256 v1: $v1" }
        logger.debug { "Shake256 v2: $v2" }
        logger.debug { "Shake256 v3: $v3" }
        assertEquals("9883e6dbf4dc2cd9cb353981bdfda69a6d80c66cd47468ebc8214169137f3060", v1)
        assertEquals("34340f865c662b05de577dd0400e41e16e4c043c07ab96a685f85c34cadc9301", v2)
        assertEquals("8827078c0ba33b2693e3538f654799ad07d2a1367f0b800647702ff981964664", v3)
    }
}
