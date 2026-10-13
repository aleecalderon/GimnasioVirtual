package com.example.gimnasiovirtual.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.gimnasiovirtual.HomeActivity
import com.example.gimnasiovirtual.databinding.ActivityRegisterBinding
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val repository by lazy { AuthRepository() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener { attemptRegister() }
        binding.tvGoLogin.setOnClickListener { finish() }
    }

    private fun attemptRegister() {
        val name = binding.etName.text?.toString().orEmpty().trim()
        val email = binding.etEmail.text?.toString().orEmpty().trim()
        val password = binding.etPassword.text?.toString().orEmpty()
        val confirmPassword = binding.etConfirmPassword.text?.toString().orEmpty()

        binding.tilName.error = AuthValidator.validateName(name)
        binding.tilEmail.error = AuthValidator.validateEmail(email)
        binding.tilPassword.error = AuthValidator.validatePassword(password)
        binding.tilConfirmPassword.error = AuthValidator.validateConfirmPassword(password, confirmPassword)

        if (binding.tilName.error != null ||
            binding.tilEmail.error != null ||
            binding.tilPassword.error != null ||
            binding.tilConfirmPassword.error != null
        ) return

        setLoading(true)
        binding.tvMessage.visibility = View.GONE

        repository.register(name, email, password) { result ->
            setLoading(false)
            result.onSuccess { openHome() }
            result.onFailure { error ->
                binding.tvMessage.text = when (error) {
                    is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con ese correo electrónico."
                    is FirebaseAuthWeakPasswordException -> "La contraseña es demasiado débil. Usa al menos 6 caracteres."
                    is FirebaseNetworkException -> "No hay conexión. Comprueba tu internet e inténtalo de nuevo."
                    is ProfileSaveException -> error.message
                    else -> "No se pudo completar el registro. Revisa los datos e inténtalo de nuevo."
                }
                binding.tvMessage.visibility = View.VISIBLE
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.btnRegister.isEnabled = !loading
        binding.btnRegister.text = if (loading) "Creando cuenta..." else "Crear cuenta"
    }

    private fun openHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
