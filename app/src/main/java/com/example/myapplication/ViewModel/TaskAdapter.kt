package com.example.myapplication.ViewModel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Model.Domain.Task
import com.example.myapplication.R
import com.example.myapplication.databinding.TaskLayoutBinding

class TaskAdapter(val listener: Listener): RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    val taskList = ArrayList<Task>()
    var filteredGroupId: Int? = 0

    class ViewHolder(item: View): RecyclerView.ViewHolder(item) {
        val binding = TaskLayoutBinding.bind(item)

        fun bind(task: Task, listener: Listener) = with(binding){
            taskState.setOnClickListener { listener.removeTask(task) }

            taskTitle.setOnClickListener { listener.goToTask(task) }

            toFavorite.setOnClickListener {
                listener.addToFavourite(task)
                task.isFavorite = !task.isFavorite
            }

            taskTitle.text = task.title
            creationTime.text = task.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(taskList[position], listener);
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    interface Listener {
        fun addToFavourite(task: Task)
        fun removeTask(task: Task)
        fun goToTask(task: Task)
    }
}