package com.microsol.bancamovil.domain.model

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthSessionTest {

    @Test
    fun `session should not be expired when created recently`() {
        // Given
        val session = AuthSession(
            accessToken = "token123",
            expiresIn = "2024-12-31T23:59:59Z",
            tokenType = "Bearer",
            user = User(
                id = "user1",
                username = "testuser",
                role = "Guest",
                template = "default",
                language = "es"
            ),
            loginTimestamp = System.currentTimeMillis()
        )

        // When & Then
        assertFalse(session.isExpired())
    }

    @Test
    fun `session should be expired when older than 2 minutes`() {
        // Given
        val threeMinutesAgo = System.currentTimeMillis() - (3 * 60 * 1000L)
        val session = AuthSession(
            accessToken = "token123",
            expiresIn = "2024-12-31T23:59:59Z",
            tokenType = "Bearer",
            user = User(
                id = "user1",
                username = "testuser",
                role = "Guest",
                template = "default",
                language = "es"
            ),
            loginTimestamp = threeMinutesAgo
        )

        // When & Then
        assertTrue(session.isExpired())
    }

    @Test
    fun `session should not be expired when exactly 2 minutes old`() {
        // Given
        val exactlyTwoMinutesAgo = System.currentTimeMillis() - (2 * 60 * 1000L)
        val session = AuthSession(
            accessToken = "token123",
            expiresIn = "2024-12-31T23:59:59Z",
            tokenType = "Bearer",
            user = User(
                id = "user1",
                username = "testuser",
                role = "Guest",
                template = "default",
                language = "es"
            ),
            loginTimestamp = exactlyTwoMinutesAgo
        )

        // When & Then
        assertFalse(session.isExpired())
    }
}


