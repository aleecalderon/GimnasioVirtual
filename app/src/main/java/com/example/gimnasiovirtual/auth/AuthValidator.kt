package com.example.gimnasiovirtual.auth

import android.util.Patterns

object AuthValidator {
    fun validateName(name: String): String? =
        when {
            name.isBlank() -> "Ingresa tu nombre."
            name.trim().length < 2 -> "El nombre debe tener al menos 2 caracteres."
            else -> null
        }

    fun validateEmail(email: String): String? =
        when {
            email.isBlank() -> "Ingresa tu correo electrónico."
            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                "Ingresa un correo electrónico válido."
            else -> null
        }

    fun validatePassword(password: String): String? =
        when {
            password.isBlank() -> "Ingresa tu contraseña."
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres."
            else -> null
        }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? =
        when {
            confirmPassword.isBlank() -> "Confirma tu contraseña."
            password != confirmPassword -> "Las contraseñas no coinciden."
            else -> null
        }
}
