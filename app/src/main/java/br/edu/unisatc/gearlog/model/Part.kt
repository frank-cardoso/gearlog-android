package br.edu.unisatc.gearlog.model

data class Part(
    val id: String,
    val name: String,
    val brand: String,
    val price: Double,
    val quantity: Int,
    val status: PartStatus,
    val photoUrl: String = ""
)

enum class PartStatus {
    INVENTORY,
    WISHLIST
}