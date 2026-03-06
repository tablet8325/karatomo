package org.karatomo.app.managers

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.karatomo.app.network.Song

object BookmarkManager {
    private const val PREF_NAME = "BookmarkPrefs"
    private const val KEY_PLAYLISTS = "Playlists"
    
    // 플레이리스트 데이터를 담는 맵 (이름 : 곡 목록)
    private var playlists = mutableMapOf<String, MutableList<Song>>()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_PLAYLISTS, null)
        
        // 1. 저장된 데이터 불러오기
        if (json != null) {
            val type = object : TypeToken<MutableMap<String, MutableList<Song>>>() {}.type
            playlists = Gson().fromJson(json, type) ?: mutableMapOf()
        }

        // 2. [오류 방지] 데이터가 없으면 "기본 플레이리스트" 생성
        if (playlists.isEmpty()) {
            playlists["기본 플레이리스트"] = mutableListOf()
            saveData(context)
        }
    }

    // 곡 추가 (브랜드+번호 중복 방지 로직 포함)
    fun addSong(context: Context, playlistName: String, song: Song): Boolean {
        val playlist = playlists[playlistName] ?: return false
        
        // 같은 곡이 이미 있는지 확인
        val isDuplicate = playlist.any { it.no == song.no && it.brand == song.brand }
        if (isDuplicate) return false 

        playlist.add(song)
        saveData(context)
        return true
    }

    // 플레이리스트 자체 추가
    fun createPlaylist(context: Context, name: String) {
        if (!playlists.containsKey(name)) {
            playlists[name] = mutableListOf()
            saveData(context)
        }
    }

    // 상세 화면용: 곡 목록 및 이름 가져오기
    fun getSongs(name: String): List<Song> = playlists[name] ?: emptyList()
    fun getPlaylistNames(): List<String> = playlists.keys.toList()

    // 곡 삭제
    fun removeSong(context: Context, playlistName: String, song: Song) {
        playlists[playlistName]?.remove(song)
        saveData(context)
    }

    // 곡 순서 변경 (Drag & Drop 반영용)
    fun moveSong(context: Context, playlistName: String, fromPos: Int, toPos: Int) {
        val playlist = playlists[playlistName] ?: return
        if (fromPos < playlist.size && toPos < playlist.size) {
            val movedItem = playlist.removeAt(fromPos)
            playlist.add(toPos, movedItem)
            saveData(context)
        }
    }

    private fun saveData(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(playlists)
        prefs.edit().putString(KEY_PLAYLISTS, json).apply()
    }
}
