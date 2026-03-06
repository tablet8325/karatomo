package org.karatomo.app.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.google.android.material.tabs.TabLayout
import org.karatomo.app.R
import org.karatomo.app.managers.BookmarkManager
import org.karatomo.app.ui.adapter.*

class LibraryFragment : Fragment() {
    private lateinit var songAdapter: MergedSongAdapter
    private var currentPlaylistName: String = ""
    private var currentBrandFilter: String = "전체"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)
        
        val tlBrand = view.findViewById<TabLayout>(R.id.tlLibraryBrand)
        val rvTabs = view.findViewById<RecyclerView>(R.id.rvPlaylistTabs)
        val rvSongs = view.findViewById<RecyclerView>(R.id.rvPlaylistSongs)

        // 1. 초기 데이터 로드
        val names = BookmarkManager.getPlaylistNames()
        if (names.isNotEmpty()) currentPlaylistName = names[0]

        // 2. 플레이리스트 탭 설정
        rvTabs.adapter = PlaylistTabAdapter(
            onItemClick = { name -> 
                currentPlaylistName = name
                updateMergedList()
            },
            onAddClick = { /* FAB와 중복되므로 필요시 다이얼로그 */ }
        )

        // 3. 브랜드 필터 탭 설정
        tlBrand.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentBrandFilter = tab?.text.toString()
                updateMergedList()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // 4. 곡 목록 어댑터 연결
        songAdapter = MergedSongAdapter(emptyList())
        rvSongs.layoutManager = LinearLayoutManager(context)
        rvSongs.adapter = songAdapter

        // 5. FAB 기능 연결
        view.findViewById<View>(R.id.fabAddPlaylist).setOnClickListener { /* 추가 다이얼로그 */ }
        view.findViewById<View>(R.id.fabRename).setOnClickListener { /* 이름변경 다이얼로그 */ }
        view.findViewById<View>(R.id.fabEditOrder).setOnClickListener { /* 순서편집 모드 */ }

        updateMergedList()
        return view
    }

    private fun updateMergedList() {
        val rawSongs = BookmarkManager.getSongs(currentPlaylistName)
        
        // 제목과 가수가 같으면 하나로 합치기
        val mergedMap = mutableMapOf<String, MergedSong>()
        rawSongs.forEach { s ->
            val key = "${s.title}_${s.singer}"
            val merged = mergedMap.getOrPut(key) { MergedSong(s.title, s.singer) }
            merged.brandNumbers[s.brand.lowercase()] = s.no
        }

        val resultList = mergedMap.values.toList().filter { ms ->
            if (currentBrandFilter == "전체") true
            else {
                val filterKey = when(currentBrandFilter) {
                    "TJ" -> "tj"
                    "금영" -> "kumyoung"
                    "Joysound" -> "joysound"
                    "DAM" -> "dam"
                    else -> ""
                }
                ms.brandNumbers.containsKey(filterKey)
            }
        }
        songAdapter.updateData(resultList, currentBrandFilter)
    }
}
