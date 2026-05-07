package com.ksp.petcaretracker.data.repository

import com.ksp.petcaretracker.data.local.dao.VaccinationDao
import com.ksp.petcaretracker.domain.model.Vaccination
import com.ksp.petcaretracker.domain.model.toDomain
import com.ksp.petcaretracker.domain.model.toEntity
import com.ksp.petcaretracker.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaccinationRepository @Inject constructor(
    private val vaccinationDao: VaccinationDao
) {
    fun getVaccinationsForPet(petId: Long): Flow<List<Vaccination>> =
        vaccinationDao.getVaccinationsForPet(petId).map { list -> list.map { it.toDomain() } }

    fun getVaccinationById(id: Long): Flow<Vaccination?> =
        vaccinationDao.getVaccinationById(id).map { it?.toDomain() }

    suspend fun getVaccinationByIdOnce(id: Long): Vaccination? =
        vaccinationDao.getVaccinationByIdOnce(id)?.toDomain()

    suspend fun upsertVaccination(vaccination: Vaccination): Long =
        vaccinationDao.upsertVaccination(vaccination.toEntity())

    suspend fun deleteVaccination(id: Long) =
        vaccinationDao.deleteVaccination(id)

    fun getUpcomingVaccinations(): Flow<List<Vaccination>> =
        vaccinationDao.getUpcomingVaccinations(DateUtils.todayStartMillis())
            .map { list -> list.map { it.toDomain() } }
}
