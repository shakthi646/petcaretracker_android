package com.ksp.petcaretracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ksp.petcaretracker.data.local.converter.Converters
import com.ksp.petcaretracker.data.local.dao.DietScheduleDao
import com.ksp.petcaretracker.data.local.dao.GrowthLogDao
import com.ksp.petcaretracker.data.local.dao.MemoryDao
import com.ksp.petcaretracker.data.local.dao.PetDao
import com.ksp.petcaretracker.data.local.dao.VaccinationDao
import com.ksp.petcaretracker.data.local.dao.VetVisitDao
import com.ksp.petcaretracker.data.local.entity.DietScheduleEntity
import com.ksp.petcaretracker.data.local.entity.GrowthLogEntity
import com.ksp.petcaretracker.data.local.entity.MemoryEntity
import com.ksp.petcaretracker.data.local.entity.PetEntity
import com.ksp.petcaretracker.data.local.entity.VaccinationEntity
import com.ksp.petcaretracker.data.local.entity.VetVisitEntity

@Database(
    entities = [
        PetEntity::class,
        VaccinationEntity::class,
        VetVisitEntity::class,
        DietScheduleEntity::class,
        GrowthLogEntity::class,
        MemoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PetCareDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
    abstract fun vaccinationDao(): VaccinationDao
    abstract fun vetVisitDao(): VetVisitDao
    abstract fun dietScheduleDao(): DietScheduleDao
    abstract fun growthLogDao(): GrowthLogDao
    abstract fun memoryDao(): MemoryDao

    companion object {
        const val DATABASE_NAME = "petcare_db"
    }
}
