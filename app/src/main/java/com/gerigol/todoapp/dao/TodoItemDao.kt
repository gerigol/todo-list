package com.gerigol.todoapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

import androidx.room.Update
import com.gerigol.todoapp.domain.TodoItem


@Dao
interface TodoItemDao {
    @Insert
    fun addTodoItem(TodoItem: TodoItem?): Long

    @Update
    fun updateTodoItem(TodoItem: TodoItem?)

    @Delete
    fun deleteTodoItem(TodoItem: TodoItem?)


    @Query("select * from todos")
    fun getTodoItems(): List<TodoItem?>?

    @Query("select * from todos where id ==:TodoItemId")
    fun getTodoItem(TodoItemId: Long): TodoItem?

}