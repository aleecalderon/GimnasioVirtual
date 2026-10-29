
package com.example.gimnasiovirtual.routines

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gimnasiovirtual.databinding.ActivityDetalleRutinaBinding

class DetalleRutinaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleRutinaBinding

    private data class Ejercicio(
        val nombre: String,
        val indicacion: String
    )

    private val rutinas = mapOf(
        "Fuerza" to listOf(
            Ejercicio("Sentadillas", "3 series · 12 repeticiones"),
            Ejercicio("Flexiones adaptadas", "3 series · 8 repeticiones"),
            Ejercicio("Zancadas", "3 series · 10 repeticiones por pierna"),
            Ejercicio("Plancha abdominal", "3 series · 20 segundos")
        ),
        "Cardio" to listOf(
            Ejercicio("Jumping jacks", "3 series · 30 segundos"),
            Ejercicio("Marcha rápida", "3 series · 45 segundos"),
            Ejercicio("Rodillas elevadas", "3 series · 30 segundos"),
            Ejercicio("Pasos laterales", "3 series · 40 segundos")
        ),
        "Abdomen" to listOf(
            Ejercicio("Abdominales cortos", "3 series · 12 repeticiones"),
            Ejercicio("Bicicleta abdominal", "3 series · 12 repeticiones"),
            Ejercicio("Elevación de piernas", "3 series · 10 repeticiones"),
            Ejercicio("Plancha abdominal", "3 series · 20 segundos")
        ),
        "Estiramientos" to listOf(
            Ejercicio("Cuello y hombros", "2 series · 20 segundos"),
            Ejercicio("Estiramiento de brazos", "2 series · 20 segundos"),
            Ejercicio("Estiramiento de piernas", "2 series · 20 segundos"),
            Ejercicio("Estiramiento de espalda", "2 series · 20 segundos")
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetalleRutinaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val titulo = intent.getStringExtra("EXTRA_RUTINA_TITULO") ?: "Fuerza"
        val descripcion = intent.getStringExtra("EXTRA_RUTINA_DESCRIPCION")
            ?: "Ejercicios para entrenar a tu propio ritmo."

        val ejercicios = rutinas[titulo] ?: rutinas.getValue("Fuerza")

        binding.tvTituloDetalle.text = titulo
        binding.tvDescripcion.text = descripcion

        mostrarEjercicios(ejercicios)

        binding.btnIniciarEntrenamiento.setOnClickListener {
            val intentEntrenamiento =
                Intent(this, EntrenamientoActivity::class.java).apply {
                    putExtra("EXTRA_RUTINA_TITULO", titulo)
                    putExtra(
                        "EXTRA_EJERCICIOS",
                        ejercicios.map { "${it.nombre}|${it.indicacion}" }
                            .toTypedArray()
                    )
                }

            startActivity(intentEntrenamiento)
        }
    }

    private fun mostrarEjercicios(ejercicios: List<Ejercicio>) {
        val contenedor = binding.root.findViewById<LinearLayout>(
            com.example.gimnasiovirtual.R.id.contenedorEjercicios
        )

        contenedor.removeAllViews()

        ejercicios.forEachIndexed { index, ejercicio ->
            val nombre = TextView(this).apply {
                text = String.format(
                    java.util.Locale.getDefault(),
                    "%02d   %s",
                    index + 1,
                    ejercicio.nombre
                )
                textSize = 16f
                setTextColor(android.graphics.Color.WHITE)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val indicacion = TextView(this).apply {
                text = ejercicio.indicacion
                textSize = 14f
                setTextColor(android.graphics.Color.rgb(183, 148, 255))
                setPadding(0, 8, 0, 0)
            }

            contenedor.addView(nombre)
            contenedor.addView(indicacion)

            if (index < ejercicios.lastIndex) {
                val separador = View(this).apply {
                    setBackgroundColor(android.graphics.Color.rgb(48, 43, 67))
                }

                contenedor.addView(
                    separador,
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                    ).apply {
                        topMargin = 24
                        bottomMargin = 24
                    }
                )
            }
        }
    }
}
