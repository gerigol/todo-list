package com.gerigol.todoapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gerigol.todoapp.dao.TodoItemDao
import com.gerigol.todoapp.domain.TodoItem

@Database(entities = [TodoItem::class], version = 1)
abstract class TodoAppDB: RoomDatabase() {
    abstract fun todoItemDao(): TodoItemDao
}