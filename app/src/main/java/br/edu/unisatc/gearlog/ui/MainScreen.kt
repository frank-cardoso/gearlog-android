package br.edu.unisatc.gearlog.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import br.edu.unisatc.gearlog.ui.components.GearLogBottomNav
import br.edu.unisatc.gearlog.ui.navigation.GearLogScreen
import br.edu.unisatc.gearlog.ui.vehicle.DashboardScreen
import br.edu.unisatc.gearlog.ui.vehicle.VehicleViewModel
import br.edu.unisatc.gearlog.ui.vehicle.AddMaintenanceScreen
import br.edu.unisatc.gearlog.ui.vehicle.AddModScreen
import br.edu.unisatc.gearlog.ui.vehicle.HistoryScreen
import br.edu.unisatc.gearlog.ui.ProfileScreen
import br.edu.unisatc.gearlog.ui.theme.ThemeViewModel

@Composable
fun MainScreen(
    viewModel: VehicleViewModel,
    themeViewModel: ThemeViewModel,
    onAddVehicleClick: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            GearLogBottomNav(navController = navController)
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            NavHost(navController = navController, startDestination = GearLogScreen.Dashboard.route) {
                composable(GearLogScreen.Dashboard.route) {
                    DashboardScreen(
                        viewModel = viewModel,
                        navController = navController,
                        onAddVehicleClick = onAddVehicleClick,
                        onAddMaintenanceClick = { navController.navigate(GearLogScreen.AddMaintenance.route) },
                        onAddModClick = { navController.navigate(GearLogScreen.AddMod.route) },
                        onProfileClick = { navController.navigate(GearLogScreen.Profile.route) }
                    )
                }
                composable(GearLogScreen.AddMaintenance.route) {
                    AddMaintenanceScreen(
                        viewModel = viewModel,
                        onSaveSuccess = { navController.popBackStack() },
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(GearLogScreen.AddMod.route) {
                    AddModScreen(
                        viewModel = viewModel,
                        onSaveSuccess = { navController.popBackStack() },
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(GearLogScreen.Garage.route) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Garagem")
                    }
                }
                composable(GearLogScreen.History.route) {
                    HistoryScreen(
                        viewModel = viewModel,
                        onProfileClick = { navController.navigate(GearLogScreen.Profile.route) }
                    )
                }
                composable(GearLogScreen.Parts.route) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Peças")
                    }
                }
                composable(GearLogScreen.Profile.route) {
                    ProfileScreen(
                        themeViewModel = themeViewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}


