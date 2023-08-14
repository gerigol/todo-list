package com.gerigol.todoapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gerigol.todoapp.adapter.TodoAdapter
import com.gerigol.todoapp.db.TodoAppDB
import com.gerigol.todoapp.domain.TodoItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var todoAdapter: TodoAdapter
    private val todos: ArrayList<TodoItem> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var todoAppDB: TodoAppDB
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


        GlobalScope.launch(Dispatchers.IO) {
            displayAllTodoInBackground()
        }

        todoAdapter = TodoAdapter(this, todos)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = todoAdapter

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO){
                addTodo()
            }
        }

    }

    private val myCallBack = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            GlobalScope.launch(Dispatchers.IO){
                displayAllTodoInBackground()
            }
            Log.i("TAG", "Database has been Created")
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            Log.i("TAG", "Database has been Opened")
        }
    }

    @SuppressLint("SetTextI18n")
    fun addTodo() {
        val layoutInflater = LayoutInflater.from(applicationContext)
        val view: View = layoutInflater.inflate(R.layout.add_todo_item, null)

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(view)

        val todoTag: TextView = view.findViewById(R.id.new_todo_title)
        val todoTitle: EditText = view.findViewById(R.id.title)
        val todoDescription: EditText = view.findViewById(R.id.description)

        todoTag.text = getString(R.string.add_new_todo)

        alertDialogBuilder.setCancelable(false)
            .setPositiveButton("Save") { dialogInterface, _ ->
                if (TextUtils.isEmpty(todoTitle.text.toString())) {
                    Toast.makeText(this, getString(R.string.enter_title), Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                } else {
                    dialogInterface.dismiss()
                }

                runOnUiThread {
                   createTodo(todoTitle.text.toString(), todoDescription.text.toString())
                }
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.cancel()
            }

        runOnUiThread {
            val alertDialog: AlertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

    fun editTodo(todo: TodoItem) {
        //TODO: implement it
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

    private fun createTodo(title: String, description: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val id = todoAppDB.todoItemDao()
                .addTodoItem(TodoItem(title, description))

            val todo = todoAppDB.todoItemDao().getTodoItem(id)

            todo?.let {
                GlobalScope.launch(Dispatchers.Main) {
                    todos.add(0, it)
                    todoAdapter.notifyDataSetChanged()
                }
            }
        }
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