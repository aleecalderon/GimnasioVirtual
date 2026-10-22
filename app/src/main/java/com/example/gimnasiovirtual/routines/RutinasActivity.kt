package com.example.gimnasiovirtual.routines

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gimnasiovirtual.databinding.ActivityRutinasBinding

class RutinasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRutinasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRutinasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVerDetalle.setOnClickListener {
            val intent = Intent(this, DetalleRutinaActivity::class.java).apply {
                putExtra("EXTRA_RUTINA_TITULO", "Rutina de Fuerza y Cardio")
                putExtra("EXTRA_RUTINA_DESCRIPCION", "Rutina completa para tonificación y resistencia física.")
            }
            startActivity(intent)
        }
    }
}