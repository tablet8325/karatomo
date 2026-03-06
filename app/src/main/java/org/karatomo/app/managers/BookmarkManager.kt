package org.karatomo.app.managers

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.karatomo.app.network.Song

object BookmarkManager {
    private const val PREF_NAME = "BookmarkPrefs"
    private const val KEY_PLAYLISTS = "Playlists"
    
    private var playlists: MutableMap<String, MutableList<Song>> = mutableMapOf()

    // 1. MainActivity에서 부르는 초기화 함수
    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_PLAYLISTS, null)
        if (json != null) {
            val type = object : TypeToken<MutableMap<String, MutableList<Song>>>() {}.type
            playlists = Gson().fromJson(json, type) ?: mutableMapOf()
        }
        if (playlists.isEmpty()) {
            playlists["기본 플레이리스트"] = mutableListOf()
            saveData(context)
        }
    }

    // 2. PlaylistDetailActivity에서 부르는 순서 변경 함수
    fun moveSong(context: Context, playlistName: String, fromPos: Int, toPos: Int) {
        val playlist = playlists[playlistName] ?: return
        if (fromPos in playlist.indices && toPos in playlist.indices) {
            val movedItem = playlist.removeAt(fromPos)
            playlist.add(toPos, movedItem)
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

    fun getPlaylistNames(): List<String> = playlists.keys.toList()
    fun getSongs(name: String): List<Song> = playlists[name] ?: emptyList()

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
