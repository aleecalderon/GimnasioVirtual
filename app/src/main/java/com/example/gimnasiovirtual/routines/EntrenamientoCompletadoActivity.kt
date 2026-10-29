
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

        val tiempoTotal =
            intent.getStringExtra("EXTRA_TIEMPO_TOTAL") ?: "00:00"

        val tituloRutina =
            intent.getStringExtra("EXTRA_RUTINA_TITULO") ?: "Fuerza y Cardio"

        val ejerciciosCompletados =
            intent.getIntExtra("EXTRA_EJERCICIOS_COMPLETADOS", 0)

        val totalEjercicios =
            intent.getIntExtra("EXTRA_TOTAL_EJERCICIOS", 4)

        binding.tvTituloRutinaFinal.text = tituloRutina
        binding.tvTiempoFinal.text = tiempoTotal
        binding.tvEjerciciosFinal.text =
            "$ejerciciosCompletados de $totalEjercicios ejercicios recorridos"

        binding.btnVolverInicio.setOnClickListener {
            val intent = Intent(this, RutinasActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK
            }

            startActivity(intent)
            finish()
        }
    }
}
