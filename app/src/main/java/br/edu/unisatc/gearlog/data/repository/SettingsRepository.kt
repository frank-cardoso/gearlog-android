package br.edu.unisatc.gearlog.repository

import br.edu.unisatc.gearlog.data.local.DataStoreManager
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val dataStoreManager: DataStoreManager) {

    val isOilNotificationEnabled: Flow<Boolean> = dataStoreManager.isOilNotificationEnabled
    val distanceUnit: Flow<String> = dataStoreManager.distanceUnit

    suspend fun setOilNotification(enabled: Boolean) {
        dataStoreManager.setOilChangeNotificationsEnabled(enabled)
    }

    suspend fun setDistanceUnit(unit: String) {
        dataStoreManager.setDistanceUnit(unit)
    }
}