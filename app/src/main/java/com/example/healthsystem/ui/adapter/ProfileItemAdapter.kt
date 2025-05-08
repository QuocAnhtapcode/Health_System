package com.example.healthsystem.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthsystem.databinding.ItemProfileOptionBinding
import com.example.healthsystem.model.ProfileOption

class ProfileAdapter(
    private val items: List<ProfileOption>,
    private val onItemClick: (ProfileOption) -> Unit
) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    inner class ProfileViewHolder(val binding: ItemProfileOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(option: ProfileOption) {
            binding.ivIcon.setImageResource(option.iconRes)
            binding.tvTitle.text = option.title
            binding.root.setOnClickListener { onItemClick(option) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val binding = ItemProfileOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}
