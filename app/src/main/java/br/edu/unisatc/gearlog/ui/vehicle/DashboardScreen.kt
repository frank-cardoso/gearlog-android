package br.edu.unisatc.gearlog.ui.vehicle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.TextButton
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.navigation.NavController
import java.util.Locale
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import br.edu.unisatc.gearlog.ui.theme.PremiumCard
import br.edu.unisatc.gearlog.ui.theme.PremiumMuted
import br.edu.unisatc.gearlog.ui.theme.JdmRed
import br.edu.unisatc.gearlog.model.LogRecord
import br.edu.unisatc.gearlog.ui.navigation.GearLogScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: VehicleViewModel,
    navController: NavController,
    onAddVehicleClick: () -> Unit,
    onAddMaintenanceClick: () -> Unit,
    onAddModClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val currentVehicle by viewModel.currentVehicle.collectAsState()
    val vehicleList by viewModel.vehicles.collectAsState()
    val vehicleLogs by viewModel.vehicleLogs.collectAsState()
    val isLoadingVehicles by viewModel.isLoadingVehicles.collectAsState()
    val currentVehicleState = currentVehicle
    val loadingVehiclesState = isLoadingVehicles

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        viewModel.fetchMyVehicles()
    }

    LaunchedEffect(currentVehicleState?.id) {
        currentVehicleState?.id?.let { vehicleId ->
            viewModel.fetchVehicleLogs(vehicleId)
        }
    }

    val totalMods = vehicleLogs.count { it.type == "MOD" }
    val currentOdometer = vehicleLogs.maxOfOrNull { it.odometer } ?: currentVehicleState?.odometer ?: 0
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Perfil")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showBottomSheet = false
                                onAddMaintenanceClick()
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Registro de Manutenção",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Troca de óleo, peças e reparos",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showBottomSheet = false
                                onAddModClick()
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = JdmRed.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = JdmRed
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Nova Modificação",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = JdmRed
                                )
                                Text(
                                    text = "Upgrades de performance e estética",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

        when {
            loadingVehiclesState -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
            currentVehicleState == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Não possui veiculo cadastrado.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onAddVehicleClick) {
                        Text("Adicionar veiculo")
                    }
                }
            }
            else -> {
                val vehicle = currentVehicleState
                val displayName = "${vehicle.brand} ${vehicle.model}"
                    .trim()
                    .ifBlank { "HONDA CIVIC" }
                    .uppercase(Locale.getDefault())
                val nickname = vehicle.nickname.takeIf { it.isNotBlank() } ?: "Sem apelido"
                val plate = vehicle.plate.takeIf { it.isNotBlank() } ?: "ABC1D23"

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            colors = CardDefaults.cardColors(containerColor = PremiumCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxSize()) {
                                Column(
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .fillMaxHeight()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    // Display name stays on the card (no selector here)
                                    Text(
                                        text = displayName,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = nickname,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = PremiumMuted
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(100.dp)
                                            .height(40.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = plate,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.Black
                                        )
                                    }
                                }

                                // Direita: Imagem
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                                        .background(Color.Black.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!vehicle.photoUrl.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = vehicle.photoUrl,
                                            contentDescription = "Foto do veículo",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsCar,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            tint = Color.White.copy(alpha = 0.15f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        SummaryBar(
                            odometerKm = currentOdometer,
                            modsCount = totalMods
                        )
                    }

                    // Vehicle context selector placed above the New Record button
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box {
                                Surface(
                                    modifier = Modifier
                                        .clickable { expanded = true }
                                        .padding(8.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    tonalElevation = 2.dp
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsCar,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.95f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "VEÍCULO ATIVO",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.LightGray
                                            )
                                            Text(
                                                text = currentVehicleState?.let { "${it.brand} ${it.model}" } ?: "Sem veículo",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                maxLines = 1
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Selecionar veículo",
                                            tint = Color.White
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.background(PremiumCard)
                                ) {
                                    vehicleList.forEach { car ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.DirectionsCar,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(18.dp),
                                                        tint = Color.White.copy(alpha = 0.9f)
                                                    )
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = "${car.brand} ${car.model}",
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = if (car.id == currentVehicleState?.id) JdmRed else Color.White
                                                        )
                                                        Text(
                                                            text = car.plate,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = PremiumMuted
                                                        )
                                                    }
                                                    if (car.id == currentVehicleState?.id) {
                                                        Icon(
                                                            imageVector = Icons.Default.CheckCircle,
                                                            contentDescription = "Selecionado",
                                                            tint = JdmRed,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                }
                                            },
                                            onClick = {
                                                viewModel.setCurrentVehicle(car)
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = { showBottomSheet = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = JdmRed,
                                contentColor = Color.White
                            )
                        ) {
                            Text("+ Novo Registro")
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Últimos Registros",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            TextButton(
                                onClick = { navController.navigate(GearLogScreen.History.route) }
                            ) {
                                Text(
                                    text = "Ver Histórico Completo",
                                    color = JdmRed
                                )
                            }
                        }
                    }

                    if (vehicleLogs.isEmpty()) {
                        item {
                            Text(text = "Não possui registros", color = Color.LightGray)
                        }
                    } else {
                        items(vehicleLogs.take(5)) { log ->
                            LogHistoryCard(log = log)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LogHistoryCard(log: LogRecord) {
    val isMod = log.type == "MOD"
    val icon = if (isMod) Icons.Default.FlashOn else Icons.Default.Build
    val iconColor = if (isMod) JdmRed else MaterialTheme.colorScheme.primary
    val detailColor = if (isMod) JdmRed else Color.Gray

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getDefault()
    val formattedDate = dateFormat.format(Date(log.date))

    val costFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val formattedCost = costFormat.format(log.cost)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PremiumCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = formattedCost,
                        style = MaterialTheme.typography.bodyMedium,
                        color = detailColor
                    )
                    Text(
                        text = "${log.odometer} km",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}
