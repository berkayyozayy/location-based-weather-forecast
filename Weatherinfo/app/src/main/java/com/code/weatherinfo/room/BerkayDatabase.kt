package com.code.weatherinfo.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = arrayOf(User::class), version = 1, exportSchema = false)
abstract class BerkayDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        private var INSTANCE: BerkayDatabase? = null
        fun getDatabase(context: Context): BerkayDatabase? {
            if (INSTANCE == null) {
                synchronized(BerkayDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        BerkayDatabase::class.java, "Berkay.db").build()
                }
            }
            return INSTANCE
        }
    }
}