package com.example.gimnasiovirtual.routines

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gimnasiovirtual.databinding.ActivityEntrenamientoCompletadoBinding

class EntrenamientoCompletadoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEntrenamientoCompletadoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntrenamientoCompletadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recibir el tiempo total enviado desde el cronómetro
        val tiempoTotal = intent.getStringExtra("EXTRA_TIEMPO_TOTAL") ?: "00:00"
        binding.tvTiempoFinal.text = "Tiempo Total: $tiempoTotal"

        // Botón para volver al menú principal de Rutinas
        binding.btnVolverInicio.setOnClickListener {
            val intent = Intent(this, RutinasActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}