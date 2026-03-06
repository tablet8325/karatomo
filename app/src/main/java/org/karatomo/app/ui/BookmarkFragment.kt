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
        val view = inflater.inflate(R.layout.fragment_bookmark, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvBookmarks)

        // 어댑터 초기화
        adapter = SongAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        loadBookmarks()
        return view
    }

    private fun loadBookmarks() {
        // [에러 해결] BookmarkManager에서 곡을 가져와 어댑터에 전달
        val songs = BookmarkManager.getSongs(currentPlaylist)
        
        // 만약 SongAdapter에 submitList를 추가했다면 아래 코드가 작동합니다.
        // 혹시 모르니 명시적으로 updateData도 호출 가능하게 설계했습니다.
        adapter.updateData(songs) 
    }

    override fun onResume() {
        super.onResume()
        loadBookmarks()
    }
}
