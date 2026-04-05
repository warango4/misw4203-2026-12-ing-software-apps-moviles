package com.misw.vinilos.data.models

data class Album(
    val id: Int,
    val name: String,
    val cover: String,
    val genre: String,
    val description: String? = null,
    val recordLabel: String? = null,
    val releaseDate: String? = null,
    val tracks: List<Track>? = emptyList(),
    val performers: List<Performer>? = emptyList(),
    val comments: List<Comment>? = emptyList()
)