package com.example.myapplication.View

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Model.Database.DB.MainDB
import com.example.myapplication.Model.Database.Repository.*
import com.example.myapplication.Model.Domain.Group
import com.example.myapplication.Model.Domain.Task
import com.example.myapplication.R
import com.example.myapplication.ViewModel.*
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity(), TaskAdapter.Listener, GroupAdapter.Listener {
    lateinit var binding: ActivityMainBinding
    private val taskAdapter = TaskAdapter(this)
    private val groupAdapter = GroupAdapter(this)

    lateinit var db: MainDB
    lateinit var taskRepo: TaskRepository
    lateinit var groupRepo: GroupRepository

    val taskObserveData: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val groupObserveData: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val TIMEPATTERN = "dd-MM HH:mm"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    override fun onResume() {
        super.onResume()
        initTasks()
        initGroups()
    }

    private fun init() {
        binding.apply {
            db = MainDB.getDb(this@MainActivity)
            taskRepo = TaskRepository(db)
            groupRepo = GroupRepository(db)

            tasksList.layoutManager = LinearLayoutManager(this@MainActivity)
            tasksList.adapter = taskAdapter

            groupList.layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            groupList.adapter = groupAdapter

            addTaskIcon.setOnClickListener { openCreationOfTask() }
            addList.setOnClickListener { openCreationOfList() }
            task0Title.setOnClickListener { setFilteredGroup(Group(0, "")) }
            favIconBtn.setOnClickListener {
                taskAdapter.filteredGroupId = -1
                initTasks()
            }
            removeList.setOnClickListener { openDeletionOfGroup() }

            val taskObserver = Observer<Int> {
                taskAdapter.notifyDataSetChanged()
            }

            val groupObserver = Observer<Int> {
                groupAdapter.notifyDataSetChanged()
            }

            taskObserveData.observe(this@MainActivity, taskObserver)
            groupObserveData.observe(this@MainActivity, groupObserver)

            taskObserveData.value = 0
            groupObserveData.value = 0
        }
    }

    fun createTask(title: String, description: String) {
        CoroutineScope(Dispatchers.IO).launch {
            taskRepo.Create(
                Task(
                    null,
                    title,
                    description,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMEPATTERN)).toString(),
                    false,
                    -1,
                    taskAdapter.filteredGroupId
                )
            )
            initTasks()
        }
    }

    fun createGroup(name: String) {
        CoroutineScope(Dispatchers.IO).launch {
            groupRepo.Create(Group(null, name))
            initGroups()
        }
    }

    fun initTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            taskAdapter.taskList.clear()

            if (taskAdapter.filteredGroupId == -1) {
                taskAdapter.taskList.addAll(taskRepo.GetItemsByFavourite())
            } else if (taskAdapter.filteredGroupId == 0) {
                taskAdapter.taskList.addAll(taskRepo.GetAllTasks())
            } else if (taskAdapter.filteredGroupId != null) {
                taskAdapter.taskList.addAll(taskRepo.GetItemsByGroupId(taskAdapter.filteredGroupId!!))
            }

            taskObserveData.postValue(taskObserveData.value!! + 1)
        }
    }

    fun initGroups() {
        CoroutineScope(Dispatchers.IO).launch {
            groupAdapter.groupList.clear()
            groupAdapter.groupList.addAll(groupRepo.GetAllGroups())
            groupObserveData.postValue(groupObserveData.value!! + 1)
        }
    }

    fun openCreationOfTask() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.create_task_layout, null)
        val tskNm = dialogLayout.findViewById<EditText>(R.id.taskName)
        val tskDesc = dialogLayout.findViewById<EditText>(R.id.taskDescription)

        with(builder) {
            setTitle("Create task")
            setPositiveButton("ok") { dialog, which ->
                if (tskNm.text.toString() != "")
                    createTask(tskNm.text.toString(), tskDesc.text.toString())
            }
            setNegativeButton("exit") { dialog, which -> }
            setView(dialogLayout)
            show()
        }
    }

    fun openCreationOfList() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.create_group_layout, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.listName)

        with(builder) {
            setTitle("Create group")
            setPositiveButton("ok") { dialog, which ->
                if (editText.text.toString() != "")
                    createGroup(editText.text.toString())
            }
            setNegativeButton("cancel") { dialog, which -> }
            setView(dialogLayout)
            show()
        }
    }

    fun openDeletionOfGroup() {
        val builder = AlertDialog.Builder(this)

        with(builder) {
            setTitle("Delete group")
            setPositiveButton("ok") { dialog, which ->
                removeGroup(groupAdapter.locGroup)
            }
            setNegativeButton("cancel") { dialog, which -> }
            show()
        }
    }

    override fun addToFavourite(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            taskRepo.Update(task)
            initTasks()
        }
    }

    override fun removeGroup(group: Group) {
        CoroutineScope(Dispatchers.IO).launch {
            groupRepo.Delete(group)
            taskRepo.DeleteAllTasksByGroup(group.id!!)
            initGroups()
        }
    }

    override fun setFilteredGroup(group: Group) {
        taskAdapter.filteredGroupId = group.id
        groupAdapter.locGroup = group
        val groupTitle = findViewById<TextView>(R.id.groupTitleMain)
        groupTitle.text = group.name
        initTasks()
    }

    override fun removeTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            taskRepo.Delete(task)
            initTasks()
        }
    }

    override fun goToTask(task: Task) {
        startActivity(Intent(this, TaskContentActivity::class.java).apply {
            putExtra("taskItem", task)
        })
    }
}