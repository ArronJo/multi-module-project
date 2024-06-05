package com.snc.zero.identification.faker.provider.name

object EnglishNameProvider : NameProvider() {

    init {
        firstNamesFemale.addAll(
            arrayOf(
                "a", "b", "c",
            )
        )

        firstNamesMale.addAll(
            arrayOf(
                "d", "e", "f"
            )
        )

        lastNames["A"] = 0.10689
        lastNames["B"] = 0.07307
        lastNames["C"] = 0.04192
        lastNames["D"] = 0.02333
        lastNames["E"] = 0.02151
        lastNames["F"] = 0.01176
    }
}