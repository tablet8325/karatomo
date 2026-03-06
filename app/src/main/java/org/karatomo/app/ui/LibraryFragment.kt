package org.karatomo.app.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.karatomo.app.R
import org.karatomo.app.managers.BookmarkManager
import org.karatomo.app.ui.adapter.PlaylistTabAdapter

class LibraryFragment : Fragment() {
    private var tabAdapter: PlaylistTabAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)
        
        val rv = view.findViewById<RecyclerView>(R.id.rvPlaylistTabs)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabAddPlaylist)

        tabAdapter = PlaylistTabAdapter(
            onItemClick = { name ->
                val intent = Intent(requireContext(), PlaylistDetailActivity::class.java)
                intent.putExtra("playlistName", name)
                startActivity(intent)
            },
            onAddClick = { showAddDialog() }
        )

        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = tabAdapter

        fab.setOnClickListener { showAddDialog() }
        
        refresh()
        return view
    }

    private fun showAddDialog() {
        val et = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("새 플레이리스트 생성")
            .setMessage("이름을 입력하세요")
            .setView(et)
            .setPositiveButton("생성") { _, _ ->
                val name = et.text.toString().trim()
                if (name.isNotEmpty()) {
                    BookmarkManager.createPlaylist(requireContext(), name)
                    refresh()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun refresh() {
        val names = BookmarkManager.getPlaylistNames()
        tabAdapter?.submitList(names)
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }
}
