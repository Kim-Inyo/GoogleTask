package com.example.myapplication.Model.Database.Dao
import androidx.room.*
import com.example.myapplication.Model.Domain.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM Tasks WHERE id = :id LIMIT 1")
    fun GetItem(id: Int): Task

    @Query("SELECT * FROM Tasks WHERE isSubtaskFor = -1")
    fun GetAllTasks(): List<Task>

    @Query("SELECT * FROM Tasks WHERE groupId = :id")
    fun GetItemsByGroupId(id: Int): List<Task>

    @Query("SELECT * FROM Tasks WHERE isSubtaskFor = :id")
    fun GetItemsBySubtaskFor(id: Int): List<Task>

    @Query("SELECT * FROM Tasks WHERE isFavourite = 1")
    fun GetItemsByFavourite(): List<Task>

    @Insert
    fun Create(item: Task)

    @Update
    fun Update(item: Task)

    @Delete
    fun Delete(item: Task)

    @Query("DELETE FROM Tasks WHERE groupId = :id")
    fun DeleteAllTasksByGroup(id: Int)
}