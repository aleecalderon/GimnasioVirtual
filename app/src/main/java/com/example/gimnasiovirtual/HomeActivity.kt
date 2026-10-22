package com.example.gimnasiovirtual

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.gimnasiovirtual.auth.AuthRepository
import com.example.gimnasiovirtual.auth.LoginActivity
import com.example.gimnasiovirtual.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val repository by lazy { AuthRepository() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = repository.currentUser()
        if (user == null) {
            goToLogin()
            return
        }

        binding.tvWelcome.text = "¡Bienvenido/a!"
        binding.tvEmail.text = "Correo: ${user.email ?: "No disponible"}"
        binding.tvUid.text = "UID: ${user.uid}"

        binding.btnLoadProfile.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.tvProfile.text = ""
            repository.getUserProfile { result ->
                binding.progressBar.visibility = View.GONE
                result.onSuccess { profile ->
                    val name = profile["name"] as? String ?: "Sin nombre"
                    val email = profile["email"] as? String ?: user.email.orEmpty()
                    binding.tvProfile.text = "Perfil de Firestore\nNombre: $name\nCorreo: $email"
                }
                result.onFailure { error ->
                    binding.tvProfile.text = error.message ?: "No se pudo consultar el perfil."
                }
            }
        }

        binding.btnLogout.setOnClickListener {
            repository.signOut()
            goToLogin()
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
