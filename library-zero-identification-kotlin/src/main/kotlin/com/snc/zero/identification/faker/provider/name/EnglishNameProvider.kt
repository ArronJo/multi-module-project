package com.snc.zero.identification.faker.provider.name

class EnglishNameProvider : NameProvider() {

    init {

        firstNamesFemale.addAll(
            arrayOf(
                "Mary", "Patricia", "Jennifer", "Linda",
                "Elizabeth", "Barbara", "Susan",
                "Jessica", "Sarah", "Karen"
            )
        )

        firstNamesMale.addAll(
            arrayOf(
                "Michael", "James", "John",
                "Robert", "David", "William",
                "Richard", "Joseph", "Charles", "Thomas"
            )
        )

        lastNames["Jones"] = 0.00193
        lastNames["Garcia"] = 0.00192
        lastNames["Miller"] = 0.00191
        lastNames["Davis"] = 0.00186
        lastNames["Rodriguez"] = 0.00186
        lastNames["Martinez"] = 0.00171
        lastNames["Brown"] = 0.00167
        lastNames["Williams"] = 0.00159
        lastNames["Johnson"] = 0.00153
        lastNames["Smith"] = 0.00144
    }
}