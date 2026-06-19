package br.edu.unisatc.gearlog.ui.settings

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

fun canEnableBiometric(context: Context): Boolean {
    val biometricManager = BiometricManager.from(context)
    return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
        BiometricManager.BIOMETRIC_SUCCESS -> true
        else -> false
    }
}

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBackClick: () -> Unit) {
    val context = LocalContext.current

    val biometricEnabled by viewModel.isBiometricEnabled.collectAsState()
    val oilNotificationEnabled by viewModel.isOilNotificationEnabled.collectAsState()
    val darkThemeEnabled by viewModel.isDarkThemeEnabled.collectAsState()
    val temperatureUnit by viewModel.temperatureUnit.collectAsState()
    val distanceUnit by viewModel.distanceUnit.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Configurações", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Segurança", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Seção: Segurança
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Acesso por Biometria")
            Switch(
                checked = biometricEnabled,
                onCheckedChange = { isChecked ->
                    if (isChecked) {
                        if (canEnableBiometric(context)) {
                            viewModel.toggleBiometric(true)
                        } else {
                            Toast.makeText(context, "Biometria não configurada no dispositivo.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        viewModel.toggleBiometric(false)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Aparência e notificações", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        // Seção: Aparência e Notificações
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Tema Escuro")
            Switch(
                checked = darkThemeEnabled,
                onCheckedChange = { viewModel.toggleDarkTheme(it) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Notificações de Troca de Óleo")
            Switch(
                checked = oilNotificationEnabled,
                onCheckedChange = { viewModel.toggleOilNotification(it) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Unidades do Sistema", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Seção: Formato de Grau (Temperatura)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Unidade de Temperatura")
            Row {
                listOf("°C", "°F").forEach { unit ->
                    val isSelected = temperatureUnit == unit
                    Button(
                        onClick = { viewModel.changeTemperatureUnit(unit) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(text = unit)
                    }
                }
            }
        }

        // Seção: Medida de Distância
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Medida de Distância")
            Row {
                listOf("km", "mi").forEach { unit ->
                    val isSelected = distanceUnit == unit
                    Button(
                        onClick = { viewModel.changeDistanceUnit(unit) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(text = unit)
                    }
                }
            }
        }
    }
}