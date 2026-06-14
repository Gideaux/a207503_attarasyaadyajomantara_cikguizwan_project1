package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [KouleejEntity].
 *
 * Room generates the implementation at compile time via KSP. Reads return Flow
 * so the UI updates automatically when the underlying database changes.
 */
@Dao
interface KouleejDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(kouleej: KouleejEntity)

    @Update
    suspend fun update(kouleej: KouleejEntity)

    @Delete
    suspend fun delete(kouleej: KouleejEntity)

    @Query("SELECT * FROM kouleejes WHERE id = :id")
    fun getKouleej(id: Int): Flow<KouleejEntity?>

    @Query("SELECT * FROM kouleejes ORDER BY id ASC")
    fun getAllKouleejes(): Flow<List<KouleejEntity>>
}
