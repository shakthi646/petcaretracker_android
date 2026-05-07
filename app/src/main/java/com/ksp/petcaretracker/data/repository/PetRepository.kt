package com.ksp.petcaretracker.data.repository

import com.ksp.petcaretracker.data.local.dao.PetDao
import com.ksp.petcaretracker.domain.model.Pet
import com.ksp.petcaretracker.domain.model.toDomain
import com.ksp.petcaretracker.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetRepository @Inject constructor(
    private val petDao: PetDao
) {
    fun getAllPets(): Flow<List<Pet>> =
        petDao.getAllPets().map { list -> list.map { it.toDomain() } }

    fun getPetById(petId: Long): Flow<Pet?> =
        petDao.getPetById(petId).map { it?.toDomain() }

    suspend fun getPetByIdOnce(petId: Long): Pet? =
        petDao.getPetByIdOnce(petId)?.toDomain()

    suspend fun upsertPet(pet: Pet): Long =
        petDao.upsertPet(pet.toEntity())

    suspend fun deletePet(petId: Long) =
        petDao.deletePet(petId)

    fun getPetCount(): Flow<Int> =
        petDao.getPetCount()
}
