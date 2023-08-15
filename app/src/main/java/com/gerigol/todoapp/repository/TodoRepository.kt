package com.gerigol.todoapp.repository

import androidx.lifecycle.LiveData
import com.gerigol.todoapp.dao.TodoItemDao
import com.gerigol.todoapp.domain.TodoItem

class TodoRepository(private val todoItemDao: TodoItemDao) {

    fun getAllTodoItems(): List<TodoItem> {
        return todoItemDao.getTodoItems()
    }

    fun addTodoItem(todoItem: TodoItem) {
        todoItemDao.addTodoItem(todoItem)
    }

    fun updateTodoItem(todoItem: TodoItem) {
        todoItemDao.updateTodoItem(todoItem)
    }

    fun deleteTodoItem(todoItem: TodoItem) {
        todoItemDao.deleteTodoItem(todoItem)
    }

    fun getAllTodoItemsLiveData(): LiveData<List<TodoItem>> {
        return todoItemDao.getTodoItemsLiveData()
    }

    fun deleteDoneTodos() {
        todoItemDao.deleteDoneTodos()
    }
}