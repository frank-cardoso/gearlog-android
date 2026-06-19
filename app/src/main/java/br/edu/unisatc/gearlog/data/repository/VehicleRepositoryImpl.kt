package br.edu.unisatc.gearlog.data.repository

import br.edu.unisatc.gearlog.data.local.DataStoreManager
import br.edu.unisatc.gearlog.data.remote.FipeDataSource
import br.edu.unisatc.gearlog.data.remote.toDomain
import br.edu.unisatc.gearlog.model.FipeOption
import br.edu.unisatc.gearlog.model.Vehicle
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class VehicleRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val fipeDataSource: FipeDataSource,
    private val dataStoreManager: DataStoreManager
) : VehicleRepository {

    override suspend fun saveVehicle(vehicle: Vehicle): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            val collection = firestore.collection("vehicles")
            val documentRef = if (vehicle.id.isBlank()) {
                collection.document()
            } else {
                collection.document(vehicle.id)
            }

            val vehicleToSave = if (vehicle.id.isBlank()) {
                vehicle.copy(id = documentRef.id)
            } else {
                vehicle
            }

            documentRef.set(vehicleToSave)
                .addOnSuccessListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.success(Unit))
                    }
                }
                .addOnFailureListener { exception ->
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(exception))
                    }
                }
        }
    }

    override fun getVehicles(userId: String): Flow<List<Vehicle>> = callbackFlow {
        val registration = firestore.collection("vehicles")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val vehicles = snapshot?.documents?.mapNotNull { document ->
                    val vehicle = document.toObject(Vehicle::class.java) ?: return@mapNotNull null
                    if (vehicle.id.isBlank()) {
                        vehicle.copy(id = document.id)
                    } else {
                        vehicle
                    }
                } ?: emptyList()

                trySend(vehicles)
            }

        awaitClose { registration.remove() }
    }

    override suspend fun fetchLatestReferenceCode(): Result<Int> = runCatching {
        fipeDataSource.getLatestReferenceCode()
    }.mapError { fipeDataSource.mapError(it) }

    override suspend fun fetchBrands(referenceCode: Int): Result<List<FipeOption>> = runCatching {
        fipeDataSource.getBrands(referenceCode).map { it.toDomain() }
    }.mapError { fipeDataSource.mapError(it) }

    override suspend fun fetchModels(referenceCode: Int, brandCode: String): Result<List<FipeOption>> = runCatching {
        fipeDataSource.getModels(brandCode, referenceCode).map { it.toDomain() }
    }.mapError { fipeDataSource.mapError(it) }

    override suspend fun fetchYears(referenceCode: Int, brandCode: String, modelCode: String): Result<List<FipeOption>> = runCatching {
        fipeDataSource.getYears(brandCode, modelCode, referenceCode).map { it.toDomain() }
    }.mapError { fipeDataSource.mapError(it) }

    override fun getSelectedVehicleId(): Flow<String?> = dataStoreManager.selectedVehicleId

    override suspend fun setSelectedVehicleId(vehicleId: String) {
        dataStoreManager.setSelectedVehicleId(vehicleId)
    }

    override suspend fun deleteVehicle(vehicleId: String): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            firestore.collection("vehicles").document(vehicleId)
                .delete()
                .addOnSuccessListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.success(Unit))
                    }
                }
                .addOnFailureListener { exception ->
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(exception))
                    }
                }
        }
    }
}

private inline fun <T> Result<T>.mapError(transform: (Throwable) -> Throwable): Result<T> {
    val exception = exceptionOrNull() ?: return this
    return Result.failure(transform(exception))
}
