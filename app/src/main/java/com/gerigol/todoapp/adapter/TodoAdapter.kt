package com.gerigol.todoapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.gerigol.todoapp.R
import com.gerigol.todoapp.databinding.TodoItemBinding
import com.gerigol.todoapp.domain.TodoItem

class TodoAdapter(
    private var todos: ArrayList<TodoItem>
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(private var todoItemBinding: TodoItemBinding) : ViewHolder(todoItemBinding.root) {
        fun bind(todoItem: TodoItem) {
            todoItemBinding.todo = todoItem
            todoItemBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val todoItemBinding = DataBindingUtil.inflate<TodoItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.todo_item,
            parent,
            false
        )
        return TodoViewHolder(todoItemBinding)
    }

    override fun getItemCount(): Int {
        return todos.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentTodo: TodoItem = todos[position]

        holder.bind(currentTodo)
    }

    fun setTodos(newTodos: ArrayList<TodoItem>) {
        todos = newTodos
        notifyDataSetChanged()
    }

//    private fun toggleStrikeThrough(isChecked: Boolean, title: TextView, description: TextView) {
//        if (isChecked) {
//            title.paintFlags = title.paintFlags or STRIKE_THRU_TEXT_FLAG
//            description.paintFlags = description.paintFlags or STRIKE_THRU_TEXT_FLAG
//        } else {
//            title.paintFlags = title.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
//            description.paintFlags = description.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
//
//        }
//    }


}
