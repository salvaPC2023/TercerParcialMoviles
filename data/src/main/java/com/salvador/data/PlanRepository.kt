package com.salvador.data

import com.salvador.data.plan.IPlanDataSource
import com.salvador.domain.Plan

class PlanRepository(
    val dataSource: IPlanDataSource
) {
    suspend fun getPlans(): NetworkResult<List<Plan>> {
        return dataSource.fetchPlans()
    }
}