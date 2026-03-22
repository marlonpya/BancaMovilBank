package com.microsol.bancamovil.data.remote.mock

import com.microsol.bancamovil.data.remote.api.AuthService
import com.microsol.bancamovil.data.remote.dto.*
import kotlinx.coroutines.delay
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockAuthService @Inject constructor() : AuthService {

    override suspend fun login(
        authorization: String,
        contentType: String,
        request: LoginRequestDto
    ): Response<LoginResponseDto> {
        delay(3000)

        return if ((request.user.usrCode == "userTest1" &&
                    request.user.pass == "passTest1") ||
            (request.user.usrCode == "User@test" &&
                    request.user.pass == "TestPass_") ||
            (request.user.usrCode == "user123&" &&
                    request.user.pass == "123456")
        ) {

            val response = LoginResponseDto(
                data = AuthDataDto(
                    accessToken = "mock_access_token_${System.currentTimeMillis()}",
                    expiresIn = "2017-08-26T23:27:26Z",
                    tokenType = "Bearer",
                    user = UserDto(
                        id = "597a76de9ad7ce1fbce07095",
                        rbac = RbacDto(
                            role = "Guest",
                            template = "default"
                        ),
                        profile = ProfileDto(
                            language = "es"
                        )
                    )
                )
            )
            Response.success(response)

        } else if (request.user.usrCode == "usr_error") {
            val response = LoginResponseDto(
                error = ErrorDto(
                    code = 40101,
                    userMessage = UserMessageDto(
                        original = "Invalid credentials",
                        spanish = "Usuario y/o contraseña incorrectos"
                    )
                )
            )
            Response.success(response)
        } else {
            val response = LoginResponseDto(
                error = ErrorDto(
                    code = 40100,
                    userMessage = UserMessageDto(
                        original = "Unexpected error occurred",
                        spanish = "Sucedió un error inesperado"
                    )
                )
            )
            Response.success(response)
        }
    }
}
