package com.example.a207503_attarasyaadyajomantara_cikguizwan_project2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The Room database singleton.
 *
 * Holds a single instance of the SQLite database for the whole app, exposed
 * via [getDatabase]. Uses double-checked locking with `@Volatile` so multiple
 * threads can't accidentally create two databases.
 */
@Database(entities = [KouleejEntity::class], version = 1, exportSchema = false)
abstract class KouleejDatabase : RoomDatabase() {

    abstract fun kouleejDao(): KouleejDao

    companion object {
        @Volatile
        private var Instance: KouleejDatabase? = null

        fun getDatabase(context: Context): KouleejDatabase {
            // If Instance already exists, return it; otherwise build it inside
            // a synchronized block to avoid the race condition where two
            // threads both see null and both try to build the database.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context.applicationContext,
                    klass   = KouleejDatabase::class.java,
                    name    = "kouleej_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
