package com.misw.vinilos.data.models
data class Performer(
    val id: Int,
    val name: String,
    val image: String,
    val description: String,
    val birthDate: String? = null,
    val creationDate: String? = null,
    val albums: List<Album>? = emptyList(),
    val musicians: List<Performer>? = emptyList(),
    val performerPrizes: List<PerformerPrize>? = emptyList()
)
