package br.edu.unisatc.gearlog.ui.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import br.edu.unisatc.gearlog.ui.theme.MontserratFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleScreen(
    viewModel: VehicleViewModel,
    onSaved: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Cadastrar veiculo") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FipeDropdown(
                label = "Marca",
                options = state.brands,
                selected = state.selectedBrand,
                isLoading = state.isLoadingBrands,
                enabled = state.referenceCode != null,
                onSelected = viewModel::onBrandSelected
            )

            Spacer(modifier = Modifier.height(12.dp))

            FipeDropdown(
                label = "Modelo",
                options = state.models,
                selected = state.selectedModel,
                isLoading = state.isLoadingModels,
                enabled = state.selectedBrand != null,
                onSelected = viewModel::onModelSelected
            )

            Spacer(modifier = Modifier.height(12.dp))

            FipeDropdown(
                label = "Ano",
                options = state.years,
                selected = state.selectedYear,
                isLoading = state.isLoadingYears,
                enabled = state.selectedModel != null,
                onSelected = viewModel::onYearSelected
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.plate,
                onValueChange = { viewModel.onPlateChange(sanitizePlate(it)) },
                label = { Text("Placa") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = MercosulPlateTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.nickname,
                onValueChange = viewModel::onNicknameChange,
                label = { Text("Apelido") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.odometer,
                onValueChange = viewModel::onOdometerChange,
                label = { Text("Odometro (km)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.modsCount,
                onValueChange = viewModel::onModsCountChange,
                label = { Text("Quantidade de mods") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { viewModel.saveVehicle(onSaved) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Salvar")
                }
            }

            state.errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FipeDropdown(
    label: String,
    options: List<br.edu.unisatc.gearlog.model.FipeOption>,
    selected: br.edu.unisatc.gearlog.model.FipeOption?,
    isLoading: Boolean,
    enabled: Boolean,
    onSelected: (br.edu.unisatc.gearlog.model.FipeOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val displayText = when {
        isLoading -> "Carregando..."
        selected != null -> selected.name
        !enabled -> "Selecione o nível anterior"
        else -> ""
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled && !isLoading) {
                expanded = !expanded
            }
        }
    ) {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            enabled = enabled && !isLoading,
            trailingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary
            )
        )

        if (options.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                options.forEach { option ->
                    val interactionSource = remember { MutableInteractionSource() }
                    val isFocused by interactionSource.collectIsFocusedAsState()
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val isHighlighted = isFocused || isPressed

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = MontserratFontFamily)
                            )
                        },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        },
                        modifier = Modifier.background(
                            if (isHighlighted) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                        ),
                        interactionSource = interactionSource,
                        colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}

private fun sanitizePlate(input: String): String {
    return input.filter { it.isLetterOrDigit() }.uppercase().take(7)
}

private class MercosulPlateTransformation : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): TransformedText {
        val transformed = text.text
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return offset
            }

            override fun transformedToOriginal(offset: Int): Int {
                return offset
            }
        }
        return TransformedText(androidx.compose.ui.text.AnnotatedString(transformed), offsetMapping)
    }
}