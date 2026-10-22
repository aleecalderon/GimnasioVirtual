package com.example.gimnasiovirtual.routines

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gimnasiovirtual.databinding.ActivityDetalleRutinaBinding

class DetalleRutinaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleRutinaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleRutinaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperar datos enviados desde la actividad anterior
        val titulo = intent.getStringExtra("EXTRA_RUTINA_TITULO") ?: "Detalle de Rutina"
        val descripcion = intent.getStringExtra("EXTRA_RUTINA_DESCRIPCION") ?: "Sin descripción disponible"

        // Asignar textos a las vistas si existen los TextViews en tu XML
        // binding.tvTituloRutina.text = titulo
        // binding.tvDescripcionRutina.text = descripcion

        // Botón para iniciar el entrenamiento
        binding.btnIniciarEntrenamiento.setOnClickListener {
            val intent = Intent(this, EntrenamientoActivity::class.java).apply {
                putExtra("EXTRA_RUTINA_TITULO", titulo)
            }
            startActivity(intent)
        }
    }
}