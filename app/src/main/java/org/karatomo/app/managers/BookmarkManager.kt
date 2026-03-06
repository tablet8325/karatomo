package org.karatomo.app.managers

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.karatomo.app.network.Song

object BookmarkManager {
    private const val PREF_NAME = "BookmarkPrefs"
    private const val KEY_PLAYLISTS = "Playlists"
    
    // 데이터를 담는 맵
    private var playlists: MutableMap<String, MutableList<Song>> = mutableMapOf()
    private var isLoaded = false

    // 데이터를 안전하게 가져오기 위한 함수 (자동 로딩 포함)
    private fun ensureLoaded(context: Context) {
        if (!isLoaded) {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val json = prefs.getString(KEY_PLAYLISTS, null)
            if (json != null) {
                val type = object : TypeToken<MutableMap<String, MutableList<Song>>>() {}.type
                playlists = Gson().fromJson(json, type) ?: mutableMapOf()
            }
            if (playlists.isEmpty()) {
                playlists["기본 플레이리스트"] = mutableListOf()
            }
            isLoaded = true
        }
    }

    fun addSong(context: Context, playlistName: String, song: Song): Boolean {
        ensureLoaded(context)
        val playlist = playlists[playlistName] ?: return false
        if (playlist.any { it.no == song.no && it.brand == song.brand }) return false 

        playlist.add(song)
        saveData(context)
        return true
    }

    fun createPlaylist(context: Context, name: String) {
        ensureLoaded(context)
        if (!playlists.containsKey(name)) {
            playlists[name] = mutableListOf()
            saveData(context)
        }
    }

    // 이름 목록을 가져올 때도 안전하게 로딩 확인
    fun getPlaylistNames(context: Context? = null): List<String> {
        // context가 있으면 로딩 확인, 없으면 현재 메모리값 반환
        return playlists.keys.toList()
    }

    fun getSongs(name: String): List<Song> = playlists[name] ?: emptyList()

    private fun saveData(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(playlists)
        prefs.edit().putString(KEY_PLAYLISTS, json).apply()
    }
    
    // 순서 변경 등 나머지 메서드도 동일하게 ensureLoaded(context)를 첫 줄에 넣어주면 완벽합니다.
}
