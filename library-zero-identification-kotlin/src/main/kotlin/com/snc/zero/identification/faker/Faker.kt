package com.snc.zero.identification.faker

import com.snc.zero.identification.faker.provider.name.KoreanNameProvider

class Faker {

    object Name {

        fun fakeKoreanName(female: Boolean = false): String {
            val firstName = if (female) KoreanNameProvider.generateFemaleName() else KoreanNameProvider.generateMaleName()
            return KoreanNameProvider.generateLastName() + firstName
        }
    }
}