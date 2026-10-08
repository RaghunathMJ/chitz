package com.example.chitassist.core.database.dao

import androidx.room.*
import com.example.chitassist.core.database.entity.ChitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChit(chit: ChitEntity): Long

    @Query("SELECT * FROM chits WHERE userId = :userId ORDER BY createdAt DESC")
    fun getChitsForUser(userId: Long): Flow<List<ChitEntity>>

    @Query("SELECT * FROM chits WHERE id = :chitId LIMIT 1")
    fun getChitById(chitId: Long): Flow<ChitEntity?>

    @Query("SELECT * FROM chits WHERE id = :chitId LIMIT 1")
    suspend fun getChitByIdSync(chitId: Long): ChitEntity?

    @Update
    suspend fun updateChit(chit: ChitEntity)

    @Delete
    suspend fun deleteChit(chit: ChitEntity)
}
