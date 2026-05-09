package com.misw.vinilos.ui.collectors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.misw.vinilos.data.models.Collector
import com.misw.vinilos.databinding.ItemCollectorBinding

class CollectorAdapter(
    private val onClick: (Collector) -> Unit
) : ListAdapter<Collector, CollectorAdapter.CollectorViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectorViewHolder {
        val binding = ItemCollectorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollectorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CollectorViewHolder(private val binding: ItemCollectorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Collector) {
            binding.tvCollectorName.text = item.name
            binding.tvCollectorTelephone.text = item.telephone
            binding.tvCollectorEmail.text = item.email

            binding.root.setOnClickListener { onClick(item) }
        }
    }

    private companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Collector>() {
            override fun areItemsTheSame(oldItem: Collector, newItem: Collector): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Collector, newItem: Collector): Boolean = oldItem == newItem
        }
    }
}

