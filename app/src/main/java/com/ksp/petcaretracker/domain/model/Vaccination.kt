package com.ksp.petcaretracker.domain.model

data class Vaccination(
    val id: Long = 0,
    val petId: Long = 0,
    val vaccineName: String = "",
    val lastDate: Long = System.currentTimeMillis(),
    val nextDueDate: Long = System.currentTimeMillis(),
    val reminderEnabled: Boolean = false,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
