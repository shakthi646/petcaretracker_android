package com.ksp.petcaretracker.domain.model

import com.ksp.petcaretracker.data.local.entity.DietScheduleEntity
import com.ksp.petcaretracker.data.local.entity.GrowthLogEntity
import com.ksp.petcaretracker.data.local.entity.MemoryEntity
import com.ksp.petcaretracker.data.local.entity.PetEntity
import com.ksp.petcaretracker.data.local.entity.VaccinationEntity
import com.ksp.petcaretracker.data.local.entity.VetVisitEntity

// Pet Mappers
fun PetEntity.toDomain() = Pet(
    id = id,
    name = name,
    type = PetType.entries.find { it.displayName == type } ?: PetType.DOG,
    breed = breed,
    dateOfBirth = dateOfBirth,
    weight = weight,
    gender = Gender.entries.find { it.displayName == gender } ?: Gender.MALE,
    imageUri = imageUri,
    createdAt = createdAt
)

fun Pet.toEntity() = PetEntity(
    id = id,
    name = name,
    type = type.displayName,
    breed = breed,
    dateOfBirth = dateOfBirth,
    weight = weight,
    gender = gender.displayName,
    imageUri = imageUri,
    createdAt = createdAt
)

// Vaccination Mappers
fun VaccinationEntity.toDomain() = Vaccination(
    id = id,
    petId = petId,
    vaccineName = vaccineName,
    lastDate = lastDate,
    nextDueDate = nextDueDate,
    reminderEnabled = reminderEnabled,
    notes = notes,
    createdAt = createdAt
)

fun Vaccination.toEntity() = VaccinationEntity(
    id = id,
    petId = petId,
    vaccineName = vaccineName,
    lastDate = lastDate,
    nextDueDate = nextDueDate,
    reminderEnabled = reminderEnabled,
    notes = notes,
    createdAt = createdAt
)

// VetVisit Mappers
fun VetVisitEntity.toDomain() = VetVisit(
    id = id,
    petId = petId,
    clinicName = clinicName,
    doctorName = doctorName,
    date = date,
    reminderEnabled = reminderEnabled,
    prescriptionImageUri = prescriptionImageUri,
    notes = notes,
    createdAt = createdAt
)

fun VetVisit.toEntity() = VetVisitEntity(
    id = id,
    petId = petId,
    clinicName = clinicName,
    doctorName = doctorName,
    date = date,
    reminderEnabled = reminderEnabled,
    prescriptionImageUri = prescriptionImageUri,
    notes = notes,
    createdAt = createdAt
)

// DietSchedule Mappers
fun DietScheduleEntity.toDomain() = DietSchedule(
    id = id,
    petId = petId,
    mealTime = MealTime.entries.find { it.displayName == mealTime } ?: MealTime.MORNING,
    foodType = foodType,
    quantity = quantity,
    reminderEnabled = reminderEnabled,
    createdAt = createdAt
)

fun DietSchedule.toEntity() = DietScheduleEntity(
    id = id,
    petId = petId,
    mealTime = mealTime.displayName,
    foodType = foodType,
    quantity = quantity,
    reminderEnabled = reminderEnabled,
    createdAt = createdAt
)

// GrowthLog Mappers
fun GrowthLogEntity.toDomain() = GrowthLog(
    id = id,
    petId = petId,
    weight = weight,
    date = date,
    notes = notes,
    createdAt = createdAt
)

fun GrowthLog.toEntity() = GrowthLogEntity(
    id = id,
    petId = petId,
    weight = weight,
    date = date,
    notes = notes,
    createdAt = createdAt
)

// Memory Mappers
fun MemoryEntity.toDomain() = Memory(
    id = id,
    petId = petId,
    imageUri = imageUri,
    caption = caption,
    date = date,
    createdAt = createdAt
)

fun Memory.toEntity() = MemoryEntity(
    id = id,
    petId = petId,
    imageUri = imageUri,
    caption = caption,
    date = date,
    createdAt = createdAt
)
