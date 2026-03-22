package com.microsol.bancamovil.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("user")
    val user: UserRequestDto,
    @SerializedName("device")
    val device: DeviceRequestDto,
    @SerializedName("app")
    val app: AppRequestDto
)

data class UserRequestDto(
    @SerializedName("usr_code")
    val usrCode: String,
    @SerializedName("pass")
    val pass: String,
    @SerializedName("profile")
    val profile: ProfileRequestDto
)

data class ProfileRequestDto(
    @SerializedName("language")
    val language: String = "es"
)

data class DeviceRequestDto(
    @SerializedName("deviceId")
    val deviceId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("version")
    val version: String,
    @SerializedName("width")
    val width: String,
    @SerializedName("heigth") // Nota: el API tiene un typo en "height"
    val height: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("platform")
    val platform: String = "android"
)

data class AppRequestDto(
    @SerializedName("version")
    val version: String = "1.0.0"
)

