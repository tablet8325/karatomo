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

    fun createPlaylist(context: Context, name: String) {
        if (!playlists.containsKey(name)) {
            playlists[name] = mutableListOf()
            saveData(context)
        }
    }

    fun renamePlaylist(context: Context, oldName: String, newName: String): Boolean {
        if (playlists.containsKey(newName)) return false
        val songs = playlists.remove(oldName) ?: return false
        playlists[newName] = songs
        saveData(context)
        return true
    }

    // [에러 해결] 누락된 함수 추가
    fun deletePlaylist(context: Context, name: String) {
        playlists.remove(name)
        if (playlists.isEmpty()) playlists["기본 플레이리스트"] = mutableListOf()
        saveData(context)
    }

    fun getSongs(name: String): List<Song> = playlists[name] ?: emptyList()
    fun getPlaylistNames(): List<String> = playlists.keys.toList()

    private fun saveData(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(playlists)
        prefs.edit().putString(KEY_PLAYLISTS, json).apply()
    }
}
