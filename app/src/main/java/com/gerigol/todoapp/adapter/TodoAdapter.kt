package com.gerigol.todoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.gerigol.todoapp.MainActivity
import com.gerigol.todoapp.R
import com.gerigol.todoapp.domain.TodoItem

class TodoAdapter(private val mainActivity: MainActivity ,private val todos: List<TodoItem>): RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {


    class TodoViewHolder(itemView: View): ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(LayoutInflater.from(parent.context)
            .inflate(
                R.layout.todo_item,
                parent,
                false
            ))
    }

    override fun getItemCount(): Int {
        return todos.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentTodo: TodoItem = todos[position]
        holder.itemView.findViewById<TextView>(R.id.tvTodoTitle).setText(currentTodo.title)
        holder.itemView.findViewById<TextView>(R.id.tvTodoDescription).setText(currentTodo.description)

        holder.itemView.setOnClickListener(View.OnClickListener {
            mainActivity.editTodo(currentTodo)
        })
    }

}
