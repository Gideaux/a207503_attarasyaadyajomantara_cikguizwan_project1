package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data

import kotlinx.coroutines.flow.Flow

/**
 * Abstraction between the ViewModel and the DAO.
 *
 * The ViewModel only knows about this interface, so the data source could be
 * swapped (e.g. for an in-memory fake in tests) without changing UI code.
 */
interface KouleejRepository {
    fun getAllKouleejesStream(): Flow<List<KouleejEntity>>
    fun getKouleejStream(id: Int): Flow<KouleejEntity?>
    suspend fun insertKouleej(kouleej: KouleejEntity)
    suspend fun updateKouleej(kouleej: KouleejEntity)
    suspend fun deleteKouleej(kouleej: KouleejEntity)
}

/**
 * Room-backed implementation of [KouleejRepository]. Just forwards every call
 * to the corresponding DAO method.
 */
class OfflineKouleejRepository(
    private val kouleejDao: KouleejDao
) : KouleejRepository {

    override fun getAllKouleejesStream(): Flow<List<KouleejEntity>> =
        kouleejDao.getAllKouleejes()

    override fun getKouleejStream(id: Int): Flow<KouleejEntity?> =
        kouleejDao.getKouleej(id)

    override suspend fun insertKouleej(kouleej: KouleejEntity) =
        kouleejDao.insert(kouleej)

    override suspend fun updateKouleej(kouleej: KouleejEntity) =
        kouleejDao.update(kouleej)

    override suspend fun deleteKouleej(kouleej: KouleejEntity) =
        kouleejDao.delete(kouleej)
}
