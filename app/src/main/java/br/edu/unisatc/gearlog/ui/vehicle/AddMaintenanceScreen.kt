package br.edu.unisatc.gearlog.ui.vehicle

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.edu.unisatc.gearlog.ui.theme.JdmRed
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import br.edu.unisatc.gearlog.model.LogRecord
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMaintenanceScreen(
    viewModel: VehicleViewModel,
    onSaveSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val currentVehicle by viewModel.currentVehicle.collectAsState()

    var title by rememberSaveable { mutableStateOf("") }
    var dateText by rememberSaveable { mutableStateOf("") }
    var odometerText by rememberSaveable { mutableStateOf("") }
    var costText by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova Manutenção") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            Box(modifier = Modifier.fillMaxSize()) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                )
            }
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título do Serviço") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dateText,
                onValueChange = { dateText = it },
                label = { Text("Data (DD/MM/AAAA)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = odometerText,
                onValueChange = { odometerText = it.filter { ch -> ch.isDigit() } },
                label = { Text("Odômetro (km)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = costText,
                onValueChange = { value -> costText = value.filter { ch -> ch.isDigit() || ch == ',' || ch == '.' } },
                label = { Text("Custo (R$)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição / Notas") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // validations
            val canSave = title.isNotBlank() && currentVehicle != null

            Button(
                onClick = {
                    if (currentVehicle == null) {
                        coroutineScope.launch { snackbarHostState.showSnackbar("Selecione ou cadastre um veículo antes de salvar.") }
                        return@Button
                    }

                    if (title.isBlank()) {
                        coroutineScope.launch { snackbarHostState.showSnackbar("O título é obrigatório.") }
                        return@Button
                    }

                    // parse date
                    val dateMillis = try {
                        if (dateText.isBlank()) {
                            System.currentTimeMillis()
                        } else {
                                            val localDate: LocalDate = LocalDate.parse(dateText, dateFormatter)
                                            localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        }
                                    } catch (_: Exception) {
                                        coroutineScope.launch { snackbarHostState.showSnackbar("Formato de data inválido. Usando data atual.") }
                                        System.currentTimeMillis()
                                    }

                    val odometer = odometerText.toIntOrNull() ?: 0
                    val cost = costText.replace(',', '.').toDoubleOrNull() ?: 0.0

                    val log = LogRecord(
                        id = "",
                        type = "MAINTENANCE",
                        title = title.trim(),
                        date = dateMillis,
                        odometer = odometer,
                        cost = cost,
                        description = description.trim(),
                        partBrand = "",
                        photoUrl = ""
                    )

                    // call viewModel to persist
                    val vehicle = currentVehicle!!
                    coroutineScope.launch {
                        viewModel.saveLogRecord(vehicle.id, log,
                            onSuccess = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Registro salvo com sucesso.",
                                        duration = SnackbarDuration.Short
                                    )
                                    onSaveSuccess()
                                }
                            },
                            onError = { ex ->
                                coroutineScope.launch { snackbarHostState.showSnackbar("Erro ao salvar: ${ex.message}") }
                            }
                        )
                    }
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = JdmRed)
            ) {
                Text("Salvar Registro", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
