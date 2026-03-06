package org.karatomo.app.ui

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.karatomo.app.R
import org.karatomo.app.managers.BookmarkManager
import android.content.Intent

class LibraryFragment : Fragment() {
    private lateinit var adapter: PlaylistTabAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)
        
        val rv = view.findViewById<RecyclerView>(R.id.rvPlaylistTabs)
        adapter = PlaylistTabAdapter(
            // 1. 클릭 시 상세화면 이동
            onItemClick = { name ->
                val intent = Intent(requireContext(), PlaylistDetailActivity::class.java)
                intent.putExtra("playlistName", name)
                startActivity(intent)
            },
            // 2. 마지막 [+] 아이템 클릭 시 새 폴더 추가
            onAddClick = { showAddDialog() }
        )

        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = adapter
        
        refreshData()
        return view
    }

    private fun refreshData() {
        val names = BookmarkManager.getPlaylistNames().toMutableList()
        adapter.submitList(names)
    }

    private fun showAddDialog() {
        val et = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("새 플레이리스트")
            .setView(et)
            .setPositiveButton("생성") { _, _ ->
                val name = et.text.toString().trim()
                if (name.isNotEmpty()) {
                    BookmarkManager.createPlaylist(requireContext(), name)
                    refreshData()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        refreshData() // 곡 개수 등이 변했을 수 있으니 복귀 시 갱신
    }
}
