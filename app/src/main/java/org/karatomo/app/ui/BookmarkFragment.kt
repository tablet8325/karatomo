package org.karatomo.app.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import org.karatomo.app.R
import org.karatomo.app.managers.BookmarkManager
import org.karatomo.app.ui.adapter.SongAdapter

class BookmarkFragment : Fragment() {
    private var adapter: SongAdapter? = null // null 안전성 확보

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // [오류방지] 레이아웃 inflate 실패 방지
        val view = inflater.inflate(R.layout.fragment_bookmark, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.rvPlaylists) ?: return view
        
        adapter = SongAdapter(emptyList())
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = adapter

        loadData()
        return view
    }

    private fun loadData() {
        // [오류방지] BookmarkManager가 비어있을 경우를 대비해 첫 번째 이름을 동적으로 가져옴
        val names = BookmarkManager.getPlaylistNames()
        val targetName = if (names.isNotEmpty()) names[0] else "기본 플레이리스트"
        
        val songs = BookmarkManager.getSongs(targetName)
        adapter?.updateData(songs)
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}
