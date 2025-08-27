package com.snc.zero.crypto.share

import java.security.SecureRandom

// ===== Secure Random =====
private val secureRandom = SecureRandom()

fun randomBytes(len: Int): ByteArray = ByteArray(len).also { secureRandom.nextBytes(it) }
