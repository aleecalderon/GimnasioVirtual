package com.example.gimnasiovirtual.routines

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.gimnasiovirtual.databinding.ActivityEntrenamientoBinding

class EntrenamientoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEntrenamientoBinding

    // Variables para controlar el cronómetro
    private var segundos = 0
    private var corriendo = false
    private val handler = Handler(Looper.getMainLooper())

    private val runnable = object : Runnable {
        override fun run() {
            if (corriendo) {
                segundos++
                actualizarCronometro()
                handler.postDelayed(this, 1000) // Se ejecuta cada segundo
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntrenamientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Iniciar el cronómetro automáticamente al entrar
        iniciarCronometro()

        // Botón Pausar / Reanudar
        binding.btnPausar.setOnClickListener {
            if (corriendo) {
                pausarCronometro()
                binding.btnPausar.text = "Reanudar"
            } else {
                iniciarCronometro()
                binding.btnPausar.text = "Pausar"
            }
        }

        // Botón Finalizar Entrenamiento
        binding.btnFinalizar.setOnClickListener {
            pausarCronometro()
            val intent = Intent(this, EntrenamientoCompletadoActivity::class.java).apply {
                putExtra("EXTRA_TIEMPO_TOTAL", formatearTiempo(segundos))
            }
            startActivity(intent)
            finish()
        }
    }

    private fun iniciarCronometro() {
        if (!corriendo) {
            corriendo = true
            handler.post(runnable)
        }
    }

    private fun pausarCronometro() {
        corriendo = false
        handler.removeCallbacks(runnable)
    }

    private fun actualizarCronometro() {
        binding.tvCronometro.text = formatearTiempo(segundos)
    }

    private fun formatearTiempo(segundosTotales: Int): String {
        val minutos = segundosTotales / 60
        val segs = segundosTotales % 60
        return String.format("%02d:%02d", minutos, segs)
    }

    override fun onDestroy() {
        super.onDestroy()
        pausarCronometro() // Evitar fugas de memoria al cerrar la pantalla
    }
}