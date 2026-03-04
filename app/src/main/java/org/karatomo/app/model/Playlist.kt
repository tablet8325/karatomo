package org.karatomo.app.model

import org.karatomo.app.network.Song

data class Playlist(
    val name: String,
    val songs: MutableList<Song> = mutableListOf()
)
