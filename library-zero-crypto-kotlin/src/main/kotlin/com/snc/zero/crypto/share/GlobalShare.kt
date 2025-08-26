package com.snc.zero.crypto.share

import java.security.SecureRandom
import java.util.Base64

// ===== Secure Random =====
private val secureRandom = SecureRandom()

fun randomBytes(len: Int): ByteArray = ByteArray(len).also { secureRandom.nextBytes(it) }

// ===== Base64 URL-safe =====
private val b64UrlEnc = Base64.getUrlEncoder().withoutPadding()
private val b64UrlDec = Base64.getUrlDecoder()

fun b64UrlEncode(bytes: ByteArray): String = b64UrlEnc.encodeToString(bytes)
fun b64UrlDecode(text: String): ByteArray = b64UrlDec.decode(text)
