package com.ksp.petcaretracker.data.repository

import com.ksp.petcaretracker.data.local.dao.MemoryDao
import com.ksp.petcaretracker.domain.model.Memory
import com.ksp.petcaretracker.domain.model.toDomain
import com.ksp.petcaretracker.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryRepository @Inject constructor(
    private val memoryDao: MemoryDao
) {
    fun getMemoriesForPet(petId: Long): Flow<List<Memory>> =
        memoryDao.getMemoriesForPet(petId).map { list -> list.map { it.toDomain() } }

    fun getMemoryById(id: Long): Flow<Memory?> =
        memoryDao.getMemoryById(id).map { it?.toDomain() }

    suspend fun upsertMemory(memory: Memory): Long =
        memoryDao.upsertMemory(memory.toEntity())

    suspend fun deleteMemory(id: Long) =
        memoryDao.deleteMemory(id)

    fun getMemoryCount(petId: Long): Flow<Int> =
        memoryDao.getMemoryCount(petId)
}
