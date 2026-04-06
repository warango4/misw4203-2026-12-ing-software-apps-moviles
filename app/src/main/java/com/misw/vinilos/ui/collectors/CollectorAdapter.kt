package com.misw.vinilos.ui.collectors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.misw.vinilos.data.models.Collector
import com.misw.vinilos.databinding.ItemCollectorBinding

class CollectorAdapter(
    private var collectors: List<Collector>,
    private val onClick: (Collector) -> Unit
) : RecyclerView.Adapter<CollectorAdapter.CollectorViewHolder>() {

    fun updateData(newCollectors: List<Collector>) {
        collectors = newCollectors
        notifyDataSetDataSet()
    }

    private fun notifyDataSetDataSet() {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectorViewHolder {
        val binding = ItemCollectorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CollectorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollectorViewHolder, position: Int) {
        val collector = collectors[position]
        holder.bind(collector)
        holder.itemView.setOnClickListener { onClick(collector) }
    }

    override fun getItemCount(): Int = collectors.size

    class CollectorViewHolder(val binding: ItemCollectorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(collector: Collector) {
            binding.tvCollectorName.text = collector.name
            binding.tvCollectorPhone.text = collector.telephone
            binding.tvCollectorEmail.text = collector.email
        }
    }
}

