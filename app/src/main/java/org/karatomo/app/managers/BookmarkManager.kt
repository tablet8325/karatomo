package org.karatomo.app.managers

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.karatomo.app.network.Song

object BookmarkManager {
    private const val PREF_NAME = "BookmarkPrefs"
    private const val KEY_PLAYLISTS = "Playlists"
    
    private var playlists: MutableMap<String, MutableList<Song>> = LinkedHashMap()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_PLAYLISTS, null)
        if (json != null) {
            val type = object : TypeToken<LinkedHashMap<String, MutableList<Song>>>() {}.type
            playlists = Gson().fromJson(json, type) ?: LinkedHashMap()
        }
        if (playlists.isEmpty()) {
            playlists["기본 플레이리스트"] = mutableListOf()
            saveData(context)
        }
    }

    fun renamePlaylist(context: Context, oldName: String, newName: String): Boolean {
        if (playlists.containsKey(newName) || oldName == newName) return false
        val songs = playlists.remove(oldName) ?: return false
        playlists[newName] = songs
        saveData(context)
        return true
    }

    fun deletePlaylist(context: Context, name: String) {
        playlists.remove(name)
        if (playlists.isEmpty()) playlists["기본 플레이리스트"] = mutableListOf()
        saveData(context)
    }

    fun movePlaylist(context: Context, fromPos: Int, toPos: Int) {
        val keys = playlists.keys.toMutableList()
        if (fromPos in keys.indices && toPos in keys.indices) {
            val movedKey = keys.removeAt(fromPos)
            keys.add(toPos, movedKey)
            
            val newMap = LinkedHashMap<String, MutableList<Song>>()
            keys.forEach { key -> newMap[key] = playlists[key]!! }
            playlists = newMap
            saveData(context)
        }
    }

    fun addSong(context: Context, playlistName: String, song: Song): Boolean {
        val playlist = playlists[playlistName] ?: return false
        if (playlist.any { it.no == song.no && it.brand == song.brand }) return false 
        playlist.add(song)
        saveData(context)
        return true
    }

    fun createPlaylist(context: Context, name: String) {
        if (!playlists.containsKey(name)) {
            playlists[name] = mutableListOf()
            saveData(context)
        }
    }

    fun getSongs(name: String): List<Song> = playlists[name] ?: emptyList()
    fun getPlaylistNames(): List<String> = playlists.keys.toList()

    fun moveSong(context: Context, playlistName: String, fromPos: Int, toPos: Int) {
        val playlist = playlists[playlistName] ?: return
        if (fromPos in playlist.indices && toPos in playlist.indices) {
            val movedItem = playlist.removeAt(fromPos)
            playlist.add(toPos, movedItem)
            saveData(context)
        }
    }

    // 중복 제거 및 단일화
    fun removeSong(context: Context, playlistName: String, song: Song) {
        playlists[playlistName]?.remove(song)
        saveData(context)
    }

    private fun saveData(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(playlists)
        prefs.edit().putString(KEY_PLAYLISTS, json).apply()
    }
}
