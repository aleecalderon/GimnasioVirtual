
package com.example.gimnasiovirtual.routines

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.gimnasiovirtual.data.EjercicioApi
import com.example.gimnasiovirtual.data.ExerciseDbService
import com.example.gimnasiovirtual.databinding.ActivityDetalleRutinaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetalleRutinaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleRutinaBinding
    private val servicioApi = ExerciseDbService()
    private val trabajoApi = Job()
    private val alcanceApi = CoroutineScope(Dispatchers.Main + trabajoApi)

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
        prepararReferenciasApi(titulo)

        binding.btnIniciarEntrenamiento.setOnClickListener {
            val intentEntrenamiento =
                android.content.Intent(this, EntrenamientoActivity::class.java).apply {
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
        val contenedor = binding.contenedorEjercicios
        contenedor.removeAllViews()

        ejercicios.forEachIndexed { index, ejercicio ->
            val tarjeta = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 8, 0, 8)
            }

            val nombre = TextView(this).apply {
                text = String.format(
                    java.util.Locale.getDefault(),
                    "%02d   %s",
                    index + 1,
                    ejercicio.nombre
                )
                textSize = 16f
                setTextColor(Color.WHITE)
                setTypeface(null, Typeface.BOLD)
            }

            val indicacion = TextView(this).apply {
                text = ejercicio.indicacion
                textSize = 14f
                setTextColor(Color.rgb(183, 148, 255))
                setPadding(0, 8, 0, 0)
            }

            tarjeta.addView(nombre)
            tarjeta.addView(indicacion)
            contenedor.addView(tarjeta)

            if (index < ejercicios.lastIndex) {
                val separador = View(this).apply {
                    setBackgroundColor(Color.rgb(48, 43, 67))
                }

                contenedor.addView(
                    separador,
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                    ).apply {
                        topMargin = 12
                        bottomMargin = 12
                    }
                )
            }
        }
    }


    private fun prepararReferenciasApi(titulo: String) {
        val terminos = when (titulo) {
            "Fuerza" -> listOf("bodyweight squat", "squat")
            "Cardio" -> listOf("jumping jack", "high knees")
            "Abdomen" -> listOf("crunch", "plank")
            "Estiramientos" -> listOf("stretch", "mobility")
            else -> return
        }

        val contenedor = binding.contenedorEjercicios

        val encabezado = TextView(this).apply {
            text = "Referencias visuales de ExerciseDB"
            textSize = 18f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 24, 0, 8)
        }

        val estado = TextView(this).apply {
            text = "Buscando ejemplos visuales..."
            textSize = 13f
            setTextColor(Color.rgb(169, 165, 184))
        }

        contenedor.addView(encabezado)
        contenedor.addView(estado)

        alcanceApi.launch {
            val encontrados = linkedMapOf<String, EjercicioApi>()
            var huboRespuesta = false

            for (termino in terminos) {
                if (encontrados.size >= 2) break

                try {
                    val resultados = servicioApi.buscarEjercicios(termino)
                    huboRespuesta = true

                    val palabrasClave = when (titulo) {
                        "Fuerza" -> listOf(
                            "squat", "squatting"
                        )
                        "Cardio" -> listOf(
                            "jumping jack", "high knee",
                            "mountain climber", "burpee",
                            "cardio"
                        )
                        "Abdomen" -> listOf(
                            "crunch", "plank",
                            "sit-up", "leg raise"
                        )
                        "Estiramientos" -> listOf(
                            "stretch", "mobility"
                        )
                        else -> emptyList()
                    }

                    resultados
                        .filter { ejercicio ->
                            ejercicio.id.isNotBlank() &&
                                    ejercicio.gifUrl.startsWith("https://") &&
                                    palabrasClave.any { palabra ->
                                        ejercicio.nombre.contains(
                                            palabra,
                                            ignoreCase = true
                                        )
                                    }
                        }
                        .forEach { ejercicio ->
                            if (encontrados.size < 2) {
                                encontrados.putIfAbsent(
                                    ejercicio.id,
                                    ejercicio
                                )
                            }
                        }
                } catch (_: Exception) {
                    // Si falla una búsqueda, se intenta el siguiente término.
                }
            }

            if (isFinishing || isDestroyed) return@launch

            estado.text = when {
                encontrados.isNotEmpty() -> ""
                huboRespuesta ->
                    "No encontramos GIFs adecuados. La rutina local sigue funcionando."
                else ->
                    "ExerciseDB no respondió. Puedes entrenar con la rutina local."
            }

            encontrados.values.forEach { ejercicio ->
                agregarReferencia(contenedor, ejercicio)
            }
        }
    }


    private fun agregarReferencia(
        contenedor: LinearLayout,
        ejercicio: EjercicioApi
    ) {
        val tarjeta = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 12, 0, 16)
        }

        val nombre = TextView(this).apply {
            text = ejercicio.nombre
            textSize = 15f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
        }

        val imagen = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(190)
            ).apply {
                topMargin = dp(8)
                bottomMargin = dp(8)
            }
            scaleType = ImageView.ScaleType.FIT_CENTER
            contentDescription = "Demostración de ${ejercicio.nombre}"
        }

        val fuente = TextView(this).apply {
            text = "Animación proporcionada por ExerciseDB"
            textSize = 12f
            setTextColor(Color.rgb(169, 165, 184))
        }

        tarjeta.addView(nombre)
        tarjeta.addView(imagen)
        tarjeta.addView(fuente)
        contenedor.addView(tarjeta)

        Glide.with(this)
            .load(ejercicio.gifUrl)
            .fitCenter()
            .into(imagen)
    }

    private fun dp(valor: Int): Int =
        (valor * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        trabajoApi.cancel()
        super.onDestroy()
    }
}
