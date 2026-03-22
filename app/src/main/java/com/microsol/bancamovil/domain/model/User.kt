package com.microsol.bancamovil.domain.model

data class User(
    val id: String,
    val username: String,
    val role: String,
    val template: String,
    val language: String
)

