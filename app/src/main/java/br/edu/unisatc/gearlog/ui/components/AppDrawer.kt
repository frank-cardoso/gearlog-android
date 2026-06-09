package br.edu.unisatc.gearlog.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.edu.unisatc.gearlog.ui.theme.PremiumCard
import br.edu.unisatc.gearlog.ui.theme.PremiumPrimary

@Composable
fun AppDrawer(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {

    ModalDrawerSheet(
        drawerContainerColor = PremiumCard
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "GEARLOG",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        NavigationDrawerItem(
            label = { Text("Dashboard") },
            selected = currentRoute == "dashboard",
            onClick = { onNavigate("dashboard") },
            icon = { Icon(Icons.Default.Home, null) },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = PremiumPrimary
            )
        )

        NavigationDrawerItem(
            label = { Text("Adicionar veículo") },
            selected = currentRoute == "add_vehicle",
            onClick = { onNavigate("add_vehicle") },
            icon = { Icon(Icons.Default.Add, null) },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = PremiumPrimary
            )
        )

        NavigationDrawerItem(
            label = { Text("Meus veículos") },
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.DirectionsCar, null) }
        )

        NavigationDrawerItem(
            label = { Text("Configurações") },
            selected = false,
            onClick = {onNavigate("settings")},
            icon = { Icon(Icons.Default.Settings, null) },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = PremiumPrimary
            )
        )

        HorizontalDivider()

        NavigationDrawerItem(
            label = { Text("Sair") },
            selected = false,
            onClick = onLogout,
            icon = { Icon(Icons.Default.ExitToApp, null) }
        )
    }
}