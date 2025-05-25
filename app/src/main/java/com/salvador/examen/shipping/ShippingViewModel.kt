package com.lainus.examen.shipping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lainus.domain.Plan
import com.lainus.domain.ShippingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShippingViewModel @Inject constructor() : ViewModel() {

    private val _shippingData = MutableStateFlow(ShippingData())
    val shippingData: StateFlow<ShippingData> = _shippingData.asStateFlow()

    private val _selectedPlan = MutableStateFlow<Plan?>(null)
    val selectedPlan: StateFlow<Plan?> = _selectedPlan.asStateFlow()

    fun setSelectedPlan(plan: Plan) {
        _selectedPlan.value = plan
    }

    fun updateReferencePhone(phone: String) {
        _shippingData.value = _shippingData.value.copy(referencePhone = phone)
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        _shippingData.value = _shippingData.value.copy(
            latitude = latitude,
            longitude = longitude
        )
    }

    fun isDataValid(): Boolean {
        val data = _shippingData.value
        return data.referencePhone.isNotBlank() &&
                data.latitude != 0.0 &&
                data.longitude != 0.0
    }

    fun submitShippingData() {
        viewModelScope.launch {
            if (isDataValid()) {
                // Aquí puedes enviar los datos al servidor
                // Por ahora solo imprimimos
                println("Datos de envío: ${_shippingData.value}")
                println("Plan seleccionado: ${_selectedPlan.value?.name}")
            }
        }
    }
}