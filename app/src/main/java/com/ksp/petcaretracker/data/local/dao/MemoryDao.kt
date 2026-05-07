package com.ksp.petcaretracker.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ksp.petcaretracker.data.local.entity.MemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {

    @Query("SELECT * FROM memories WHERE petId = :petId ORDER BY date DESC")
    fun getMemoriesForPet(petId: Long): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE id = :id")
    fun getMemoryById(id: Long): Flow<MemoryEntity?>

    @Upsert
    suspend fun upsertMemory(memory: MemoryEntity): Long

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemory(id: Long)

    @Query("SELECT COUNT(*) FROM memories WHERE petId = :petId")
    fun getMemoryCount(petId: Long): Flow<Int>
}
