package br.edu.unisatc.gearlog.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "gearlog_settings")

class DataStoreManager(private val context: Context) {

    companion object {
        val BIOMETRIC_ENABLED_KEY = booleanPreferencesKey("biometric_enabled")
        val OIL_NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_oil")
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        val TEMPERATURE_UNIT_KEY = stringPreferencesKey("temperature_unit")
        val DISTANCE_UNIT_KEY = stringPreferencesKey("distance_unit")
    }

    val isBiometricEnabled: Flow<Boolean> = context.dataStore.data.map { it[BIOMETRIC_ENABLED_KEY] ?: false }
    val isOilNotificationEnabled: Flow<Boolean> = context.dataStore.data.map { it[OIL_NOTIFICATIONS_KEY] ?: true }
    val isDarkThemeEnabled: Flow<Boolean> = context.dataStore.data.map { it[DARK_THEME_KEY] ?: false }
    val temperatureUnit: Flow<String> = context.dataStore.data.map { it[TEMPERATURE_UNIT_KEY] ?: "°C" }
    val distanceUnit: Flow<String> = context.dataStore.data.map { it[DISTANCE_UNIT_KEY] ?: "km" }

    suspend fun setOilChangeNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[OIL_NOTIFICATIONS_KEY] = enabled }
    }

    suspend fun setDarkThemeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[DARK_THEME_KEY] = enabled }
    }

    suspend fun setTemperatureUnit(unit: String) {
        context.dataStore.edit { it[TEMPERATURE_UNIT_KEY] = unit }
    }

    suspend fun setDistanceUnit(unit: String) {
        context.dataStore.edit { it[DISTANCE_UNIT_KEY] = unit }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { it[BIOMETRIC_ENABLED_KEY] = enabled }
    }
}