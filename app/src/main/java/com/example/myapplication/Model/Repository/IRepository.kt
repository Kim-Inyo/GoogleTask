package com.example.myapplication.Model.Repository

import androidx.room.Dao

@Dao
interface IRepository<T> {
    suspend fun Create(item: T)
    suspend fun Update(item: T)
    suspend fun Delete(item: T)
    suspend fun GetItem(id: Int): T
}