package com.example.myapplication.Model.Repository

import androidx.room.Dao
import com.example.myapplication.Model.Domain.Task

@Dao
interface ITaskRepository : IRepository<Task> {
    suspend fun GetAllTasks(): List<Task>
    suspend fun GetItemsByGroupId(id: Int): List<Task>
    suspend fun GetItemsBySubtaskFor(id: Int): List<Task>
    suspend fun GetItemsByFavourite(): List<Task>
    suspend fun DeleteAllTasksByGroup(id: Int)
}