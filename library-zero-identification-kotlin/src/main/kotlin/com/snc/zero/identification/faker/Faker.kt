package com.snc.zero.identification.faker

import com.snc.zero.identification.faker.provider.name.KoreanNameProvider
import com.snc.zero.identification.faker.provider.name.NameProvider

class Faker {

    enum class ProviderType {
        KOREAN,
        ETC
    }

    object Name {

        private fun getProvider(type: ProviderType): NameProvider {
            return when (type) {
                ProviderType.KOREAN -> {
                    KoreanNameProvider
                }
                else -> {
                    KoreanNameProvider
                }
            }
        }

        fun fake(type: ProviderType, female: Boolean = false): Array<String> {
            val provider = getProvider(type)
            val lastName = provider.generateLastName()
            val firstName = if (female) KoreanNameProvider.generateFemaleName() else KoreanNameProvider.generateMaleName()
            return arrayOf(lastName, firstName)
        }

        fun fakeKoreanName(female: Boolean = false): String {
            val v = fake(ProviderType.KOREAN, female)
            return v.joinToString(separator = "")
        }
    }
}