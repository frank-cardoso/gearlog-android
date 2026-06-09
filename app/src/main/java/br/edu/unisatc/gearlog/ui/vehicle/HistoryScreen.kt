package br.edu.unisatc.gearlog.ui.vehicle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import java.io.File
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.IconButton
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import br.edu.unisatc.gearlog.model.LogRecord
import br.edu.unisatc.gearlog.ui.theme.JdmRed
import br.edu.unisatc.gearlog.ui.theme.PremiumCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: VehicleViewModel,
    onProfileClick: () -> Unit = {}
) {
    val allLogs by viewModel.vehicleLogs.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }
    var selectedLog by remember { mutableStateOf<LogRecord?>(null) }

    val filteredLogs = when (selectedFilter) {
        "MAINTENANCE" -> allLogs.filter { it.type == "MAINTENANCE" }
        "MOD" -> allLogs.filter { it.type == "MOD" }
        else -> allLogs
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Histórico do Veículo") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Perfil")
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
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                item {
                    FilterButton(
                        text = "Todos",
                        isSelected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" }
                    )
                }
                item {
                    FilterButton(
                        text = "Manutenções",
                        isSelected = selectedFilter == "MAINTENANCE",
                        onClick = { selectedFilter = "MAINTENANCE" }
                    )
                }
                item {
                    FilterButton(
                        text = "Upgrades",
                        isSelected = selectedFilter == "MOD",
                        onClick = { selectedFilter = "MOD" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredLogs.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Nenhum registro encontrado",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredLogs) { log ->
                        RegistroCard(log = log, onClick = { selectedLog = log })
                    }
                }
            }
        }
    }

    if (selectedLog != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedLog = null }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = selectedLog!!.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                val isUpgrade = selectedLog!!.type == "MOD"
                val accentColor = if (isUpgrade) Color(0xFFD7263D) else Color(0xFFAAAAAA)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    dateFormat.timeZone = TimeZone.getDefault()
                    val formattedDate = dateFormat.format(Date(selectedLog!!.date))

                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF888888)
                    )
                    Text(
                        text = "${selectedLog!!.odometer} km",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF888888)
                    )
                    val costFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                    val formattedCost = costFormat.format(selectedLog!!.cost)
                    Text(
                        text = formattedCost,
                        style = MaterialTheme.typography.bodyMedium,
                        color = accentColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (selectedLog!!.photoUrl.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AsyncImage(
                        model = if (selectedLog!!.photoUrl.startsWith("http")) {
                            selectedLog!!.photoUrl
                        } else {
                            File(selectedLog!!.photoUrl)
                        },
                        contentDescription = "Foto do Registro",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                if (isUpgrade && selectedLog!!.partBrand.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Marca: ${selectedLog!!.partBrand}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }

                if (!isUpgrade && selectedLog!!.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Notas/Descrição:",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = selectedLog!!.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFCCCCCC)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFFD7263D) else Color(0xFF2A2A2A),
            contentColor = if (isSelected) Color.White else Color(0xFFAAAAAA)
        )
    ) {
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun RegistroCard(log: LogRecord, onClick: () -> Unit = {}) {
    val isUpgrade = log.type == "MOD"
    val icon = if (isUpgrade) Icons.Default.FlashOn else Icons.Default.Build
    val accentColor = if (isUpgrade) Color(0xFFD7263D) else Color(0xFFAAAAAA)
    val priceColor = if (isUpgrade) Color(0xFFD7263D) else Color.White

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getDefault()
    val formattedDate = dateFormat.format(Date(log.date))

    val costFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val formattedCost = costFormat.format(log.cost)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PremiumCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF888888),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formattedCost,
                        style = MaterialTheme.typography.bodyMedium,
                        color = priceColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${log.odometer} km",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF888888),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
