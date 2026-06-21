package br.edu.unisatc.gearlog.ui.vehicle

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.unisatc.gearlog.ui.theme.JdmRed
import br.edu.unisatc.gearlog.model.LogRecord
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

fun saveImageLocally(context: Context, uri: Uri): String? {
    return try {
        val inputStream: InputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = "mod_${UUID.randomUUID()}.jpg"
        val outputFile = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(outputFile)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        outputFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddModScreen(
    viewModel: VehicleViewModel,
    onSaveSuccess: () -> Unit,
    onBackClick: () -> Unit,
    logId: String? = null
) {
    val currentVehicle by viewModel.currentVehicle.collectAsState()
    val logs by viewModel.vehicleLogs.collectAsState()
    val existingLog = remember(logId, logs) { logs.find { it.id == logId } }
    val context = LocalContext.current

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var upgradeName by rememberSaveable(existingLog) { mutableStateOf(existingLog?.title ?: "") }
    var partBrand by rememberSaveable(existingLog) { mutableStateOf(existingLog?.partBrand ?: "") }
    var dateText by rememberSaveable(existingLog) {
        val date = existingLog?.date
        if (date != null) {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val localDate = java.time.Instant.ofEpochMilli(date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            mutableStateOf(localDate.format(formatter))
        } else {
            mutableStateOf("")
        }
    }
    var odometerText by rememberSaveable(existingLog) { mutableStateOf(existingLog?.odometer?.toString() ?: "") }
    var costText by rememberSaveable(existingLog) { mutableStateOf(existingLog?.cost?.toString() ?: "") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (logId == null) "Nova Modificação" else "Editar Modificação") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Voltar")
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
                value = upgradeName,
                onValueChange = { upgradeName = it },
                label = { Text("Nome do Upgrade") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = partBrand,
                onValueChange = { partBrand = it },
                label = { Text("Marca da Peça") },
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
                label = { Text("Custo da Peça (R$)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            // Placeholder de foto com borda tracejada (clicável)
            val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .drawBehind {
                        val strokeWidth = 2.dp.toPx()
                        val radius = 8.dp.toPx()
                        drawRoundRect(
                            color = borderColor,
                            size = size,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius, radius),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                        )
                    }
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Foto da Peça",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Filled.CameraAlt, contentDescription = "Adicionar Foto", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Adicionar Foto da Peça", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 14.sp)
                    }
                }
            }

            val canSave = upgradeName.isNotBlank() && currentVehicle != null

            Button(
                onClick = {
                    if (currentVehicle == null) {
                        coroutineScope.launch { snackbarHostState.showSnackbar("Selecione ou cadastre um veículo antes de salvar.") }
                        return@Button
                    }

                    if (upgradeName.isBlank()) {
                        coroutineScope.launch { snackbarHostState.showSnackbar("O nome do upgrade é obrigatório.") }
                        return@Button
                    }

                    val dateMillis = try {
                        if (dateText.isBlank()) System.currentTimeMillis()
                        else {
                            val localDate: LocalDate = LocalDate.parse(dateText, dateFormatter)
                            localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        }
                    } catch (_: Exception) {
                        coroutineScope.launch { snackbarHostState.showSnackbar("Formato de data inválido. Usando data atual.") }
                        System.currentTimeMillis()
                    }

                    val odometer = odometerText.toIntOrNull() ?: 0
                    val cost = costText.replace(',', '.').toDoubleOrNull() ?: 0.0

                    val photoUrl = if (selectedImageUri != null) {
                        saveImageLocally(context, selectedImageUri!!) ?: ""
                    } else {
                        existingLog?.photoUrl ?: ""
                    }

                    val log = LogRecord(
                        id = logId ?: "",
                        type = "MOD",
                        title = upgradeName.trim(),
                        date = dateMillis,
                        odometer = odometer,
                        cost = cost,
                        description = existingLog?.description ?: "",
                        partBrand = partBrand.trim(),
                        photoUrl = photoUrl
                    )

                    val vehicle = currentVehicle!!
                    coroutineScope.launch {
                        viewModel.saveLogRecord(vehicle.id, log,
                            onSuccess = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = if (logId == null) "Upgrade salvo com sucesso." else "Upgrade atualizado com sucesso.",
                                        duration = SnackbarDuration.Short
                                    )
                                    onSaveSuccess()
                                }
                            },
                            onError = { ex -> coroutineScope.launch { snackbarHostState.showSnackbar("Erro ao salvar: ${ex.message}") } }
                        )
                    }
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = JdmRed)
            ) {
                Text(if (logId == null) "Salvar Upgrade" else "Atualizar Upgrade", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
