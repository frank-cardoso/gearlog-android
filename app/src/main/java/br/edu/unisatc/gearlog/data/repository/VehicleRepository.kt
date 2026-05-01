package br.edu.unisatc.gearlog.data.repository

import br.edu.unisatc.gearlog.model.FipeOption
import br.edu.unisatc.gearlog.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    suspend fun saveVehicle(vehicle: Vehicle): Result<Unit>
    fun getVehicles(userId: String): Flow<List<Vehicle>>
    suspend fun fetchLatestReferenceCode(): Result<Int>
    suspend fun fetchBrands(referenceCode: Int): Result<List<FipeOption>>
    suspend fun fetchModels(referenceCode: Int, brandCode: String): Result<List<FipeOption>>
    suspend fun fetchYears(referenceCode: Int, brandCode: String, modelCode: String): Result<List<FipeOption>>
}
