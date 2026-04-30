package com.misw.vinilos.ui.collectors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.misw.vinilos.R
import com.misw.vinilos.data.models.CollectorAlbum
import com.misw.vinilos.databinding.ItemAlbumBinding

class CollectorAlbumAdapter(
    private val onClick: (CollectorAlbum) -> Unit = {}
) : ListAdapter<CollectorAlbum, CollectorAlbumAdapter.VH>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding, onClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        private val binding: ItemAlbumBinding,
        private val onClick: (CollectorAlbum) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CollectorAlbum) {
           val ctx = binding.root.context
            binding.tvAlbumName.text = ctx.getString(R.string.collector_album_title_format, item.albumId)
            val price = item.price?.let { "$it" }
            binding.tvAlbumGenre.text = listOfNotNull(item.status, price).joinToString(" · ")
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    private companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<CollectorAlbum>() {
            override fun areItemsTheSame(oldItem: CollectorAlbum, newItem: CollectorAlbum): Boolean =
                oldItem.albumId == newItem.albumId

            override fun areContentsTheSame(oldItem: CollectorAlbum, newItem: CollectorAlbum): Boolean =
                oldItem == newItem
        }
    }
}

