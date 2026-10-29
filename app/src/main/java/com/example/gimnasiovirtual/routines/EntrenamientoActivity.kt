
package com.example.gimnasiovirtual.routines

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.gimnasiovirtual.databinding.ActivityEntrenamientoBinding

class EntrenamientoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEntrenamientoBinding

    private data class Ejercicio(
        val nombre: String,
        val indicacion: String
    )

    private var ejercicios = emptyList<Ejercicio>()
    private var indiceEjercicio = 0
    private var segundos = 0
    private var corriendo = false
    private var finalizado = false

    private val handler = Handler(Looper.getMainLooper())

    private val runnable = object : Runnable {
        override fun run() {
            if (corriendo && !finalizado) {
                segundos++
                actualizarCronometro()
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEntrenamientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val titulo = intent.getStringExtra("EXTRA_RUTINA_TITULO") ?: "Fuerza"

        val datosEjercicios =
            intent.getStringArrayExtra("EXTRA_EJERCICIOS")

        ejercicios = datosEjercicios
            ?.mapNotNull { dato ->
                val partes = dato.split("|", limit = 2)
                if (partes.size == 2) {
                    Ejercicio(partes[0], partes[1])
                } else {
                    null
                }
            }
            .orEmpty()

        if (ejercicios.isEmpty()) {
            ejercicios = listOf(
                Ejercicio("Sentadillas", "3 series · 12 repeticiones"),
                Ejercicio("Flexiones adaptadas", "3 series · 8 repeticiones"),
                Ejercicio("Jumping jacks", "3 series · 30 segundos"),
                Ejercicio("Plancha abdominal", "3 series · 20 segundos")
            )
        }

        binding.tvTituloRutina.text = titulo

        actualizarEjercicio()
        actualizarCronometro()
        iniciarCronometro()

        binding.btnPausar.setOnClickListener {
            if (corriendo) {
                pausarCronometro()
                binding.btnPausar.text = "Reanudar"
            } else {
                iniciarCronometro()
                binding.btnPausar.text = "Pausar"
            }
        }

        binding.btnSiguiente.setOnClickListener {
            if (indiceEjercicio < ejercicios.lastIndex) {
                indiceEjercicio++
                actualizarEjercicio()
            } else {
                finalizarEntrenamiento()
            }
        }

        binding.btnFinalizar.setOnClickListener {
            finalizarEntrenamiento()
        }
    }

    private fun actualizarEjercicio() {
        val ejercicio = ejercicios[indiceEjercicio]

        binding.tvEjercicio.text = ejercicio.nombre
        binding.tvIndicacion.text = ejercicio.indicacion

        binding.tvProgreso.text =
            "EJERCICIO ${indiceEjercicio + 1} DE ${ejercicios.size}"

        binding.progressEjercicios.max = ejercicios.size
        binding.progressEjercicios.progress = indiceEjercicio + 1

        binding.btnSiguiente.text =
            if (indiceEjercicio == ejercicios.lastIndex) {
                "Completar entrenamiento"
            } else {
                "Siguiente ejercicio"
            }
    }

    private fun iniciarCronometro() {
        if (!corriendo && !finalizado) {
            corriendo = true
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, 1000)
        }
    }

    private fun pausarCronometro() {
        corriendo = false
        handler.removeCallbacks(runnable)
    }

    private fun actualizarCronometro() {
        val minutos = segundos / 60
        val segs = segundos % 60

        binding.tvCronometro.text = String.format(
            java.util.Locale.getDefault(),
            "%02d:%02d",
            minutos,
            segs
        )
    }

    private fun finalizarEntrenamiento() {
        if (finalizado) return

        finalizado = true
        pausarCronometro()

        val intent = Intent(
            this,
            EntrenamientoCompletadoActivity::class.java
        ).apply {
            putExtra("EXTRA_TIEMPO_TOTAL", formatearTiempo(segundos))
            putExtra("EXTRA_RUTINA_TITULO", binding.tvTituloRutina.text.toString())
            putExtra("EXTRA_EJERCICIOS_COMPLETADOS", indiceEjercicio + 1)
            putExtra("EXTRA_TOTAL_EJERCICIOS", ejercicios.size)
        }

        startActivity(intent)
        finish()
    }

    private fun formatearTiempo(totalSegundos: Int): String {
        val minutos = totalSegundos / 60
        val segs = totalSegundos % 60

        return String.format(
            java.util.Locale.getDefault(),
            "%02d:%02d",
            minutos,
            segs
        )
    }

    override fun onDestroy() {
        pausarCronometro()
        super.onDestroy()
    }
}
