package com.gerigol.todoapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gerigol.todoapp.adapter.TodoAdapter
import com.gerigol.todoapp.databinding.ActivityMainBinding
import com.gerigol.todoapp.db.TodoAppDB
import com.gerigol.todoapp.domain.TodoItem
import com.gerigol.todoapp.repository.TodoRepository
import com.gerigol.todoapp.viewmodel.TodoViewModel

class MainActivity : AppCompatActivity() {

    //For Commit!!!!

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: TodoViewModel


        private val todos: MutableList<TodoItem> = ArrayList()
        private lateinit var todoAdapter: TodoAdapter
        private lateinit var recyclerView: RecyclerView

    private lateinit var todoAppDB: TodoAppDB
    private lateinit var repository: TodoRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupViewModel()
        setContentView(binding.root)
        setupViews()
        loadTodos()



    }

    private fun setupViews() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.my_todos)

        val recyclerView = binding.include.rvTodoList
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.itemAnimator = DefaultItemAnimator()

        todoAdapter = TodoAdapter(this, todos)
        recyclerView.adapter = todoAdapter

        val fabAdd: Button = binding.fabAdd
        fabAdd.setOnClickListener {
            showTodoDialog()
        }

        val fabDeleteDone: Button = binding.fabDeleteDone
        fabDeleteDone.setOnClickListener {
            showDeleteConfirmDialog()
        }
    }


    private fun setupViewModel() {
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
    }


    private fun loadTodos() {
        viewModel.todos.observe(
            this
        ) { todos ->
            todos?.let { todoAdapter.updateData(it) }
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
    private var editingTodo: TodoItem? = null


    @SuppressLint("SetTextI18n")
    fun showTodoDialog(todoItem: TodoItem? = null) {
        val dialogView = createTodoDialogView(todoItem)
        val alertDialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("Save") { dialogInterface, _ ->
                handleSaveButtonClick(dialogView)
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showDeleteConfirmDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
            .setTitle("Delete Done Todos")
            .setMessage("Do you want to delete all done todos?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, which ->
                viewModel.deleteDoneTodos()
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun createTodoDialogView(todoItem: TodoItem?): View {
        val layoutInflater = LayoutInflater.from(applicationContext)
        val view: View = layoutInflater.inflate(R.layout.add_todo_item, null)
        val todoTextView: TextView = view.findViewById(R.id.new_todo_title)
        val todoTitle: EditText = view.findViewById(R.id.title)
        val todoDescription: EditText = view.findViewById(R.id.description)
        val nameTitle: String = todoItem?.let {
            todoTitle.setText(it.title)
            todoDescription.setText(it.description)
            editingTodo = it
            "Edit Todo"
        } ?: "Add new todo"
        todoTextView.text = nameTitle

        return view
    }

    private fun handleSaveButtonClick(dialogView: View) {
        val todoTitle: EditText = dialogView.findViewById(R.id.title)
        val todoDescription: EditText = dialogView.findViewById(R.id.description)

        if (todoTitle.text.isNotEmpty()) {
            if (editingTodo != null) {
                updateTodoInDatabase(
                    editingTodo!!,
                    todoTitle.text.toString(),
                    todoDescription.text.toString()
                )
            } else {
                addTodoToDatabase(todoTitle.text.toString(), todoDescription.text.toString())
            }
        } else {
            showToast(getString(R.string.enter_title))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun updateTodoInDatabase(todoItem: TodoItem, title: String, description: String) {
        viewModel.updateTodo(todoItem, title, description)
    }

    fun updateTodoInDatabase(todoItem: TodoItem, isChecked: Boolean) {
        viewModel.updateTodo(todoItem, isChecked)
    }

    private fun addTodoToDatabase(title: String, description: String) {
        viewModel.addTodo(title, description)
    }
}