package com.salvador.data.plan


import com.salvador.data.NetworkResult
import com.salvador.domain.Plan

interface IPlanDataSource {
    suspend fun fetchPlans(): NetworkResult<List<Plan>>
}