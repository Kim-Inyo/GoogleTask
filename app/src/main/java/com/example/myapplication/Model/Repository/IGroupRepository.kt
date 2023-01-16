package com.example.myapplication.Model.Repository

import androidx.room.Dao
import com.example.myapplication.Model.Domain.Group

@Dao
interface IGroupRepository : IRepository<Group> {
    suspend fun GetAllGroups(): List<Group>
}