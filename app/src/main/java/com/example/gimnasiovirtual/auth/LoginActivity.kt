package com.example.gimnasiovirtual.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.gimnasiovirtual.HomeActivity
import com.example.gimnasiovirtual.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.FirebaseNetworkException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val repository by lazy { AuthRepository() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase restaura la sesión automáticamente entre aperturas de la app.
        if (repository.currentUser() != null) {
            openHome()
            return
        }

        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email = binding.etEmail.text?.toString().orEmpty().trim()
        val password = binding.etPassword.text?.toString().orEmpty()

        binding.tilEmail.error = AuthValidator.validateEmail(email)
        binding.tilPassword.error = AuthValidator.validatePassword(password)
        if (binding.tilEmail.error != null || binding.tilPassword.error != null) return

        setLoading(true)
        binding.tvMessage.visibility = View.GONE

        repository.signIn(email, password) { result ->
            setLoading(false)
            result.onSuccess { openHome() }
            result.onFailure { error ->
                binding.tvMessage.text = when (error) {
                    is FirebaseAuthInvalidUserException,
                    is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos. Verifica tus datos e inténtalo de nuevo."
                    is FirebaseNetworkException -> "No hay conexión. Comprueba tu internet e inténtalo de nuevo."
                    else -> "No se pudo iniciar sesión. Verifica tus datos e inténtalo de nuevo."
                }
                binding.tvMessage.visibility = View.VISIBLE
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.btnLogin.isEnabled = !loading
        binding.btnLogin.text = if (loading) "Ingresando..." else "Iniciar sesión"
    }

    private fun openHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
