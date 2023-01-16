package com.example.myapplication.ViewModel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Model.Domain.Group
import com.example.myapplication.R
import com.example.myapplication.databinding.GroupLayoutBinding
import java.util.*
import kotlin.collections.ArrayList

class GroupAdapter(val listener: Listener) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {
    val groupList = ArrayList<Group>()
    var locGroup: Group = Group(-1, "")

    class ViewHolder(item: View): RecyclerView.ViewHolder(item) {
        val binding = GroupLayoutBinding.bind(item)

        fun bind(group: Group, listener: Listener, position: Int) = with(binding) {
            groupTitle.text = group.name

            groupTitle.setOnClickListener {
                listener.setFilteredGroup(group)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.group_layout, parent, false)
        return GroupAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(groupList[position], listener, position);
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    interface Listener {
        fun removeGroup(group: Group)
        fun setFilteredGroup(group: Group)
    }
}