package br.edu.unisatc.gearlog.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThemeViewModel : ViewModel() {

    private var prefs: SharedPreferences? = null

    private val _themeMode = MutableStateFlow<ThemeMode>(ThemeMode.LIGHT)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun initialize(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
            val savedMode = prefs?.getString("theme_mode", ThemeMode.LIGHT.name)
            _themeMode.value = ThemeMode.valueOf(savedMode ?: ThemeMode.LIGHT.name)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            _themeMode.value = mode
            prefs?.edit()?.putString("theme_mode", mode.name)?.apply()
        }
    }
}
