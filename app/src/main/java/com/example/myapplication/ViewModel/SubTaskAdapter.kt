package com.example.myapplication.ViewModel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Model.Domain.Task
import com.example.myapplication.R
import com.example.myapplication.databinding.SubtaskLayoutBinding

class SubtaskAdapter(val listener: Listener) : RecyclerView.Adapter<SubtaskAdapter.ViewHolder>() {
    val subtaskList = ArrayList<Task>()

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = SubtaskLayoutBinding.bind(item)

        fun bind(task: Task, listener: SubtaskAdapter.Listener, position: Int) = with(binding) {
            subtaskTitle.text = task.title

            subtaskState.setOnClickListener { listener.removeSubtask(task) }
            subtaskTitle.setOnClickListener { listener.goToSubtask(task) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.subtask_layout, parent, false)
        return SubtaskAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subtaskList[position], listener, position)
    }

    override fun getItemCount(): Int {
        return subtaskList.size
    }

    fun addSubtask(task: Task) {
        subtaskList.add(task)
        notifyDataSetChanged()
    }

    interface Listener {
        fun removeSubtask(subtask: Task)
        fun goToSubtask(subtask: Task)
    }
}