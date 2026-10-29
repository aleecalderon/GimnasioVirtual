
package com.example.gimnasiovirtual.data

data class EjercicioApi(
    val id: String,
    val nombre: String,
    val gifUrl: String,
    val musculosObjetivo: List<String> = emptyList(),
    val partesCuerpo: List<String> = emptyList(),
    val equipo: List<String> = emptyList(),
    val musculosSecundarios: List<String> = emptyList(),
    val instrucciones: List<String> = emptyList()
)
