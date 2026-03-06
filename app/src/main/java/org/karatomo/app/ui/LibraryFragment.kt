package org.karatomo.app.ui

import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.karatomo.app.R
import org.karatomo.app.managers.BookmarkManager
import org.karatomo.app.ui.adapter.PlaylistTabAdapter
import org.karatomo.app.ui.adapter.SongAdapter

class LibraryFragment : Fragment() {
    private lateinit var tabAdapter: PlaylistTabAdapter
    private lateinit var songAdapter: SongAdapter
    private var currentPlaylistName: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)
        
        val rvTabs = view.findViewById<RecyclerView>(R.id.rvPlaylistTabs)
        val rvSongs = view.findViewById<RecyclerView>(R.id.rvPlaylistSongs)
        
        val names = BookmarkManager.getPlaylistNames()
        if (names.isNotEmpty()) currentPlaylistName = names[0]

        // 탭 설정 및 Drag & Drop
        tabAdapter = PlaylistTabAdapter(
            onItemClick = { name -> 
                currentPlaylistName = name
                updateSongs()
            },
            onAddClick = { showCreateDialog() }
        )
        rvTabs.adapter = tabAdapter
        
        val tabHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val from = vh.adapterPosition
                val to = target.adapterPosition
                BookmarkManager.movePlaylist(requireContext(), from, to)
                tabAdapter.notifyItemMoved(from, to)
                return true
            }
            override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {}
        })
        tabHelper.attachToRecyclerView(rvTabs)

        // 곡 목록 설정
        songAdapter = SongAdapter(BookmarkManager.getSongs(currentPlaylistName))
        rvSongs.adapter = songAdapter

        view.findViewById<FloatingActionButton>(R.id.fabAddPlaylist).setOnClickListener {
            // 상세 화면으로 이동 (이름표: "playlist_name")
            val intent = android.content.Intent(context, PlaylistDetailActivity::class.java)
            intent.putExtra("playlist_name", currentPlaylistName)
            startActivity(intent)
        }

        return view
    }

    private fun updateSongs() {
        songAdapter.updateData(BookmarkManager.getSongs(currentPlaylistName))
    }

    private fun showCreateDialog() {
        val et = EditText(context)
        AlertDialog.Builder(requireContext()).setTitle("새 플레이리스트").setView(et)
            .setPositiveButton("생성") { _, _ ->
                val name = et.text.toString()
                if (name.isNotEmpty()) {
                    BookmarkManager.createPlaylist(requireContext(), name)
                    tabAdapter.notifyDataSetChanged()
                }
            }.show()
    }
    
    override fun onResume() {
        super.onResume()
        updateSongs()
        tabAdapter.notifyDataSetChanged()
    }
}
