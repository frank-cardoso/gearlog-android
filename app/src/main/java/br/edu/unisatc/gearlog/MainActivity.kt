package br.edu.unisatc.gearlog

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import br.edu.unisatc.gearlog.data.local.DataStoreManager
import br.edu.unisatc.gearlog.navigation.NavGraph
import br.edu.unisatc.gearlog.ui.theme.GearLogTheme
import br.edu.unisatc.gearlog.ui.theme.ThemeViewModel

class MainActivity : FragmentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeViewModel.initialize(this)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val dataStoreManager = remember { DataStoreManager(context) }

            val isDarkThemeEnabled by dataStoreManager.isDarkThemeEnabled.collectAsState(
                initial = isSystemInDarkTheme()
            )

            GearLogTheme(darkTheme = isDarkThemeEnabled) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavGraph(modifier = Modifier.padding(innerPadding), themeViewModel = themeViewModel)
                }
            }
        }
    }
}