package br.edu.unisatc.gearlog.data.remote

import com.google.gson.annotations.SerializedName

data class FipeOptionDto(
    @SerializedName("code") val code: String,
    @SerializedName("name") val name: String
)

data class FipeReferenceDto(
    @SerializedName("code") val code: Int,
    @SerializedName("month") val month: String
)
