package br.edu.unisatc.gearlog.ui.vehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.unisatc.gearlog.data.repository.VehicleRepository
import br.edu.unisatc.gearlog.model.FipeOption
import br.edu.unisatc.gearlog.model.Vehicle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddVehicleUiState(
    val referenceCode: Int? = null,
    val brands: List<FipeOption> = emptyList(),
    val models: List<FipeOption> = emptyList(),
    val years: List<FipeOption> = emptyList(),
    val selectedBrand: FipeOption? = null,
    val selectedModel: FipeOption? = null,
    val selectedYear: FipeOption? = null,
    val plate: String = "",
    val nickname: String = "",
    val odometer: String = "",
    val modsCount: String = "",
    val isLoadingReference: Boolean = false,
    val isLoadingBrands: Boolean = false,
    val isLoadingModels: Boolean = false,
    val isLoadingYears: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

class VehicleViewModel(
    private val repository: VehicleRepository
) : ViewModel() {

    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid

    private val _uiState = MutableStateFlow(AddVehicleUiState())
    val uiState: StateFlow<AddVehicleUiState> = _uiState.asStateFlow()

    val vehicles: StateFlow<List<Vehicle>> = (userId?.let { repository.getVehicles(it) }
        ?: flowOf(emptyList()))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        loadReference()
    }

    fun retryReference() {
        loadReference()
    }

    fun onPlateChange(value: String) {
        _uiState.update { it.copy(plate = value) }
    }

    fun onNicknameChange(value: String) {
        _uiState.update { it.copy(nickname = value) }
    }

    fun onOdometerChange(value: String) {
        _uiState.update { it.copy(odometer = value) }
    }

    fun onModsCountChange(value: String) {
        _uiState.update { it.copy(modsCount = value) }
    }

    fun onBrandSelected(option: FipeOption) {
        val referenceCode = _uiState.value.referenceCode ?: return
        _uiState.update {
            it.copy(
                selectedBrand = option,
                selectedModel = null,
                selectedYear = null,
                models = emptyList(),
                years = emptyList()
            )
        }
        loadModels(referenceCode, option.code)
    }

    fun onModelSelected(option: FipeOption) {
        val referenceCode = _uiState.value.referenceCode ?: return
        val brandCode = _uiState.value.selectedBrand?.code ?: return
        _uiState.update {
            it.copy(
                selectedModel = option,
                selectedYear = null,
                years = emptyList()
            )
        }
        loadYears(referenceCode, brandCode, option.code)
    }

    fun onYearSelected(option: FipeOption) {
        _uiState.update { it.copy(selectedYear = option) }
    }

    private fun loadReference() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingReference = true, errorMessage = null) }
            val referenceResult = repository.fetchLatestReferenceCode()
            val referenceCode = referenceResult.getOrNull()
            if (referenceCode == null) {
                _uiState.update {
                    it.copy(
                        referenceCode = null,
                        brands = emptyList(),
                        models = emptyList(),
                        years = emptyList(),
                        isLoadingReference = false,
                        errorMessage = referenceResult.exceptionOrNull()?.message
                            ?: "Falha ao carregar referencia da FIPE."
                    )
                }
                return@launch
            }
            _uiState.update { it.copy(referenceCode = referenceCode, isLoadingReference = false) }
            loadBrands(referenceCode)
        }
    }

    private fun loadBrands(referenceCode: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingBrands = true, errorMessage = null) }
            val result = repository.fetchBrands(referenceCode)
            _uiState.update {
                it.copy(
                    brands = result.getOrDefault(emptyList()),
                    isLoadingBrands = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    private fun loadModels(referenceCode: Int, brandCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingModels = true, errorMessage = null) }
            val result = repository.fetchModels(referenceCode, brandCode)
            _uiState.update {
                it.copy(
                    models = result.getOrDefault(emptyList()),
                    isLoadingModels = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    private fun loadYears(referenceCode: Int, brandCode: String, modelCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingYears = true, errorMessage = null) }
            val result = repository.fetchYears(referenceCode, brandCode, modelCode)
            _uiState.update {
                it.copy(
                    years = result.getOrDefault(emptyList()),
                    isLoadingYears = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun saveVehicle(onSuccess: () -> Unit) {
        val currentUserId = userId
        if (currentUserId.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = "Usuario nao autenticado.") }
            return
        }

        val selectedBrand = uiState.value.selectedBrand
        val selectedModel = uiState.value.selectedModel
        val selectedYear = uiState.value.selectedYear
        val plate = uiState.value.plate.trim()
        val nickname = uiState.value.nickname.trim()
        val odometer = uiState.value.odometer.trim().toIntOrNull()
        val modsCount = uiState.value.modsCount.trim().toIntOrNull()
        val yearInt = selectedYear?.let { parseYear(it) }

        if (selectedBrand == null || selectedModel == null || yearInt == null || plate.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Preencha todos os campos corretamente.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = repository.saveVehicle(
                Vehicle(
                    userId = currentUserId,
                    model = selectedModel.name,
                    brand = selectedBrand.name,
                    year = yearInt,
                    plate = plate,
                    nickname = nickname,
                    odometer = odometer ?: 0,
                    modsCount = modsCount ?: 0
                )
            )
            _uiState.update { it.copy(isSaving = false) }

            if (result.isSuccess) {
                val brands = _uiState.value.brands
                _uiState.update { AddVehicleUiState(brands = brands) }
                onSuccess()
            } else {
                _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun parseYear(option: FipeOption): Int? {
        val digits = option.code.substringBefore("-").filter { it.isDigit() }
        return digits.toIntOrNull()
    }
}
