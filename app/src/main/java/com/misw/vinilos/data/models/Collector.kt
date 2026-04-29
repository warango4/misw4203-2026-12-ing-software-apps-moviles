package com.misw.vinilos.data.models

import com.google.gson.annotations.SerializedName

data class Collector(
    val id: Int,
    val name: String,
    val telephone: String,
    val email: String,
    val favoritePerformers: List<Performer>? = emptyList(),
    val comments: List<CollectorComment>? = emptyList(),
    val collectorAlbums: List<CollectorAlbum>? = emptyList()
)

/**
 * Comentario hecho por el coleccionista sobre un álbum.
 * El backend retorna un objeto `album` embebido (mínimo id + name).
 */
data class CollectorComment(
    val id: Int,
    val description: String,
    val rating: Int
)

/**
 * Álbum asociado al coleccionista (con metadatos propios como price/status y un álbum embebido).
 */
data class CollectorAlbum(
    @SerializedName("id")
    val albumId: Int,
    val price: Double? = null,
    val status: String? = null
)

