package br.edu.unisatc.gearlog.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person

sealed class GearLogScreen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : GearLogScreen("dashboard", "Home", Icons.Filled.Home)
    object Garage : GearLogScreen("garage", "Garagem", Icons.Filled.DirectionsCar)
    object History : GearLogScreen("history", "Histórico", Icons.Filled.List)
    object Parts : GearLogScreen("parts", "Peças", Icons.Filled.Build)
    object Profile : GearLogScreen("profile", "Perfil", Icons.Filled.Person)
    object AddMaintenance : GearLogScreen("add_maintenance", "Nova Manutenção", Icons.Filled.Build)
    object AddMod : GearLogScreen("add_mod", "Nova Modificação", Icons.Filled.Build)
}

