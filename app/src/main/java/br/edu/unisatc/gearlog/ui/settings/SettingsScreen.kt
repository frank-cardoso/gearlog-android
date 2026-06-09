package br.edu.unisatc.gearlog.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBackClick: () -> Unit) {
    // Coleta o StateFlow do ViewModel convertido em Estado do Compose
    val oilNotificationEnabled by viewModel.isOilNotificationEnabled.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Configurações", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Notificações de Troca de Óleo")
            Switch(
                checked = oilNotificationEnabled,
                onCheckedChange = { viewModel.toggleOilNotification(it) }
            )
        }
    }
}