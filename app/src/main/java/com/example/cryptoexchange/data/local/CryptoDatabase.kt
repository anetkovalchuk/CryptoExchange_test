package com.example.cryptoexchange.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [CryptoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CryptoDatabase : RoomDatabase() {

    abstract fun cryptoDao(): CryptoDao

    companion object {

        @Volatile
        private var INSTANCE: CryptoDatabase? = null

        fun getDatabase(
            context: Context
        ): CryptoDatabase {

            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CryptoDatabase::class.java,
                    "crypto_database"
                )

                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}