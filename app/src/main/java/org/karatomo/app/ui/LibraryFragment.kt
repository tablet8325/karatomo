package org.karatomo.app.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import org.karatomo.app.R
import org.karatomo.app.managers.BookmarkManager
import org.karatomo.app.ui.adapter.*

data class MergedSong(
    val title: String,
    val singer: String,
    val brandNumbers: MutableMap<String, String> = mutableMapOf()
)

class LibraryFragment : Fragment() {
    private lateinit var songAdapter: MergedSongAdapter
    private lateinit var tabAdapter: PlaylistTabAdapter
    private var currentPlaylistName: String = ""
    private var currentBrandFilter: String = "전체"

    private fun cleanText(text: String): String {
        return text.replace(Regex("\\(.*?\\)"), "").trim()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)
        
        val tlBrand = view.findViewById<TabLayout>(R.id.tlLibraryBrand)
        val rvTabs = view.findViewById<RecyclerView>(R.id.rvPlaylistTabs)
        val rvSongs = view.findViewById<RecyclerView>(R.id.rvPlaylistSongs)

        // 1. 초기 데이터 및 탭 설정
        val names = BookmarkManager.getPlaylistNames()
        if (names.isNotEmpty()) currentPlaylistName = names[0]

        tabAdapter = PlaylistTabAdapter(onItemClick = { name -> 
            currentPlaylistName = name
            updateMergedList()
        }, onAddClick = { showCreatePlaylistDialog() })
        
        rvTabs.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvTabs.adapter = tabAdapter

        // 2. 브랜드 필터 설정
        tlBrand.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentBrandFilter = tab?.text.toString()
                updateMergedList()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // 3. 곡 목록 어댑터
        songAdapter = MergedSongAdapter(emptyList())
        rvSongs.layoutManager = LinearLayoutManager(context)
        rvSongs.adapter = songAdapter

        // 4. FAB 기능 연결 (이름변경, 순서편집, 추가)
        view.findViewById<ExtendedFloatingActionButton>(R.id.fabAddPlaylist).setOnClickListener {
            showCreatePlaylistDialog()
        }
        view.findViewById<ExtendedFloatingActionButton>(R.id.fabRename).setOnClickListener {
            showRenamePlaylistDialog()
        }
        view.findViewById<ExtendedFloatingActionButton>(R.id.fabEditOrder).setOnClickListener {
            val intent = Intent(context, PlaylistDetailActivity::class.java)
            intent.putExtra("playlist_name", currentPlaylistName)
            startActivity(intent)
        }

        updateMergedList()
        return view
    }

    // [기능 1] 플레이리스트 추가 다이얼로그
    private fun showCreatePlaylistDialog() {
        val et = EditText(context)
        et.hint = "플레이리스트 이름"
        AlertDialog.Builder(context)
            .setTitle("새 플레이리스트 추가")
            .setView(et)
            .setPositiveButton("생성") { _, _ ->
                val name = et.text.toString().trim()
                if (name.isNotEmpty()) {
                    BookmarkManager.createPlaylist(requireContext(), name)
                    refreshAll()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    // [기능 2] 플레이리스트 이름 변경 다이얼로그
    private fun showRenamePlaylistDialog() {
        val et = EditText(context)
        et.setText(currentPlaylistName)
        AlertDialog.Builder(context)
            .setTitle("플레이리스트 이름 변경")
            .setView(et)
            .setPositiveButton("변경") { _, _ ->
                val newName = et.text.toString().trim()
                if (newName.isNotEmpty() && newName != currentPlaylistName) {
                    val success = BookmarkManager.renamePlaylist(requireContext(), currentPlaylistName, newName)
                    if (success) {
                        currentPlaylistName = newName
                        refreshAll()
                    } else {
                        Toast.makeText(context, "이미 존재하는 이름입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun refreshAll() {
        tabAdapter.notifyDataSetChanged()
        updateMergedList()
    }

    private fun updateMergedList() {
        val rawSongs = BookmarkManager.getSongs(currentPlaylistName)
        val mergedMap = mutableMapOf<String, MergedSong>()

        rawSongs.forEach { s ->
            val cTitle = cleanText(s.title)
            val cSinger = cleanText(s.singer)
            val key = "${cTitle}_${cSinger}"
            
            val merged = mergedMap.getOrPut(key) { MergedSong(cTitle, cSinger) }
            merged.brandNumbers[s.brand.lowercase()] = s.no
        }

        val resultList = mergedMap.values.toList().filter { ms ->
            if (currentBrandFilter == "전체") true
            else {
                val filterKey = when(currentBrandFilter) {
                    "TJ" -> "tj"
                    "금영" -> "kumyoung"
                    "JOY" -> "joysound"
                    "DAM" -> "dam"
                    else -> ""
                }
                ms.brandNumbers.containsKey(filterKey)
            }
        }
        songAdapter.updateData(resultList, currentBrandFilter)
    }

    override fun onResume() {
        super.onResume()
        refreshAll()
    }
}
