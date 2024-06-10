package com.snc.zero.identification.faker

import com.snc.zero.identification.faker.provider.name.EnglishNameProvider
import com.snc.zero.identification.faker.provider.name.KoreanNameProvider
import com.snc.zero.identification.faker.provider.name.NameProvider

object Faker {

    enum class ProviderType {
        KOREAN,
        ENGLISH
    }

    object Name {

        private fun getProvider(type: ProviderType): NameProvider {
            return if (ProviderType.KOREAN == type) {
                KoreanNameProvider
            } else {
                EnglishNameProvider
            }
        }

        fun fake(type: ProviderType, female: Boolean = false): Array<String> {
            val provider = getProvider(type)
            val lastName = provider.generateLastName()
            val firstName = if (female) {
                provider.generateFemaleName()
            } else {
                provider.generateMaleName()
            }
            return arrayOf(firstName, lastName)
        }

        fun fakeKoreanName(female: Boolean = false): String {
            val v = fake(ProviderType.KOREAN, female)
            return v.joinToString(separator = "")
        }
    }
}