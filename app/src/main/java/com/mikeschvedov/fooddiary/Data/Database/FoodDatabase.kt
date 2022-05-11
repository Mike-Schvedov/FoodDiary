package com.mikeschvedov.fooddiary.Data.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mikeschvedov.fooddiary.Data.Database.Daos.FoodEntryDao
import com.mikeschvedov.fooddiary.Data.Database.Daos.WaterEntryDao
import com.mikeschvedov.fooddiary.Data.Database.Daos.WeightEntryDao
import com.mikeschvedov.fooddiary.Data.Database.Entities.FoodEntry
import com.mikeschvedov.fooddiary.Data.Database.Entities.WaterEntry
import com.mikeschvedov.fooddiary.Data.Database.Entities.WeightEntry

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [FoodEntry::class, WaterEntry::class, WeightEntry::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
public abstract class FoodDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodEntryDao
    abstract fun waterDao(): WaterEntryDao
    abstract fun weightDao(): WeightEntryDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: FoodDatabase? = null

        fun getDatabase(
            context: Context
        ): FoodDatabase {
            // if the INSTANCE is not null, then return it, if it is, then create the database.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodDatabase::class.java,
                    "word_database"
                ).build()

                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}