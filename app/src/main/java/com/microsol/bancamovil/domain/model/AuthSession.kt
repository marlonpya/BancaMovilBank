package com.microsol.bancamovil.domain.model

data class AuthSession(
    val accessToken: String,
    val refreshToken: String? = null,
    val expiresIn: String,
    val tokenType: String,
    val user: User,
    val loginTimestamp: Long = System.currentTimeMillis()
) {
    fun isExpired(): Boolean {
        val currentTime = System.currentTimeMillis()
        val sessionDuration = 2 * 60 * 1000L
        return (currentTime - loginTimestamp) > sessionDuration
    }
}
