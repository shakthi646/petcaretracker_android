package com.ksp.petcaretracker.domain.model

data class GrowthLog(
    val id: Long = 0,
    val petId: Long = 0,
    val weight: Float = 0f,
    val date: Long = System.currentTimeMillis(),
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
