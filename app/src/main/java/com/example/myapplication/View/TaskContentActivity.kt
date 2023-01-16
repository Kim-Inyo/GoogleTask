package com.example.myapplication.View

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Model.Database.DB.MainDB
import com.example.myapplication.Model.Database.Repository.*
import com.example.myapplication.Model.Domain.*
import com.example.myapplication.R
import com.example.myapplication.ViewModel.SubtaskAdapter
import com.example.myapplication.databinding.TaskContentLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TaskContentActivity : AppCompatActivity(), SubtaskAdapter.Listener {
    private lateinit var binding: TaskContentLayoutBinding
    private val subtaskAdapter = SubtaskAdapter(this)
    lateinit var locTask: Task
    lateinit var db: MainDB
    lateinit var taskRepo: TaskRepository

    val subtaskObserveData: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val TIMEPATTERN = "dd-MM HH:mm"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TaskContentLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = MainDB.getDb(this@TaskContentActivity)
        taskRepo = TaskRepository(db)

        locTask = intent.getSerializableExtra("taskItem") as Task

        val subtaskObserver = Observer<Int> {
            subtaskAdapter.notifyDataSetChanged()
        }

        subtaskObserveData.observe(this@TaskContentActivity, subtaskObserver)

        subtaskObserveData.value = 0

        initSubtasks()

        binding.apply {
            subtaskList.layoutManager = LinearLayoutManager(this@TaskContentActivity)
            subtaskList.adapter = subtaskAdapter

            taskTitleContent.text = locTask.title
            taskContent.text = locTask.description

            addSubtaskBtn.setOnClickListener { SubtaskCreationWindow() }

            editTaskBtn.setOnClickListener { editTaskWindow() }
        }
        var backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener { goToMain() }
    }

    fun SubtaskCreationWindow() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.create_task_layout, null)
        val tskNm = dialogLayout.findViewById<EditText>(R.id.taskTitleInput)
        val tskDesc = dialogLayout.findViewById<EditText>(R.id.taskDescriptionInput)

        with(builder) {
            setTitle("Create Subtask")
            setPositiveButton("ok") {dialog, which ->
                if (tskNm.text.toString() != "")
                    createSubtask(tskNm.text.toString(), tskDesc.text.toString())
            }
            setNegativeButton("exit") { dialog, which -> }
            setView(dialogLayout)
            show()
        }
    }

    fun editTaskWindow() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.create_task_layout, null)
        val tskNm = dialogLayout.findViewById<EditText>(R.id.taskTitleInput)
        val tskDesc = dialogLayout.findViewById<EditText>(R.id.taskDescriptionInput)

        with(builder) {
            setTitle("Edit Task")
            setPositiveButton("ok") {dialog, which ->
                if (tskNm.text.toString() != "")
                    locTask.title = tskNm.text.toString()
                if (tskDesc.text.toString() != "")
                    locTask.description = tskDesc.text.toString()
                refreshTask()
            }
            setNegativeButton("exit") { dialog, which -> }
            setView(dialogLayout)
            show()
        }
    }

    fun createSubtask(title: String, description: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                taskRepo.Create(
                    Task(
                        null,
                        title,
                        description,
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMEPATTERN)).toString(),
                        false,
                        locTask.id,
                        0
                    )
                )
            }
            initSubtasks()
        }
    }

    fun refreshTask() {
        binding.taskTitleContent.text = locTask.title
        binding.taskContent.text = locTask.description

        CoroutineScope(Dispatchers.IO).launch {
            taskRepo.Update(locTask)
        }
    }

    override fun removeSubtask(subtask: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            taskRepo.Delete(subtask)
            initSubtasks()
        }
    }

    fun initSubtasks() {
        CoroutineScope(Dispatchers.IO).launch {
            subtaskAdapter.subtaskList.clear()
            subtaskAdapter.subtaskList.addAll(taskRepo.GetItemsBySubtaskFor(locTask.id!!))
            subtaskObserveData.postValue(subtaskObserveData.value!! + 1)
        }
    }

    override fun goToSubtask(subtask: Task) {
        startActivity(Intent(this, TaskContentActivity::class.java).apply {
            putExtra("taskItem", subtask)
        })
    }

    fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}