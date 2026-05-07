package com.ksp.petcaretracker.domain.model

data class Memory(
    val id: Long = 0,
    val petId: Long = 0,
    val imageUri: String = "",
    val caption: String = "",
    val date: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
