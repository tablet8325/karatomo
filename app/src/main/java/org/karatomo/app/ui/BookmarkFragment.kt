package org.karatomo.app.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import org.karatomo.app.R
import org.karatomo.app.managers.BookmarkManager
import org.karatomo.app.ui.adapter.SongAdapter

class BookmarkFragment : Fragment() {
    private lateinit var adapter: SongAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bookmark, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.rvPlaylists)
        
        adapter = SongAdapter(emptyList())
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = adapter

        loadData()
        return view
    }

    private fun loadData() {
        val songs = BookmarkManager.getSongs("기본 플레이리스트")
        adapter.updateData(songs)
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}
