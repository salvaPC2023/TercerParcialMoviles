package com.salvador.usecases

import com.salvador.data.NetworkResult
import com.salvador.data.PlanRepository
import com.salvador.domain.Plan

class GetPlans(
    private val planRepository: PlanRepository
) {
    suspend fun invoke(): NetworkResult<List<Plan>> {
        return planRepository.getPlans()
    }
}