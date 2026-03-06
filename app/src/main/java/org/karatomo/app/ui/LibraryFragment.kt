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
import org.karatomo.app.managers.BookmarkManager // s 확인
import org.karatomo.app.ui.adapter.PlaylistTabAdapter // 경로 확인

class LibraryFragment : Fragment() {
    private var tabAdapter: PlaylistTabAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.rvPlaylistTabs)

        tabAdapter = PlaylistTabAdapter(
            onItemClick = { playlistName: String -> // [해결] 타입을 String으로 명시하여 putExtra 에러 방지
                val intent = Intent(requireContext(), PlaylistDetailActivity::class.java)
                intent.putExtra("playlistName", playlistName)
                startActivity(intent)
            },
            onAddClick = { /* 추가 로직 */ }
        )

        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = tabAdapter
        
        refreshData()
        return view
    }

    private fun refreshData() {
        val names = BookmarkManager.getPlaylistNames()
        tabAdapter?.submitList(names)
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }
}
