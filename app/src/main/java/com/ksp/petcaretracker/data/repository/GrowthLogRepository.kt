package com.ksp.petcaretracker.data.repository

import com.ksp.petcaretracker.data.local.dao.GrowthLogDao
import com.ksp.petcaretracker.domain.model.GrowthLog
import com.ksp.petcaretracker.domain.model.toDomain
import com.ksp.petcaretracker.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GrowthLogRepository @Inject constructor(
    private val growthLogDao: GrowthLogDao
) {
    fun getGrowthLogsForPet(petId: Long): Flow<List<GrowthLog>> =
        growthLogDao.getGrowthLogsForPet(petId).map { list -> list.map { it.toDomain() } }

    fun getGrowthLogById(id: Long): Flow<GrowthLog?> =
        growthLogDao.getGrowthLogById(id).map { it?.toDomain() }

    suspend fun upsertGrowthLog(growthLog: GrowthLog): Long =
        growthLogDao.upsertGrowthLog(growthLog.toEntity())

    suspend fun deleteGrowthLog(id: Long) =
        growthLogDao.deleteGrowthLog(id)

    suspend fun getLatestGrowthLog(petId: Long): GrowthLog? =
        growthLogDao.getLatestGrowthLog(petId)?.toDomain()
}
