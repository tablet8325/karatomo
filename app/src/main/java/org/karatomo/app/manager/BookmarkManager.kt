package org.karatomo.app

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.karatomo.app.network.Playlist
import org.karatomo.app.network.Song
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * [BookmarkManager]
 * 보관함(탭)과 곡 목록의 데이터를 관리하고 내부 저장소에 자동으로 저장하는 싱글톤 객체입니다.
 */
object BookmarkManager {
    private const val FILE_NAME = "karatomo_data.json"
    private val gson = Gson()
    
    // 외부에서 접근 가능한 탭 리스트
    var playlists: MutableList<Playlist> = mutableListOf()
        private set

    /**
     * 앱 실행 시 최초 1회 호출하여 데이터를 로드합니다.
     */
    fun initialize(context: Context) {
        loadData(context)
        // 탭이 하나도 없다면 삭제 불가능한 '기본 탭'을 강제 생성합니다.
        if (playlists.isEmpty()) {
            playlists.add(Playlist(name = "기본 탭", isDefault = true))
            saveData(context)
        }
    }

    /**
     * 새로운 탭을 추가합니다.
     */
    fun addPlaylist(context: Context, name: String): Boolean {
        if (playlists.any { it.name == name }) return false 
        playlists.add(Playlist(name = name))
        saveData(context)
        return true
    }

    /**
     * 탭의 이름을 수정합니다. (기본 탭은 수정 불가)
     */
    fun renamePlaylist(context: Context, index: Int, newName: String) {
        if (index in playlists.indices && !playlists[index].isDefault) {
            playlists[index].name = newName
            saveData(context)
        }
    }

    /**
     * 탭을 삭제합니다. (기본 탭은 삭제 불가)
     */
    fun deletePlaylist(context: Context, index: Int) {
        if (index in playlists.indices && !playlists[index].isDefault) {
            playlists.removeAt(index)
            saveData(context)
        }
    }

    /**
     * 탭의 순서를 변경합니다.
     */
    fun movePlaylist(context: Context, fromIndex: Int, toIndex: Int) {
        if (fromIndex in playlists.indices && toIndex in playlists.indices) {
            val item = playlists.removeAt(fromIndex)
            playlists.add(toIndex, item)
            saveData(context)
        }
    }

    /**
     * 특정 탭에 곡을 추가합니다.
     */
    fun addSongToPlaylist(context: Context, playlistIndex: Int, song: Song): Boolean {
        if (playlistIndex in playlists.indices) {
            val isDuplicate = playlists[playlistIndex].songs.any { 
                it.title == song.title && it.singer == song.singer 
            }
            if (!isDuplicate) {
                val songWithDate = song.copy(addedDate = getCurrentDate())
                playlists[playlistIndex].songs.add(songWithDate)
                saveData(context)
                return true
            }
        }
        return false
    }

    /**
     * 데이터 변경 사항이 있을 때 명시적으로 호출하여 저장합니다.
     */
    fun updateSongList(context: Context) {
        saveData(context)
    }

    private fun saveData(context: Context) {
        try {
            val json = gson.toJson(playlists)
            val file = File(context.filesDir, FILE_NAME)
            file.writeText(json)
        } catch (e: Exception) {
            // 로그 확인 불가 시 예외 대응
        }
    }

    private fun loadData(context: Context) {
        try {
            val file = File(context.filesDir, FILE_NAME)
            if (file.exists()) {
                val json = file.readText()
                val type = object : TypeToken<MutableList<Playlist>>() {}.type
                val loaded: MutableList<Playlist>? = gson.fromJson(json, type)
                if (loaded != null) {
                    playlists = loaded
                }
            }
        } catch (e: Exception) {
            playlists = mutableListOf()
        }
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
    }
}
