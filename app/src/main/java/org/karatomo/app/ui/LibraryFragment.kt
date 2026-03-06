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
    private lateinit var tabAdapter: PlaylistTabAdapter

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
            onAddClick = { showCreateDialog() }
        )

        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = tabAdapter

        // 오른쪽 하단 동그란 버튼 누르면 입력창 뜸
        fab.setOnClickListener { showCreateDialog() }
        
        loadList()
        return view
    }

    private fun showCreateDialog() {
        val input = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("새 플레이리스트")
            .setMessage("이름을 정해주세요.")
            .setView(input)
            .setPositiveButton("만들기") { _, _ ->
                val name = input.text.toString()
                if (name.isNotEmpty()) {
                    BookmarkManager.createPlaylist(requireContext(), name)
                    loadList() // 목록 새로고침
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun loadList() {
        val playlists = BookmarkManager.getPlaylistNames()
        tabAdapter.submitList(playlists)
    }

    override fun onResume() {
        super.onResume()
        loadList()
    }
}
