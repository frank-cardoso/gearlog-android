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
import br.edu.unisatc.gearlog.ui.login.LoginScreen
import br.edu.unisatc.gearlog.ui.login.RegisterScreen
import br.edu.unisatc.gearlog.ui.vehicle.AddVehicleScreen
import br.edu.unisatc.gearlog.ui.vehicle.DashboardScreen
import br.edu.unisatc.gearlog.ui.vehicle.VehicleViewModel
import br.edu.unisatc.gearlog.ui.vehicle.VehicleViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.DrawerValue
import kotlinx.coroutines.launch
import br.edu.unisatc.gearlog.ui.components.AppDrawer

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val vehicleRepository = remember {
        VehicleRepositoryImpl(
            FirebaseFirestore.getInstance(),
            FipeDataSource(FipeApiClient.api)
        )
    }
    val vehicleViewModelFactory = remember { VehicleViewModelFactory(vehicleRepository) }

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
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

            val viewModel: VehicleViewModel =
                viewModel(factory = vehicleViewModelFactory)

            val drawerState =
                rememberDrawerState(initialValue = DrawerValue.Closed)

            val scope = rememberCoroutineScope()

            ModalNavigationDrawer(

                drawerState = drawerState,

                drawerContent = {
                    AppDrawer(

                        currentRoute = "dashboard",

                        onNavigate = {

                            navController.navigate(it)

                            scope.launch {
                                drawerState.close()
                            }
                        },

                        onLogout = {

                            auth.signOut()

                            navController.navigate("login") {
                                popUpTo(0)
                            }
                        }
                    )
                }

            ) {

                DashboardScreen(

                    viewModel = viewModel,

                    onAddVehicleClick = {
                        navController.navigate("add_vehicle")
                    },

                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
        }

        composable("add_vehicle") {
            val viewModel: VehicleViewModel = viewModel(factory = vehicleViewModelFactory)
            AddVehicleScreen(
                viewModel = viewModel,
                onSaved = { navController.popBackStack() }
            )
        }
    }
}