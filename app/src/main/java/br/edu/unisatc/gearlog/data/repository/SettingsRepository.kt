package br.edu.unisatc.gearlog.repository

import br.edu.unisatc.gearlog.data.local.DataStoreManager
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val dataStoreManager: DataStoreManager) {

    val isBiometricEnabled: Flow<Boolean> = dataStoreManager.isBiometricEnabled
    val isOilNotificationEnabled: Flow<Boolean> = dataStoreManager.isOilNotificationEnabled
    val isDarkThemeEnabled: Flow<Boolean> = dataStoreManager.isDarkThemeEnabled
    val temperatureUnit: Flow<String> = dataStoreManager.temperatureUnit
    val distanceUnit: Flow<String> = dataStoreManager.distanceUnit

    suspend fun setBiometric(enabled: Boolean) = dataStoreManager.setBiometricEnabled(enabled)
    suspend fun setOilNotification(enabled: Boolean) = dataStoreManager.setOilChangeNotificationsEnabled(enabled)
    suspend fun setDarkTheme(enabled: Boolean) = dataStoreManager.setDarkThemeEnabled(enabled)
    suspend fun setTemperatureUnit(unit: String) = dataStoreManager.setTemperatureUnit(unit)
    suspend fun setDistanceUnit(unit: String) = dataStoreManager.setDistanceUnit(unit)
}