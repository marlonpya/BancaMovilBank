package com.microsol.bancamovil.data.repository

import android.os.Build
import com.microsol.bancamovil.data.local.preferences.UserPreferences
import com.microsol.bancamovil.data.remote.api.AuthService
import com.microsol.bancamovil.data.remote.dto.*
import com.microsol.bancamovil.data.remote.interceptor.AuthInterceptor
import com.microsol.bancamovil.domain.model.AuthSession
import com.microsol.bancamovil.domain.model.User
import com.microsol.bancamovil.domain.repository.AuthRepository
import com.microsol.bancamovil.domain.util.Result
import com.microsol.bancamovil.domain.util.SESSION_CHECK_INTERVAL
import com.microsol.bancamovil.domain.util.SESSION_DURATION_MS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val userPreferences: UserPreferences,
    private val authInterceptor: AuthInterceptor
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<AuthSession> =
        withContext(Dispatchers.IO) {
            try {
                val request = createLoginRequest(username, password)
                val response = authService.login(request = request)

                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    when {
                        loginResponse?.data != null -> {
                            val session = loginResponse.data.toDomain(username)
                            authInterceptor.setAccessToken(session.accessToken)
                            Result.Success(session)
                        }
                        loginResponse?.error != null -> {
                            val errorMessage = loginResponse.error.userMessage.spanish
                            Result.Error(Exception(errorMessage))
                        }
                        else -> Result.Error(Exception("Sucedió un error inesperado"))
                    }
                } else {
                    Result.Error(Exception("Error de conexión: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.Error(Exception("Sucedió un error inesperado: ${e.message}"))
            }
        }

    override suspend fun saveSession(session: AuthSession) = withContext(Dispatchers.IO) {
        userPreferences.saveAccessToken(session.accessToken)
        session.refreshToken?.let { userPreferences.saveRefreshToken(it) }
        userPreferences.saveUsername(session.user.username)
        userPreferences.saveUserId(session.user.id)
        userPreferences.saveUserRole(session.user.role)
        userPreferences.saveUserTemplate(session.user.template)
        userPreferences.saveUserLanguage(session.user.language)
        userPreferences.saveExpiresIn(session.expiresIn)
        userPreferences.saveTokenType(session.tokenType)
        userPreferences.saveSessionTimestamp(session.loginTimestamp)
        authInterceptor.setAccessToken(session.accessToken)
    }

    override suspend fun getSession(): Flow<AuthSession?> = flow {
        val accessToken = userPreferences.getAccessToken().first()
        val refreshToken = userPreferences.getRefreshToken().first()
        val username = userPreferences.getUsername().first()
        val userId = userPreferences.getUserId().first()
        val role = userPreferences.getUserRole().first()
        val template = userPreferences.getUserTemplate().first()
        val language = userPreferences.getUserLanguage().first()
        val expiresIn = userPreferences.getExpiresIn().first()
        val tokenType = userPreferences.getTokenType().first()
        val timestamp = userPreferences.getSessionTimestamp().first()

        if (accessToken != null && username != null && userId != null &&
            role != null && template != null && language != null &&
            expiresIn != null && tokenType != null && timestamp != null
        ) {
            emit(
                AuthSession(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    expiresIn = expiresIn,
                    tokenType = tokenType,
                    user = User(
                        id = userId,
                        username = username,
                        role = role,
                        template = template,
                        language = language
                    ),
                    loginTimestamp = timestamp
                )
            )
        } else {
            emit(null)
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun clearSession() = withContext(Dispatchers.IO) {
        userPreferences.clearSession()
        authInterceptor.setAccessToken(null)
    }

    override suspend fun getLoginTimestamp(): Long? = withContext(Dispatchers.IO) {
        userPreferences.getSessionTimestamp().first()
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return userPreferences.isLoggedIn()
    }

    override fun isSessionExpired(): Flow<Boolean> {
        return userPreferences.getSessionTimestamp()
            .flatMapLatest { timestamp ->
                if (timestamp == null) {
                    flowOf(true)
                } else {
                    flow {
                        while (true) {
                            val elapsed = System.currentTimeMillis() - timestamp
                            emit(elapsed > SESSION_DURATION_MS)
                            delay(SESSION_CHECK_INTERVAL)
                        }
                    }
                }
            }
            .distinctUntilChanged()
    }

    private fun createLoginRequest(username: String, password: String): LoginRequestDto {
        return LoginRequestDto(
            user = UserRequestDto(
                usrCode = username,
                pass = password,
                profile = ProfileRequestDto(language = "es")
            ),
            device = DeviceRequestDto(
                deviceId = UUID.randomUUID().toString(),
                name = "${Build.MANUFACTURER} ${Build.MODEL}",
                version = Build.VERSION.RELEASE,
                width = "640",
                height = "960",
                model = Build.MODEL,
                platform = "android"
            ),
            app = AppRequestDto(version = "1.0.0")
        )
    }
}


