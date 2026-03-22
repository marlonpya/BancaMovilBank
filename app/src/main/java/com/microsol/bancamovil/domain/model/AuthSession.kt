package com.microsol.bancamovil.domain.model

import com.microsol.bancamovil.domain.util.SESSION_DURATION_MS

data class AuthSession(
    val accessToken: String,
    val refreshToken: String? = null,
    val expiresIn: String,
    val tokenType: String,
    val user: User,
    val loginTimestamp: Long = System.currentTimeMillis()
) {
    fun isExpired(): Boolean =
        (System.currentTimeMillis() - loginTimestamp) > SESSION_DURATION_MS
}
