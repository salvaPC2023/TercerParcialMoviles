package com.salvador.domain

data class Plan(
    val id: String,
    val name: String,
    val dataAmount: String,
    val originalPrice: Int,
    val discountedPrice: Int,
    val features: List<String>,
    val socialNetworks: List<String>,
    val isUnlimitedCalls: Boolean = true,
    val isUnlimitedSMS: Boolean = true,
    val hasHotspot: Boolean = true,
    val hasCO2Negative: Boolean = true
)