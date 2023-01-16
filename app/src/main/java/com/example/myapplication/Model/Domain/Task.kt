package com.example.myapplication.Model.Domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "Tasks")
data class Task (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "description")
    var description: String,
    @ColumnInfo(name = "date")
    var date: String,
    @ColumnInfo(name = "isFavourite")
    var isFavorite: Boolean,
    @ColumnInfo(name = "isSubtaskFor")
    var isSubtaskFor: Int?,
    @ColumnInfo(name = "groupId")
    var groupId: Int?) : java.io.Serializable
