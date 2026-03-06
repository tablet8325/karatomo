package org.karatomo.app.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.google.android.material.tabs.TabLayout
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

        val names = BookmarkManager.getPlaylistNames()
        if (names.isNotEmpty()) currentPlaylistName = names[0]

        rvTabs.adapter = PlaylistTabAdapter(onItemClick = { name -> 
            currentPlaylistName = name
            updateMergedList()
        }, onAddClick = {})

        tlBrand.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentBrandFilter = tab?.text.toString()
                updateMergedList()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        songAdapter = MergedSongAdapter(emptyList())
        rvSongs.layoutManager = LinearLayoutManager(context)
        rvSongs.adapter = songAdapter

        updateMergedList()
        return view
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
}
