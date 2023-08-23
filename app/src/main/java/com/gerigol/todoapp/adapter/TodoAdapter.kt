package com.gerigol.todoapp.adapter

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.gerigol.todoapp.MainActivity
import com.gerigol.todoapp.R
import com.gerigol.todoapp.domain.TodoItem

class TodoAdapter(
    private val mainActivity: MainActivity,
    private val todos: MutableList<TodoItem>
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(itemView: View) : ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.todo_item,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int {
        return todos.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {

        //TODO: Remove all the all the references to  Views and Widges
        //TODO: Use Data Binding

        val currentTodo: TodoItem = todos[position]
        val checkBox: CheckBox = holder.itemView.findViewById(R.id.cbDone)
        val tvTodoTitle = holder.itemView.findViewById<TextView>(R.id.tvTodoTitle)
        val tvTodoDescription = holder.itemView.findViewById<TextView>(R.id.tvTodoDescription)

        tvTodoTitle.setText(currentTodo.title)
        tvTodoDescription.setText(currentTodo.description)
        checkBox.isChecked = currentTodo.isChecked

        toggleStrikeThrough(currentTodo.isChecked, tvTodoTitle, tvTodoDescription)

        checkBox.setOnClickListener {
            currentTodo.isChecked = checkBox.isChecked
            toggleStrikeThrough(currentTodo.isChecked, tvTodoTitle, tvTodoDescription)
            mainActivity.updateTodoInDatabase(currentTodo, currentTodo.isChecked)
        }
        holder.itemView.setOnClickListener {
            mainActivity.showTodoDialog(currentTodo)
        }
    }

    private fun toggleStrikeThrough(isChecked: Boolean, title: TextView, description: TextView) {
        if (isChecked) {
            title.paintFlags = title.paintFlags or STRIKE_THRU_TEXT_FLAG
            description.paintFlags = description.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            title.paintFlags = title.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
            description.paintFlags = description.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()

        }
    }

    fun updateData(newTodos: List<TodoItem>) {
        todos.clear()
        todos.addAll(newTodos)
        notifyDataSetChanged()
    }
}
