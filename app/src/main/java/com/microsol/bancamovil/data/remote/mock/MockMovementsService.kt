package com.microsol.bancamovil.data.remote.mock

import com.microsol.bancamovil.data.remote.api.MovementsService
import com.microsol.bancamovil.data.remote.dto.MovementDto
import com.microsol.bancamovil.data.remote.dto.MovementsResponseDto
import kotlinx.coroutines.delay
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockMovementsService @Inject constructor() : MovementsService {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    
    override suspend fun getAccountMovements(
        accountId: String,
        authorization: String
    ): Response<MovementsResponseDto> {
        delay(3000)
        
        val movements = when (accountId) {
            "1" -> getSolesAccountMovements(accountId)
            "2" -> getDollarAccountMovements(accountId)
            "3" -> getEmptyAccountMovements()
            else -> emptyList()
        }
        
        val response = MovementsResponseDto(
            data = movements
        )
        
        return Response.success(response)
    }
    
    private fun getSolesAccountMovements(accountId: String): List<MovementDto> {
        val calendar = Calendar.getInstance()
        
        return listOf(
            MovementDto(
                id = "mov_1",
                accountId = accountId,
                type = "transfer_in",
                amount = 46.10,
                currency = "PEN",
                description = "Transferencia",
                date = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, -1) }.time),
                balance = 1000.80,
            ),
            MovementDto(
                id = "mov_2",
                accountId = accountId,
                type = "transfer_out",
                amount = -10.00,
                currency = "PEN",
                description = "Plin",
                date = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, -2) }.time),
                balance = 954.70,
            ),
            MovementDto(
                id = "mov_3",
                accountId = accountId,
                type = "deposit",
                amount = 500.00,
                currency = "PEN",
                description = "Transferencia",
                date = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, -5) }.time),
                balance = 964.70,
            ),
            MovementDto(
                id = "mov_4",
                accountId = accountId,
                type = "withdrawal",
                amount = -200.00,
                currency = "PEN",
                description = "Plin",
                date = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, -7) }.time),
                balance = 464.70,
            ),
            MovementDto(
                id = "mov_5",
                accountId = accountId,
                type = "fee",
                amount = -5.00,
                currency = "PEN",
                description = "Plin",
                date = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, -10) }.time),
                balance = 664.70,
            )
        )
    }
    
    private fun getDollarAccountMovements(accountId: String): List<MovementDto> {
        val calendar = Calendar.getInstance()
        
        return listOf(
            MovementDto(
                id = "mov_usd_1",
                accountId = accountId,
                type = "deposit",
                amount = 1000.00,
                currency = "USD",
                description = "Depósito inicial",
                date = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, -3) }.time),
                balance = 1500.20,
            ),
            MovementDto(
                id = "mov_usd_2",
                accountId = accountId,
                type = "interest",
                amount = 25.20,
                currency = "USD",
                description = "Intereses ganados",
                date = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, -15) }.time),
                balance = 500.20,
            ),
            MovementDto(
                id = "mov_usd_3",
                accountId = accountId,
                type = "deposit",
                amount = 475.00,
                currency = "USD",
                description = "Transferencia internacional",
                date = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, -30) }.time),
                balance = 475.00,
            )
        )
    }
    
    private fun getEmptyAccountMovements(): List<MovementDto> {
        return emptyList()
    }
}


