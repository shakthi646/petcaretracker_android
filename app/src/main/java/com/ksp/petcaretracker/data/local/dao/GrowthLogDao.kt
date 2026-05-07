package com.ksp.petcaretracker.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ksp.petcaretracker.data.local.entity.GrowthLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrowthLogDao {

    @Query("SELECT * FROM growth_logs WHERE petId = :petId ORDER BY date ASC")
    fun getGrowthLogsForPet(petId: Long): Flow<List<GrowthLogEntity>>

    @Query("SELECT * FROM growth_logs WHERE id = :id")
    fun getGrowthLogById(id: Long): Flow<GrowthLogEntity?>

    @Upsert
    suspend fun upsertGrowthLog(growthLog: GrowthLogEntity): Long

    @Query("DELETE FROM growth_logs WHERE id = :id")
    suspend fun deleteGrowthLog(id: Long)

    @Query("SELECT * FROM growth_logs WHERE petId = :petId ORDER BY date DESC LIMIT 1")
    suspend fun getLatestGrowthLog(petId: Long): GrowthLogEntity?
}
