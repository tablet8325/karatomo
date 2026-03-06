package org.karatomo.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import org.karatomo.app.R
import org.karatomo.app.managers.BookmarkManager
import org.karatomo.app.ui.adapter.PlaylistTabAdapter
import org.karatomo.app.ui.adapter.SongAdapter

class LibraryFragment : Fragment() {
    private lateinit var tabAdapter: PlaylistTabAdapter
    private lateinit var songAdapter: SongAdapter
    private lateinit var tvEmptyNotice: TextView
    private lateinit var rvPlaylistSongs: RecyclerView
    private var currentPlaylistName: String = ""
    private var currentBrandFilter: String = "all"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)
        
        tvEmptyNotice = view.findViewById(R.id.tvEmptyNotice)
        rvPlaylistSongs = view.findViewById(R.id.rvPlaylistSongs)
        val tlBrand = view.findViewById<TabLayout>(R.id.tlLibraryBrand)
        val rvTabs = view.findViewById<RecyclerView>(R.id.rvPlaylistTabs)
        
        val names = BookmarkManager.getPlaylistNames()
        if (names.isNotEmpty()) currentPlaylistName = names[0]

        // 1. 브랜드 탭 설정
        tlBrand.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentBrandFilter = when(tab?.position) {
                    1 -> "tj"
                    2 -> "kumyoung"
                    3 -> "joysound"
                    4 -> "dam"
                    else -> "all"
                }
                updateSongs()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // 2. 플레이리스트 탭 설정
        tabAdapter = PlaylistTabAdapter(
            onItemClick = { name -> 
                currentPlaylistName = name
                updateSongs()
            },
            onAddClick = { /* 다이얼로그 호출 */ }
        )
        rvTabs.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvTabs.adapter = tabAdapter

        // 3. 곡 목록 설정
        songAdapter = SongAdapter(emptyList())
        rvPlaylistSongs.layoutManager = LinearLayoutManager(context)
        rvPlaylistSongs.adapter = songAdapter

        // 4. 플로팅 버튼 (편집 화면 이동)
        view.findViewById<FloatingActionButton>(R.id.fabMain).setOnClickListener {
            val intent = Intent(context, PlaylistDetailActivity::class.java)
            intent.putExtra("playlist_name", currentPlaylistName)
            startActivity(intent)
        }

        updateSongs()
        return view
    }

    private fun updateSongs() {
        val allSongs = BookmarkManager.getSongs(currentPlaylistName)
        val filtered = if (currentBrandFilter == "all") allSongs 
                       else allSongs.filter { it.brand?.lowercase() == currentBrandFilter }
        
        if (filtered.isEmpty()) {
            tvEmptyNotice.visibility = View.VISIBLE
            rvPlaylistSongs.visibility = View.GONE
        } else {
            tvEmptyNotice.visibility = View.GONE
            rvPlaylistSongs.visibility = View.VISIBLE
            songAdapter.updateData(filtered)
        }
    }

    override fun onResume() {
        super.onResume()
        updateSongs()
        tabAdapter.notifyDataSetChanged()
    }
}
