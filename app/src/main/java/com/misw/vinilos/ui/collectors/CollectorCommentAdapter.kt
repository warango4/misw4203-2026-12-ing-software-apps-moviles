package com.misw.vinilos.ui.collectors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.misw.vinilos.data.models.CollectorComment
import com.misw.vinilos.R
import com.misw.vinilos.databinding.ItemCollectorCommentBinding

class CollectorCommentAdapter : ListAdapter<CollectorComment, CollectorCommentAdapter.VH>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCollectorCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(private val binding: ItemCollectorCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CollectorComment) {
            val ctx = binding.root.context
            binding.tvCommentAlbum.text = ctx.getString(R.string.collector_comment_title)
            binding.tvCommentRating.text = ctx.getString(R.string.collector_rating_format, item.rating)
            binding.tvCommentDescription.text = item.description
        }
    }

    private companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<CollectorComment>() {
            override fun areItemsTheSame(oldItem: CollectorComment, newItem: CollectorComment): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: CollectorComment, newItem: CollectorComment): Boolean =
                oldItem == newItem
        }
    }
}

