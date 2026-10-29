
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
            abrirRutina(
                "Fuerza",
                "Fortalece tus músculos con ejercicios de fuerza."
            )
        }

        binding.btnCardio.setOnClickListener {
            abrirRutina(
                "Cardio",
                "Mejora tu resistencia y mantente en movimiento."
            )
        }

        binding.btnAbdomen.setOnClickListener {
            abrirRutina(
                "Abdomen",
                "Trabaja la zona abdominal con ejercicios específicos."
            )
        }

        binding.btnEstiramientos.setOnClickListener {
            abrirRutina(
                "Estiramientos",
                "Mejora tu movilidad y flexibilidad."
            )
        }
    }

    private fun abrirRutina(titulo: String, descripcion: String) {
        val intent = Intent(this, DetalleRutinaActivity::class.java).apply {
            putExtra("EXTRA_RUTINA_TITULO", titulo)
            putExtra("EXTRA_RUTINA_DESCRIPCION", descripcion)
        }

        startActivity(intent)
    }
}
