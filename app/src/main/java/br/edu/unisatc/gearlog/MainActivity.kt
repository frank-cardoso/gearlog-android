package br.edu.unisatc.gearlog

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
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
            val themeMode by themeViewModel.themeMode.collectAsState()
            GearLogTheme(themeMode = themeMode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavGraph(modifier = Modifier.padding(innerPadding), themeViewModel = themeViewModel)
                }
            }
        }
    }
}