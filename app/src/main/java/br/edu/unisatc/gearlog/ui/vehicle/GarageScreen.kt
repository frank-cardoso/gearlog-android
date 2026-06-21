package br.edu.unisatc.gearlog.ui.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.edu.unisatc.gearlog.model.Vehicle
import br.edu.unisatc.gearlog.ui.navigation.GearLogScreen
import br.edu.unisatc.gearlog.ui.theme.JdmRed
import br.edu.unisatc.gearlog.ui.theme.PremiumCard
import br.edu.unisatc.gearlog.ui.theme.PremiumMuted
import br.edu.unisatc.gearlog.ui.theme.premiumCard
import br.edu.unisatc.gearlog.ui.theme.premiumMuted
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarageScreen(viewModel: VehicleViewModel, navController: NavController) {
    val vehicleList by viewModel.vehicles.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    var selectedVehicleForMenu by remember { mutableStateOf<Vehicle?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Minha Garagem") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(GearLogScreen.AddVehicle.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Veículo")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (vehicleList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.premiumMuted
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Sua garagem está vazia. Adicione seu primeiro projeto!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.premiumMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(vehicleList) { vehicle ->
                        GarageVehicleCard(
                            vehicle = vehicle,
                            onClick = {
                                selectedVehicleForMenu = vehicle
                            }
                        )
                    }
                }
            }
        }

        if (selectedVehicleForMenu != null) {
            val vehicle = selectedVehicleForMenu!!
                ModalBottomSheet(
                onDismissRequest = { selectedVehicleForMenu = null },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                containerColor = MaterialTheme.colorScheme.premiumCard,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Header aligned left with image and texts
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!vehicle.photoUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = vehicle.photoUrl,
                                contentDescription = "Foto do veículo",
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                                                    .background(MaterialTheme.colorScheme.premiumCard),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.premiumMuted,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "${vehicle.brand} ${vehicle.model}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = vehicle.plate,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.premiumMuted
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.premiumMuted.copy(alpha = 0.12f))

                    // Edit action wrapped in a pill-like Surface
                        Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.premiumCard
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        sheetState.hide()
                                    }.invokeOnCompletion {
                                        selectedVehicleForMenu = null
                                        if (vehicle.id.isNotEmpty()) {
                                            navController.navigate("edit_vehicle/${vehicle.id}")
                                        }
                                    }
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Editar Veículo",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Delete action wrapped in a pill-like Surface
                        Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.premiumCard
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val vehicleId = vehicle.id
                                    scope.launch {
                                        sheetState.hide()
                                    }.invokeOnCompletion {
                                        selectedVehicleForMenu = null
                                        viewModel.deleteVehicle(vehicleId) { success ->
                                            if (success) {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Veículo excluído com sucesso")
                                                }
                                            }
                                        }
                                    }
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Excluir Projeto",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GarageVehicleCard(vehicle: Vehicle, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.premiumCard
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Esquerda: Imagem
            if (vehicle.photoUrl.isNotEmpty()) {
                AsyncImage(
                    model = vehicle.photoUrl,
                    contentDescription = "${vehicle.brand} ${vehicle.model}",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.premiumMuted
                        )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Direita: Informações
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                    Text(
                    text = "${vehicle.brand} ${vehicle.model}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${vehicle.year} • ${vehicle.plate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.premiumMuted
                )
            }
        }
    }
}
