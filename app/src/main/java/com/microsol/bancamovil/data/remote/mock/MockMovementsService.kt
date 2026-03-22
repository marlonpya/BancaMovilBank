package com.microsol.bancamovil.data.remote.mock

import com.microsol.bancamovil.data.remote.api.MovementsService
import com.microsol.bancamovil.data.remote.dto.MovementDto
import com.microsol.bancamovil.data.remote.dto.MovementsResponseDto
import kotlinx.coroutines.delay
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockMovementsService @Inject constructor() : MovementsService {

    override suspend fun getAccountMovements(
        accountId: String,
        authorization: String
    ): Response<MovementsResponseDto> {
        delay(3000)

        val movements = when (accountId) {
            "1" -> getSolesAccountMovements(accountId)
            "2" -> getDollarAccountMovements(accountId)
            "3" -> emptyList()
            else -> emptyList()
        }

        return Response.success(MovementsResponseDto(data = movements))
    }

    private fun getSolesAccountMovements(accountId: String): List<MovementDto> {
        // Fixed date: 23 Nov 2021 as per spec
        val nov23 = Calendar.getInstance().apply {
            set(2021, Calendar.NOVEMBER, 23, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        val nov23Str = "2021-11-23T12:00:00Z"

        return listOf(
            MovementDto(
                id = "mov_1",
                accountId = accountId,
                type = "transfer_in",
                amount = 6.10,
                currency = "PEN",
                description = "Transferencia",
                date = nov23Str,
                balance = 1000.80,
            ),
            MovementDto(
                id = "mov_2",
                accountId = accountId,
                type = "transfer_out",
                amount = -10.00,
                currency = "PEN",
                description = "Pin",
                date = nov23Str,
                balance = 994.70,
            )
        )
    }

    private fun getDollarAccountMovements(accountId: String): List<MovementDto> {
        return listOf(
            MovementDto(
                id = "mov_usd_1",
                accountId = accountId,
                type = "deposit",
                amount = 1000.00,
                currency = "USD",
                description = "Depósito inicial",
                date = "2021-11-20T12:00:00Z",
                balance = 1800.20,
            ),
            MovementDto(
                id = "mov_usd_2",
                accountId = accountId,
                type = "interest",
                amount = 25.20,
                currency = "USD",
                description = "Intereses ganados",
                date = "2021-11-10T12:00:00Z",
                balance = 800.20,
            ),
            MovementDto(
                id = "mov_usd_3",
                accountId = accountId,
                type = "deposit",
                amount = 775.00,
                currency = "USD",
                description = "Transferencia internacional",
                date = "2021-10-15T12:00:00Z",
                balance = 775.00,
            )
        )
    }
}
