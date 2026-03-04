package org.karatomo.app.manager

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.karatomo.app.model.Playlist
import org.karatomo.app.network.Song
import java.io.File

object BookmarkManager {
    val allSongs = mutableListOf<Song>()
    val playlists = mutableListOf<Playlist>()
    private const val FILE_NAME = "bookmark_backup.json"

    fun addSong(song: Song) {
        if (!allSongs.any { it.no == song.no && it.brand == song.brand }) {
            allSongs.add(song)
        }
    }

    fun createPlaylist(name: String): Boolean {
        if (playlists.any { it.name == name }) return false
        playlists.add(Playlist(name))
        return true
    }

    fun deletePlaylist(name: String) {
        playlists.removeAll { it.name == name }
    }

    fun addSongToPlaylist(song: Song, playlistName: String) {
        playlists.find { it.name == playlistName }?.let { playlist ->
            if (!playlist.songs.any { it.no == song.no && it.brand == song.brand }) {
                playlist.songs.add(song)
            }
        }
    }

    fun removeSongFromPlaylist(song: Song, playlistName: String) {
        playlists.find { it.name == playlistName }?.let { playlist ->
            playlist.songs.removeAll { it.no == song.no && it.brand == song.brand }
        }
    }

    fun exportToJson(context: Context) {
        try {
            val gson = Gson()
            val data = mapOf("version" to 1.0, "songs" to allSongs, "playlists" to playlists)
            val json = gson.toJson(data)
            File(context.filesDir, FILE_NAME).writeText(json)
            Toast.makeText(context, "북마크 JSON 내보내기 완료", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "내보내기 실패", Toast.LENGTH_SHORT).show()
        }
    }

    fun importFromJson(context: Context) {
        try {
            val file = File(context.filesDir, FILE_NAME)
            if (!file.exists()) return

            val gson = Gson()
            val json = file.readText()
            val mapType = object : TypeToken<Map<String, Any>>() {}.type
            val map: Map<String, Any> = gson.fromJson(json, mapType)

            val version = map["version"] as? Double ?: 1.0

            val songsJson = gson.toJson(map["songs"])
            allSongs.clear()
            allSongs.addAll(gson.fromJson(songsJson, object : TypeToken<List<Song>>() {}.type))

            val playlistsJson = gson.toJson(map["playlists"])
            playlists.clear()
            playlists.addAll(gson.fromJson(playlistsJson, object : TypeToken<List<Playlist>>() {}.type))

            Toast.makeText(context, "북마크 JSON 가져오기 완료 (버전 $version)", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "가져오기 실패: JSON 구조 확인 필요", Toast.LENGTH_SHORT).show()
        }
    }
}
