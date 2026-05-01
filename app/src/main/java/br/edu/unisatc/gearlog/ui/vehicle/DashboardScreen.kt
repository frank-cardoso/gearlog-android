package br.edu.unisatc.gearlog.ui.vehicle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: VehicleViewModel,
    onAddVehicleClick: () -> Unit
) {
    val vehicles by viewModel.vehicles.collectAsState()
    val heroVehicle = vehicles.firstOrNull()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Dashboard") })
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (vehicles.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Nenhum veiculo cadastrado.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onAddVehicleClick) {
                    Text("Adicionar veiculo")
                }
            }
        } else {
            val displayName = "${heroVehicle?.brand.orEmpty()} ${heroVehicle?.model.orEmpty()}"
                .trim()
                .ifBlank { "HONDA CIVIC" }
                .uppercase(Locale.getDefault())
            val nickname = heroVehicle?.nickname?.takeIf { it.isNotBlank() }?.let { "\"$it\"" } ?: "\"Projeto G6\""
            val plate = heroVehicle?.plate?.takeIf { it.isNotBlank() } ?: "ABC1D23"

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    CarCard(
                        displayName = displayName,
                        nickname = nickname,
                        plate = plate
                    )
                }

                item {
                    SummaryBar(
                        odometerKm = heroVehicle?.odometer ?: 152000,
                        modsCount = heroVehicle?.modsCount ?: 15
                    )
                }

                item {
                    QuickActionRow(
                        onLastMaintenance = {},
                        onLastUpgrade = {},
                        onNewRecord = onAddVehicleClick
                    )
                }

                item {
                    SectionTitle(text = "WIP")
                }

                item {
                    WipGallery(items = listOf("Coilovers", "Motor", "Interior", "Freios", "Escape"))
                }

                item {
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}

