package br.edu.unisatc.gearlog.model

data class Vehicle(
    val id: String = "",
    val userId: String = "",
    val model: String = "",
    val brand: String = "",
    val year: Int = 0,
    val plate: String = "",
    val nickname: String = "",
    val odometer: Int = 0,
    val modsCount: Int = 0
)
