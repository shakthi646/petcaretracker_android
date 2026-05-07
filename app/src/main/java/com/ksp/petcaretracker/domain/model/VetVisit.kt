package com.ksp.petcaretracker.domain.model

data class VetVisit(
    val id: Long = 0,
    val petId: Long = 0,
    val clinicName: String = "",
    val doctorName: String = "",
    val date: Long = System.currentTimeMillis(),
    val reminderEnabled: Boolean = false,
    val prescriptionImageUri: String? = null,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
