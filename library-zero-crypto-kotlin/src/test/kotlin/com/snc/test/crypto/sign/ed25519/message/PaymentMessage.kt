package com.snc.test.crypto.sign.ed25519.message

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class PaymentMessage(

    @field:JsonProperty("일시")
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val dateTime: LocalDateTime,
    @field:JsonProperty("장소")
    val location: String,
    @field:JsonProperty("사용자")
    val user: String,
    @field:JsonProperty("메뉴")
    val menu: String,
    @field:JsonProperty("목적")
    val purpose: String,
    @field:JsonProperty("금액")
    val amount: Int,
    var hash: String = ""
)
