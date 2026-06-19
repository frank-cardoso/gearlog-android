package br.edu.unisatc.gearlog.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.edu.unisatc.gearlog.data.remote.FipeApiClient
import br.edu.unisatc.gearlog.data.remote.FipeDataSource
import br.edu.unisatc.gearlog.data.repository.VehicleRepositoryImpl
import br.edu.unisatc.gearlog.ui.ProfileScreen
import br.edu.unisatc.gearlog.ui.login.LoginScreen
import br.edu.unisatc.gearlog.ui.login.RegisterScreen
import br.edu.unisatc.gearlog.ui.theme.ThemeViewModel
import br.edu.unisatc.gearlog.ui.vehicle.AddVehicleScreen
import br.edu.unisatc.gearlog.ui.vehicle.VehicleViewModel
import br.edu.unisatc.gearlog.ui.vehicle.VehicleViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import br.edu.unisatc.gearlog.data.local.DataStoreManager
import br.edu.unisatc.gearlog.repository.SettingsRepository
import kotlinx.coroutines.launch
import br.edu.unisatc.gearlog.ui.components.AppDrawer
import br.edu.unisatc.gearlog.ui.settings.SettingsScreen
import br.edu.unisatc.gearlog.ui.settings.SettingsViewModel
import br.edu.unisatc.gearlog.ui.settings.SettingsViewModelFactory
import br.edu.unisatc.gearlog.ui.parts.AddPartScreen
import br.edu.unisatc.gearlog.ui.parts.PartsViewModel
import br.edu.unisatc.gearlog.ui.MainScreen

@Composable
fun NavGraph(modifier: Modifier = Modifier, themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val auth = FirebaseAuth.getInstance()
    val vehicleRepository = remember {
        VehicleRepositoryImpl(
            FirebaseFirestore.getInstance(),
            FipeDataSource(FipeApiClient.api),
            dataStoreManager
        )
    }
    val vehicleViewModelFactory = remember { VehicleViewModelFactory(vehicleRepository) }
    val isBiometricEnabled by dataStoreManager.isBiometricEnabled.collectAsState(initial = false)
    val partsViewModel: PartsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
                isBiometricSettingEnabled = isBiometricEnabled,
                onLoginClick = { email, password ->
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Erro: ${task.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Informe email e senha.", Toast.LENGTH_LONG).show()
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                },
                onBiometricSuccess = {
                    if (auth.currentUser != null) {
                        navController.navigate("dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Por favor, faça o login com e-mail e senha a primeira vez.", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterClick = { email, password ->
                    if (email.isNotEmpty() && password.length >= 6) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Erro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Verifique os dados. A senha precisa ter 6+ caracteres.", Toast.LENGTH_LONG).show()
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("dashboard") {
            val viewModel: VehicleViewModel = viewModel(factory = vehicleViewModelFactory)
            MainScreen(
                viewModel = viewModel,
                themeViewModel = themeViewModel,
                partsViewModel = partsViewModel,
                onAddVehicleClick = { navController.navigate("add_vehicle") },
                onAddPartClick = { status -> navController.navigate("add_part/$status") },
                onEditPartClick = { partId -> navController.navigate("edit_part/$partId") }
            )
        }

        composable("add_vehicle") {
            val viewModel: VehicleViewModel = viewModel(factory = vehicleViewModelFactory)
            AddVehicleScreen(
                viewModel = viewModel,
                onSaved = { navController.popBackStack() }
            )
        }

        composable(route = "settings") {
            val context = LocalContext.current

            val dataStoreManager = DataStoreManager(context)
            val repository = SettingsRepository(dataStoreManager)

            val factory = SettingsViewModelFactory(repository)

            val viewModel: SettingsViewModel = viewModel(factory = factory)

            SettingsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("profile") {
            ProfileScreen(
                themeViewModel = themeViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(route = "add_part/{status}") { backStackEntry ->
            val status = backStackEntry.arguments?.getString("status") ?: "INVENTORY"

            AddPartScreen(
                viewModel = partsViewModel,
                initialStatus = status,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
        composable(route = "edit_part/{partId}") { backStackEntry ->
            val partId = backStackEntry.arguments?.getString("partId") ?: ""
            AddPartScreen(
                viewModel = partsViewModel,
                initialStatus = "INVENTORY",
                partId = partId,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
        composable("edit_vehicle/{vehicleId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("vehicleId")
            Text("Tela de Edição em Construção: $id")
        }
    }
}