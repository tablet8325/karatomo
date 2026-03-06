package org.karatomo.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.karatomo.app.R
import org.karatomo.app.managers.BookmarkManager
import org.karatomo.app.ui.adapter.PlaylistTabAdapter
import org.karatomo.app.ui.adapter.SongAdapter

class LibraryFragment : Fragment() {
    private lateinit var tabRecyclerView: RecyclerView
    private lateinit var songRecyclerView: RecyclerView
    private lateinit var songAdapter: SongAdapter
    private var currentPlaylistName: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)

        tabRecyclerView = view.findViewById(R.id.rvPlaylistTabs)
        songRecyclerView = view.findViewById(R.id.rvPlaylistSongs) // XML에 추가 필요
        
        // 1. 플레이리스트 탭 설정
        val names = BookmarkManager.getPlaylistNames()
        if (names.isNotEmpty()) currentPlaylistName = names[0]

        val tabAdapter = PlaylistTabAdapter(
            onItemClick = { name -> 
                currentPlaylistName = name
                updateSongList() // 탭 클릭 시 곡 목록 갱신
            },
            onAddClick = { /* 다이얼로그 로직 */ }
        )
        tabRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        tabRecyclerView.adapter = tabAdapter

        // 2. 곡 목록 설정 (첫 번째 리스트 바로 노출)
        songAdapter = SongAdapter(BookmarkManager.getSongs(currentPlaylistName))
        songRecyclerView.layoutManager = LinearLayoutManager(context)
        songRecyclerView.adapter = songAdapter

        view.findViewById<FloatingActionButton>(R.id.fabAddPlaylist).setOnClickListener {
            // 상세 화면(편집모드)으로 이동
            val intent = Intent(context, PlaylistDetailActivity::class.java)
            intent.putExtra("playlist_name", currentPlaylistName)
            startActivity(intent)
        }

        return view
    }

    private fun updateSongList() {
        val songs = BookmarkManager.getSongs(currentPlaylistName)
        songAdapter.updateData(songs)
    }
}
