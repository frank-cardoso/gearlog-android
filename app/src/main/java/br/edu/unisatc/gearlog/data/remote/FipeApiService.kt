package br.edu.unisatc.gearlog.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FipeApiService {
    @GET("references")
    suspend fun getReferences(): List<FipeReferenceDto>

    @GET("cars/brands")
    suspend fun getBrands(@Query("reference") referenceCode: Int): List<FipeOptionDto>

    @GET("cars/brands/{brandId}/models")
    suspend fun getModels(
        @Path("brandId") brandId: String,
        @Query("reference") referenceCode: Int
    ): List<FipeOptionDto>

    @GET("cars/brands/{brandId}/models/{modelId}/years")
    suspend fun getYears(
        @Path("brandId") brandId: String,
        @Path("modelId") modelId: String,
        @Query("reference") referenceCode: Int
    ): List<FipeOptionDto>
}
