package com.misw.vinilos.util

import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object HttpErrorMapper {
    fun toUserMessage(e: Exception): String = when (e) {
        is HttpException -> when (e.code()) {
            400 -> "Datos inválidos. Revisa los campos e intenta de nuevo."
            404 -> "El recurso no fue encontrado."
            500, 503 -> "Error en el servidor. Intenta más tarde."
            else -> "Error de red (${e.code()}). Intenta de nuevo."
        }
        is UnknownHostException,
        is ConnectException -> "Sin conexión a internet. Verifica tu red."
        is SocketTimeoutException -> "La solicitud tardó demasiado. Intenta de nuevo."
        else -> "Ocurrió un error inesperado."
    }
}
