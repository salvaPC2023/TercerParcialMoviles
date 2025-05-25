package com.salvador.examen.plans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salvador.data.NetworkResult
import com.salvador.domain.Plan
import com.salvador.usecases.GetPlans
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.mutableStateOf

@HiltViewModel
class PlansViewModel @Inject constructor(
    private val getPlans: GetPlans
) : ViewModel() {

    sealed class PlansState {
        object Loading : PlansState()
        data class Success(val plans: List<Plan>) : PlansState()
        data class Error(val message: String) : PlansState()
    }

    private val _state = MutableStateFlow<PlansState>(PlansState.Loading)
    val state: StateFlow<PlansState> = _state

    private val _currentPlanIndex = MutableStateFlow(0)
    val currentPlanIndex: StateFlow<Int> = _currentPlanIndex

    init {
        loadPlans()
    }

    private fun loadPlans() {
        viewModelScope.launch {
            _state.value = PlansState.Loading

            when (val result = getPlans.invoke()) {
                is NetworkResult.Success -> {
                    _state.value = PlansState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _state.value = PlansState.Error(result.message)
                }
            }
        }
    }

    fun onPlanIndexChanged(index: Int) {
        _currentPlanIndex.value = index
    }

    private val _selectedPlan = mutableStateOf<Plan?>(null)
    val selectedPlan: Plan? get() = _selectedPlan.value

    fun onPlanSelected(plan: Plan) {
        _selectedPlan.value = plan
    }
}