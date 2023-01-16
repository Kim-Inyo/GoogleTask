package com.example.myapplication.Model.Database.Dao
import androidx.room.*
import com.example.myapplication.Model.Domain.Group

@Dao
interface GroupDao {
    @Query("SELECT * FROM Groups WHERE id = :id LIMIT 1")
    fun GetItem(id: Int): Group

    @Query("SELECT * FROM Groups")
    fun GetAllGroups(): List<Group>

    @Insert
    fun Create(item: Group)

    @Update
    fun Update(item: Group)

    @Delete
    fun Delete(item: Group)
}