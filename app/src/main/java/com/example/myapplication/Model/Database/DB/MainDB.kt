package com.example.myapplication.Model.Database.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.Model.Database.Dao.GroupDao
import com.example.myapplication.Model.Database.Dao.TaskDao
import com.example.myapplication.Model.Domain.Group
import com.example.myapplication.Model.Domain.Task

@Database (entities = [Task::class, Group::class], version = 1)
abstract class MainDB : RoomDatabase() {
    abstract fun getTaskDao(): TaskDao
    abstract fun getGroupDao(): GroupDao
    companion object {
        fun getDb(context: Context): MainDB {
            return Room.databaseBuilder(
                context.applicationContext,
                MainDB::class.java,
                "DB.db"
            ).build()
        }
    }
}