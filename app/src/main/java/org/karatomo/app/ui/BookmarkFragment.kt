package org.karatomo.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.karatomo.app.R
import org.karatomo.app.managers.BookmarkManager
// PlaylistTabAdapter의 패키지 경로를 확인하세요. (보통 .ui.adapter)
import org.karatomo.app.ui.adapter.PlaylistTabAdapter

class BookmarkFragment : Fragment() {
    private lateinit var adapter: PlaylistTabAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.rvPlaylistTabs)

        adapter = PlaylistTabAdapter(
            onItemClick = { name ->
                val intent = Intent(requireContext(), PlaylistDetailActivity::class.java)
                intent.putExtra("playlistName", name)
                startActivity(intent)
            },
            onAddClick = { /* 추가 다이얼로그 로직 */ }
        )

        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = adapter
        
        refresh()
        return view
    }

    private fun refresh() {
        // [수정] BookmarkManager의 public 메서드를 통해 리스트를 가져옵니다.
        val names = BookmarkManager.getPlaylistNames()
        adapter.submitList(names)
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }
}
