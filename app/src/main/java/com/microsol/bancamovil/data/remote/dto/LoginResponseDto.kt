package com.microsol.bancamovil.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.microsol.bancamovil.domain.model.AuthSession
import com.microsol.bancamovil.domain.model.User

data class LoginResponseDto(
    @SerializedName("data")
    val data: AuthDataDto? = null,
    @SerializedName("error")
    val error: ErrorDto? = null
)

data class AuthDataDto(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("expiresIn")
    val expiresIn: String,
    @SerializedName("tokenType")
    val tokenType: String,
    @SerializedName("user")
    val user: UserDto
)

data class UserDto(
    @SerializedName("_id")
    val id: String,
    @SerializedName("rbac")
    val rbac: RbacDto,
    @SerializedName("profile")
    val profile: ProfileDto
)

data class RbacDto(
    @SerializedName("role")
    val role: String,
    @SerializedName("template")
    val template: String
)

data class ProfileDto(
    @SerializedName("language")
    val language: String
)

data class ErrorDto(
    @SerializedName("info")
    val info: Map<String, Any>? = null,
    @SerializedName("code")
    val code: Int,
    @SerializedName("userMessage")
    val userMessage: UserMessageDto
)

data class UserMessageDto(
    @SerializedName("original")
    val original: String,
    @SerializedName("es")
    val spanish: String
)

// Extension functions para mapear DTOs a modelos de dominio
fun AuthDataDto.toDomain(username: String): AuthSession {
    return AuthSession(
        accessToken = accessToken,
        expiresIn = expiresIn,
        tokenType = tokenType,
        user = user.toDomain(username)
    )
}

fun UserDto.toDomain(username: String): User {
    return User(
        id = id,
        username = username,
        role = rbac.role,
        template = rbac.template,
        language = profile.language
    )
}

