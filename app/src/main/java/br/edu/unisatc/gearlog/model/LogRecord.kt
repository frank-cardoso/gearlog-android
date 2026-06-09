package br.edu.unisatc.gearlog.model

data class LogRecord(
    val id: String = "",
    val type: String = "",        // e.g. "MAINTENANCE" | "MOD"
    val title: String = "",
    val date: Long = 0L,           // epoch millis
    val odometer: Int = 0,
    val cost: Double = 0.0,
    val description: String = "",
    val partBrand: String = "",
    val photoUrl: String = ""
)

