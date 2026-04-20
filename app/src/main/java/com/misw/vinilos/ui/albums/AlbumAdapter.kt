package com.misw.vinilos.ui.albums

import android.util.Log
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
        Log.d("AlbumAdapter", "onCreateViewHolder called")
        val binding = ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albums[position]
        Log.d("AlbumAdapter", "onBindViewHolder called for position: $position, album: ${album.name}")
        holder.binding.apply {
            tvAlbumName.text = album.name
            tvAlbumGenre.text = album.genre
            Glide.with(root.context).load(album.cover).into(ivAlbumCover)
            root.setOnClickListener {
                Log.d("AlbumAdapter", "Album clicked: ${album.name}")
                onClick(album) 
            }
        }
    }

    override fun getItemCount(): Int {
        Log.d("AlbumAdapter", "getItemCount called: ${albums.size}")
        return albums.size
    }
}