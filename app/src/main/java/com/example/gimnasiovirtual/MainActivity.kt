
package com.example.gimnasiovirtual

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gimnasiovirtual.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val usuario = FirebaseAuth.getInstance().currentUser

        if (usuario != null) {
            // Ya inició sesión: abrir la pantalla principal
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        } else {
            // No ha iniciado sesión: mostrar Login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        finish()
    }
}
