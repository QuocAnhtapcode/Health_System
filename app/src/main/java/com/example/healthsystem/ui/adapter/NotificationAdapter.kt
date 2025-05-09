package com.example.healthsystem.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.healthsystem.databinding.ItemNotificationBinding
import com.example.healthsystem.model.NotificationItem

class NotificationAdapter(private val list: List<NotificationItem>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(val binding: ItemNotificationBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = list[position]
        with(holder.binding) {
            tvTitle.text = item.title
            tvContent.text = item.content
            val color = ContextCompat.getColor(root.context, item.colorResId)
            colorDot.background.setTint(color)
        }
    }


    override fun getItemCount(): Int = list.size
}
