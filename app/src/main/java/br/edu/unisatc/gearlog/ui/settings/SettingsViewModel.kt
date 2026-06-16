package br.edu.unisatc.gearlog.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.edu.unisatc.gearlog.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    val isBiometricEnabled: StateFlow<Boolean> = repository.isBiometricEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val isOilNotificationEnabled: StateFlow<Boolean> = repository.isOilNotificationEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isDarkThemeEnabled: StateFlow<Boolean> = repository.isDarkThemeEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val temperatureUnit: StateFlow<String> = repository.temperatureUnit
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "°C")

    val distanceUnit: StateFlow<String> = repository.distanceUnit
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "km")

    fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch { repository.setBiometric(enabled) }
    }
    fun toggleOilNotification(enabled: Boolean) = viewModelScope.launch { repository.setOilNotification(enabled) }
    fun toggleDarkTheme(enabled: Boolean) = viewModelScope.launch { repository.setDarkTheme(enabled) }
    fun changeTemperatureUnit(unit: String) = viewModelScope.launch { repository.setTemperatureUnit(unit) }
    fun changeDistanceUnit(unit: String) = viewModelScope.launch { repository.setDistanceUnit(unit) }
}

class SettingsViewModelFactory(private val repository: SettingsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}