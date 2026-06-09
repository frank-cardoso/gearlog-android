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
        // Definição das chaves tipadas
        val OIL_NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_oil")
        val DISTANCE_UNIT_KEY = stringPreferencesKey("distance_unit")
    }

    // Leitura dos dados (Retorna um Flow que notifica a UI sempre que o dado mudar)
    val isOilNotificationEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[OIL_NOTIFICATIONS_KEY] ?: true // true como padrão
        }

    val distanceUnit: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[DISTANCE_UNIT_KEY] ?: "km"
        }

    // Escrita dos dados (Funções suspensas para rodar fora da main thread)
    suspend fun setOilChangeNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[OIL_NOTIFICATIONS_KEY] = enabled
        }
    }

    suspend fun setDistanceUnit(unit: String) {
        context.dataStore.edit { preferences ->
            preferences[DISTANCE_UNIT_KEY] = unit
        }
    }
}