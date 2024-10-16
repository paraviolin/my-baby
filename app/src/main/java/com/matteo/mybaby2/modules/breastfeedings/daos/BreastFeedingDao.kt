package com.matteo.mybaby2.modules.breastfeedings.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.matteo.mybaby2.modules.breastfeedings.entities.BreastFeedingEntity

@Dao
interface BreastFeedingDao {
    @Query("SELECT * FROM breastfeedingentity ORDER BY date DESC")
    suspend fun getAll(): List<BreastFeedingEntity>

    @Query("SELECT * FROM breastfeedingentity WHERE date BETWEEN :startOfDay AND :endOfDay")
    suspend fun getAllByDate(startOfDay: Long, endOfDay: Long): List<BreastFeedingEntity>

    @Upsert
    suspend fun upsertBreastFeeding(breastfeeding: BreastFeedingEntity)

    @Query("SELECT * FROM breastfeedingentity WHERE id = :id")
    suspend fun getBreastFeedingById(id: Int): BreastFeedingEntity?

    @Delete
    suspend fun delete(breastfeeding: BreastFeedingEntity)
}