package br.edu.unisatc.gearlog.ui

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import br.edu.unisatc.gearlog.ui.settings.SettingsViewModel
import br.edu.unisatc.gearlog.ui.theme.ThemeMode
import br.edu.unisatc.gearlog.ui.theme.ThemeViewModel

fun canEnableBiometric(context: Context): Boolean {
    val biometricManager = BiometricManager.from(context)
    return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
        BiometricManager.BIOMETRIC_SUCCESS -> true
        else -> false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    themeViewModel: ThemeViewModel,
    settingsViewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    val themeMode by themeViewModel.themeMode.collectAsState()
    val biometricEnabled by settingsViewModel.isBiometricEnabled.collectAsState()
    val oilNotificationEnabled by settingsViewModel.isOilNotificationEnabled.collectAsState()
    val temperatureUnit by settingsViewModel.temperatureUnit.collectAsState()
    val distanceUnit by settingsViewModel.distanceUnit.collectAsState()

    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // --- SEÇÃO: INFORMAÇÕES DO USUÁRIO ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Usuário",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "usuario@gearlog.com"
                    Text(
                        text = "Email: $userEmail",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Configurações",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- SEÇÃO: APARÊNCIA ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showThemeDialog = true }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Aparência",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Aparência",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = when (themeMode) {
                            ThemeMode.LIGHT -> "Claro"
                            ThemeMode.DARK -> "Escuro"
                            ThemeMode.SYSTEM -> "Padrão"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- SEÇÃO: NOTIFICAÇÕES ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notificações de Troca de Óleo",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = oilNotificationEnabled,
                        onCheckedChange = { settingsViewModel.toggleOilNotification(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- SEÇÃO: SEGURANÇA ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Acesso por Biometria",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = biometricEnabled,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                if (canEnableBiometric(context)) {
                                    settingsViewModel.toggleBiometric(true)
                                } else {
                                    Toast.makeText(context, "Biometria não configurada no dispositivo.", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                settingsViewModel.toggleBiometric(false)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- SEÇÃO: UNIDADES DO SISTEMA ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Unidades do Sistema",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Unidade de Temperatura
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Temperatura", color = MaterialTheme.colorScheme.onSurface)
                        Row {
                            listOf("°C", "°F").forEach { unit ->
                                val isSelected = temperatureUnit == unit
                                Button(
                                    onClick = { settingsViewModel.changeTemperatureUnit(unit) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text(text = unit)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Unidade de Distância
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Distância", color = MaterialTheme.colorScheme.onSurface)
                        Row {
                            listOf("km", "mi").forEach { unit ->
                                val isSelected = distanceUnit == unit
                                Button(
                                    onClick = { settingsViewModel.changeDistanceUnit(unit) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text(text = unit)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Text(
                    text = "Escolha o tema",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    ThemeOption(
                        text = "Claro",
                        icon = Icons.Filled.Brightness7,
                        isSelected = themeMode == ThemeMode.LIGHT,
                        onClick = {
                            themeViewModel.setThemeMode(ThemeMode.LIGHT)
                            showThemeDialog = false
                        }
                    )
                    ThemeOption(
                        text = "Escuro",
                        icon = Icons.Filled.Brightness4,
                        isSelected = themeMode == ThemeMode.DARK,
                        onClick = {
                            themeViewModel.setThemeMode(ThemeMode.DARK)
                            showThemeDialog = false
                        }
                    )
                    ThemeOption(
                        text = "Padrão do Sistema",
                        icon = Icons.Filled.Settings,
                        isSelected = themeMode == ThemeMode.SYSTEM,
                        onClick = {
                            themeViewModel.setThemeMode(ThemeMode.SYSTEM)
                            showThemeDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ThemeOption(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.weight(1f))
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selecionado",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}