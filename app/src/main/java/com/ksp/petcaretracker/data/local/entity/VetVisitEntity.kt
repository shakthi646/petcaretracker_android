package com.ksp.petcaretracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vet_visits",
    foreignKeys = [
        ForeignKey(
            entity = PetEntity::class,
            parentColumns = ["id"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("petId")]
)
data class VetVisitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val petId: Long,
    val clinicName: String,
    val doctorName: String,
    val date: Long,
    val reminderEnabled: Boolean = false,
    val prescriptionImageUri: String? = null,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
