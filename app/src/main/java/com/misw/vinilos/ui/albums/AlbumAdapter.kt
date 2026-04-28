package com.misw.vinilos.ui.albums

import com.misw.vinilos.data.models.Album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.misw.vinilos.databinding.ItemAlbumBinding

class AlbumAdapter(private val albums: List<Album>, private val onClick: (Album) -> Unit) :
    RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    inner class AlbumViewHolder(val binding: ItemAlbumBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albums[position]
        holder.binding.apply {
            tvAlbumName.text = album.name
            tvAlbumGenre.text = album.genre
            Glide.with(root.context).load(album.cover).into(ivAlbumCover)
            root.setOnClickListener {
                onClick(album)
            }
        }
    }

    override fun getItemCount(): Int {
        return albums.size
    }
}