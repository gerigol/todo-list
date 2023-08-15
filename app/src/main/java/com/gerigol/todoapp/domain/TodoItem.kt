package com.gerigol.todoapp.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoItem(
    @ColumnInfo("title")
    var title: String,

    @ColumnInfo("description")
    var description: String,

    @ColumnInfo("is_checked")
    var isChecked: Boolean = false,

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)