
package com.example.gimnasiovirtual.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ExerciseDbService {

    suspend fun buscarEjercicios(termino: String): List<EjercicioApi> =
        withContext(Dispatchers.IO) {

            val terminoCodificado = java.net.URLEncoder.encode(
                termino,
                "UTF-8"
            )

            val url = URL(
                "https://oss.exercisedb.dev/api/v1/exercises/search?search=$terminoCodificado"
            )

            val conexion = url.openConnection() as HttpURLConnection

            try {
                conexion.requestMethod = "GET"
                conexion.connectTimeout = 15000
                conexion.readTimeout = 15000

                if (conexion.responseCode !in 200..299) {
                    throw Exception(
                        "ExerciseDB respondió con código ${conexion.responseCode}"
                    )
                }

                val respuesta = conexion.inputStream
                    .bufferedReader(Charsets.UTF_8)
                    .use { it.readText() }

                val objeto = JSONObject(respuesta)
                val datos = objeto.optJSONArray("data") ?: JSONArray()

                buildList {
                    for (i in 0 until datos.length()) {
                        val ejercicio = datos.optJSONObject(i) ?: continue

                        add(
                            EjercicioApi(
                                id = ejercicio.optString("exerciseId"),
                                nombre = ejercicio.optString("name"),
                                gifUrl = ejercicio.optString("gifUrl")
                            )
                        )
                    }
                }
            } finally {
                conexion.disconnect()
            }
        }

    suspend fun obtenerDetalle(id: String): EjercicioApi =
        withContext(Dispatchers.IO) {

            val idCodificado = java.net.URLEncoder.encode(id, "UTF-8")
            val url = URL(
                "https://oss.exercisedb.dev/api/v1/exercises/$idCodificado"
            )

            val conexion = url.openConnection() as HttpURLConnection

            try {
                conexion.requestMethod = "GET"
                conexion.connectTimeout = 15000
                conexion.readTimeout = 15000

                if (conexion.responseCode !in 200..299) {
                    throw Exception(
                        "ExerciseDB respondió con código ${conexion.responseCode}"
                    )
                }

                val respuesta = conexion.inputStream
                    .bufferedReader(Charsets.UTF_8)
                    .use { it.readText() }

                val ejercicio = JSONObject(respuesta)
                    .optJSONObject("data")
                    ?: throw Exception("No se encontraron los datos del ejercicio")

                fun leerLista(nombre: String): List<String> {
                    val arreglo = ejercicio.optJSONArray(nombre) ?: JSONArray()
                    return buildList {
                        for (i in 0 until arreglo.length()) {
                            add(arreglo.optString(i))
                        }
                    }
                }

                EjercicioApi(
                    id = ejercicio.optString("exerciseId"),
                    nombre = ejercicio.optString("name"),
                    gifUrl = ejercicio.optString("gifUrl"),
                    musculosObjetivo = leerLista("targetMuscles"),
                    partesCuerpo = leerLista("bodyParts"),
                    equipo = leerLista("equipments"),
                    musculosSecundarios = leerLista("secondaryMuscles"),
                    instrucciones = leerLista("instructions")
                )
            } finally {
                conexion.disconnect()
            }
        }
}
