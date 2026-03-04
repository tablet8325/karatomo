package org.karatomo.app.network

data class Song(
    val brand: String,
    val no: String,
    val title: String,
    val singer: String,
    val composer: String? = null,
    val lyricist: String? = null,
    val release: String? = null
)
