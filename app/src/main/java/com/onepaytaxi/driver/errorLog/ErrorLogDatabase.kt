package com.onepaytaxi.driver.errorLog

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context

@Database(entities = [ApiErrorModel::class], version = 1,exportSchema = false)
@TypeConverters(Converter::class)
abstract class ErrorLogDatabase : RoomDatabase() {
    abstract fun errorLogDao(): ErrorLogDao

    companion object {
        @Volatile
        private lateinit var errorLogDatabase: ErrorLogDatabase

        @JvmStatic
        fun getDatabase(context: Context): ErrorLogDatabase{
                synchronized(ErrorLogDatabase::class.java) {
                        errorLogDatabase = Room.databaseBuilder(context.applicationContext,
                                ErrorLogDatabase::class.java, "errorLogDatabase")
                                .build()
                }
            return errorLogDatabase
        }
    }
}