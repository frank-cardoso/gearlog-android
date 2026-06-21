package br.edu.unisatc.gearlog.ui.vehicle

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVehicleScreen(vehicleId: String, viewModel: VehicleViewModel, navController: NavController) {
    val vehicleList by viewModel.vehicles.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var brand by rememberSaveable { mutableStateOf("") }
    var model by rememberSaveable { mutableStateOf("") }
    var plate by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("") }
    var photoUrl by rememberSaveable { mutableStateOf("") }

    // Image picker
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    // Populate form when vehicle is available
    LaunchedEffect(vehicleId, vehicleList) {
        val vehicle = vehicleList.find { it.id == vehicleId }
        vehicle?.let {
            brand = it.brand
            model = it.model
            plate = it.plate
            year = it.year.toString()
            photoUrl = it.photoUrl
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Veículo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Photo
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                when {
                    selectedImageUri != null -> {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Foto do veículo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    photoUrl.isNotBlank() -> {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Foto do veículo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Text("Toque para escolher foto", fontSize = 14.sp)
                    }
                }
            }

            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text("Marca") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("Modelo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = plate,
                onValueChange = { plate = it },
                label = { Text("Placa") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = year,
                onValueChange = { year = it.filter { ch -> ch.isDigit() } },
                label = { Text("Ano") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            val context = LocalContext.current

            Button(
                onClick = {
                    // determine final photo URL: save locally if user selected new image
                    val finalPhotoUrl = if (selectedImageUri != null) {
                        saveImageLocally(context, selectedImageUri!!) ?: photoUrl.ifBlank { "" }
                    } else {
                        photoUrl.ifBlank { "" }
                    }

                    // update local state so UI reflects change immediately
                    photoUrl = finalPhotoUrl

                    val updatedData = mapOf(
                        "brand" to brand,
                        "model" to model,
                        "plate" to plate,
                        "year" to (year.toIntOrNull() ?: 0),
                        "photoUrl" to finalPhotoUrl
                    )

                    viewModel.updateVehicle(vehicleId, updatedData) { success ->
                        if (success) {
                            navController.popBackStack()
                        } else {
                            coroutineScope.launch { snackbarHostState.showSnackbar("Falha ao salvar alterações") }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = brand.isNotBlank() && model.isNotBlank() && plate.isNotBlank()
            ) {
                Text("Salvar Alterações")
            }
        }
    }
}


