package br.edu.unisatc.gearlog.ui.vehicle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import br.edu.unisatc.gearlog.util.PdfReportGenerator
import br.edu.unisatc.gearlog.ui.reports.ReportDialog
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import br.edu.unisatc.gearlog.ui.navigation.GearLogScreen
import androidx.navigation.NavController
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import br.edu.unisatc.gearlog.model.LogRecord
import br.edu.unisatc.gearlog.ui.theme.PremiumCard
import br.edu.unisatc.gearlog.ui.theme.premiumMuted
import br.edu.unisatc.gearlog.ui.theme.premiumCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: VehicleViewModel,
    navController: NavController,
    onProfileClick: () -> Unit = {},
    onLogClick: (String) -> Unit
) {
    val allLogs by viewModel.vehicleLogs.collectAsState()
    val currentVehicle by viewModel.currentVehicle.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showReportDialog by remember { mutableStateOf(false) }
    var logToDelete by remember { mutableStateOf<LogRecord?>(null) }
    val ctx = LocalContext.current

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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = { showReportDialog = true }) {
                    Text("Exportar")
                }
            }

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
                        color = MaterialTheme.colorScheme.premiumMuted
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredLogs) { log ->
                        RegistroCard(
                            log = log,
                            onClick = { onLogClick(log.id) },
                            onEdit = {
                                if (log.type == "MOD") {
                                    navController.navigate("edit_mod/${log.id}")
                                } else {
                                    navController.navigate("edit_maintenance/${log.id}")
                                }
                            },
                            onDelete = {
                                logToDelete = log
                            }
                        )
                    }
                }
            }
        }
    }

    if (logToDelete != null) {
        AlertDialog(
            onDismissRequest = { logToDelete = null },
            title = { Text("Excluir Registro") },
            text = { Text("Tem certeza que deseja excluir '${logToDelete?.title}'? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val logId = logToDelete?.id ?: ""
                        currentVehicle?.id?.let { vehicleId ->
                            viewModel.deleteLogRecord(vehicleId, logId, {
                                logToDelete = null
                            }, {
                                logToDelete = null
                            })
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { logToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    val vehicleForReport = currentVehicle
    if (showReportDialog && vehicleForReport != null) {
        ReportDialog(
            show = showReportDialog,
            onDismiss = { showReportDialog = false },
            vehicle = vehicleForReport,
            logs = allLogs,
            onGenerate = { type ->
                PdfReportGenerator.generateVehicleReport(ctx, vehicleForReport, allLogs, type)
            }
        )
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
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
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
fun RegistroCard(
    log: LogRecord,
    onClick: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val isUpgrade = log.type == "MOD"
    val icon = if (isUpgrade) Icons.Default.FlashOn else Icons.Default.Build
    val accentColor = if (isUpgrade) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.premiumMuted
    val priceColor = if (isUpgrade) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getDefault()
    val formattedDate = dateFormat.format(Date(log.date))

    val costFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val formattedCost = costFormat.format(log.cost)

    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.premiumCard)
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
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.premiumMuted,
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
                        color = MaterialTheme.colorScheme.premiumMuted,
                        fontSize = 12.sp
                    )
                }
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Mais opções",
                        tint = MaterialTheme.colorScheme.premiumMuted
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Editar") },
                        onClick = {
                            showMenu = false
                            onEdit()
                        },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Excluir", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                    )
                }
            }
        }
    }
}
