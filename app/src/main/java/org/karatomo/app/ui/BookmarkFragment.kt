package org.karatomo.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.karatomo.app.R
import org.karatomo.app.managers.BookmarkManager
import org.karatomo.app.ui.adapter.SongAdapter

class BookmarkFragment : Fragment() {
    private lateinit var adapter: SongAdapter
    private var currentPlaylist: String = "기본 플레이리스트"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // [수정] Data Binding 레이아웃(<layout>)을 사용 중이지만, 
        // 일단 빌드 에러 해결을 위해 일반적인 inflate 방식으로 처리합니다.
        val view = inflater.inflate(R.layout.fragment_bookmark, container, false)
        
        // [에러 해결] XML에 정의된 정확한 ID인 rvPlaylists를 사용합니다.
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvPlaylists)

        // 어댑터 및 리사이클러뷰 설정
        adapter = SongAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // 플레이리스트 추가 버튼 리스너 (필요 시)
        view.findViewById<View>(R.id.btnAddPlaylist).setOnClickListener {
            // 여기에 플레이리스트 추가 로직 연결
        }

        loadBookmarks()
        return view
    }

    private fun loadBookmarks() {
        val songs = BookmarkManager.getSongs(currentPlaylist)
        adapter.updateData(songs)
    }

    override fun onResume() {
        super.onResume()
        loadBookmarks()
    }
}
