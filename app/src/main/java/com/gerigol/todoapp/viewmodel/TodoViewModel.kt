package com.gerigol.todoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gerigol.todoapp.domain.TodoItem
import com.gerigol.todoapp.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {
    val todos: LiveData<List<TodoItem>> = repository.getAllTodoItemsLiveData()

    fun addTodo(title: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTodoItem(TodoItem(title, description))
        }
    }

    fun updateTodo(todoItem: TodoItem, title: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTodoItem(todoItem.copy(title = title, description = description))
        }
    }

    fun updateTodo(todoItem: TodoItem, isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTodoItem(todoItem.copy(isChecked = isChecked))
        }
    }

    fun deleteDoneTodos() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDoneTodos()
        }
    }
}
