package org.karatomo.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.karatomo.app.R
import org.karatomo.app.managers.BookmarkManager
import org.karatomo.app.ui.adapter.PlaylistTabAdapter

class BookmarkFragment : Fragment() {
    private var adapter: PlaylistTabAdapter? = null // 변수 선언 방식 수정

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // R.layout.fragment_library 가 실제 파일명과 일치하는지 확인하세요
        val view = inflater.inflate(R.layout.fragment_library, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.rvPlaylistTabs)

        adapter = PlaylistTabAdapter(
            onItemClick = { name: String -> // 타입을 명시하여 putExtra 에러 방지
                val intent = Intent(requireContext(), PlaylistDetailActivity::class.java)
                intent.putExtra("playlistName", name)
                startActivity(intent)
            },
            onAddClick = { /* 추가 로직 */ }
        )

        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = adapter
        
        refresh()
        return view
    }

    private fun refresh() {
        val names = BookmarkManager.getPlaylistNames()
        adapter?.submitList(names)
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }
}
