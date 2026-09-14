package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.PinProject

@Database(entities = [PinProject::class], version = 1, exportSchema = false)
abstract class PinDatabase : RoomDatabase() {
    abstract fun pinProjectDao(): PinProjectDao

    companion object {
        @Volatile
        private var INSTANCE: PinDatabase? = null

        fun getDatabase(context: Context): PinDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PinDatabase::class.java,
                    "pincraft_ai_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
