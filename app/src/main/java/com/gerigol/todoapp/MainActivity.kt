package com.gerigol.todoapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gerigol.todoapp.adapter.TodoAdapter
import com.gerigol.todoapp.db.TodoAppDB
import com.gerigol.todoapp.domain.TodoItem
import com.gerigol.todoapp.repository.TodoRepository
import com.gerigol.todoapp.viewmodel.TodoViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var todoAdapter: TodoAdapter
    private val todos: MutableList<TodoItem> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var todoAppDB: TodoAppDB
    private lateinit var viewModel: TodoViewModel
    private lateinit var repository: TodoRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.my_todos)

        recyclerView = findViewById(R.id.rvTodoList)
        todoAppDB = Room.databaseBuilder(
            applicationContext,
            TodoAppDB::class.java,
            "TodoDB"
        )
            .addCallback(myCallBack)
            .build()

        repository = TodoRepository(todoAppDB.todoItemDao())

        viewModel = ViewModelProvider(
            this,
            TodoViewModelFactory(repository)
        )[TodoViewModel::class.java]

        lifecycleScope.launch(Dispatchers.IO) {
            displayAllTodoInBackground()
        }

        todoAdapter = TodoAdapter(this, todos)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = todoAdapter

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                showTodoDialog()
            }
        }

        viewModel.todos.observe(this) { todos ->
            todos?.let {
                todoAdapter.updateData(it)
            }
        }

    }

    class TodoViewModelFactory(private val repository: TodoRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TodoViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val myCallBack = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.i("TAG", "Database has been Created")
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            Log.i("TAG", "Database has been Opened")
        }
    }

    @SuppressLint("SetTextI18n")
    fun showTodoDialog() {
        runOnUiThread {
            val layoutInflater = LayoutInflater.from(applicationContext)
            val view: View = layoutInflater.inflate(R.layout.add_todo_item, null)

            val todoTextView: TextView = view.findViewById(R.id.new_todo_title)
            val todoTitle: EditText = view.findViewById(R.id.title)
            val todoDescription: EditText = view.findViewById(R.id.description)

            todoTextView.text = getString(R.string.add_new_todo)

            val alertDialogBuilder = AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("Save") { dialogInterface, _ ->
                    if (todoTitle.text.isNotEmpty()) {
                        addTodoToDatabase(
                            todoTitle.text.toString(),
                            todoDescription.text.toString()
                        )
                    } else {
                        Toast.makeText(
                            this,
                            getString(R.string.enter_title),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setNegativeButton("Cancel") { dialogInterface, _ ->
                    dialogInterface.cancel()
                }

            val alertDialog: AlertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

    private fun addTodoToDatabase(title: String, description: String) {
        viewModel.addTodo(title, description)

    }

    fun editTodo(todo: TodoItem) {
        //TODO: implement edit Todo
    }

    private fun deleteTodo(todo: TodoItem, position: Int) {
        todos.removeAt(position)
        todoAppDB.todoItemDao().deleteTodoItem(todo)
        todoAdapter.notifyDataSetChanged()
    }

    private fun updateTodo(title: String, description: String, position: Int) {
        val todo = todos[position]

        todo.title = title
        todo.description = description

        todoAppDB.todoItemDao().updateTodoItem(todo)
        todos[position] = todo
        todoAdapter.notifyDataSetChanged()
    }


    private fun displayAllTodoInBackground() {
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            todos.addAll(todoAppDB.todoItemDao().getTodoItems())

            handler.post {
                todoAdapter.notifyDataSetChanged()
            }
        }
    }

}