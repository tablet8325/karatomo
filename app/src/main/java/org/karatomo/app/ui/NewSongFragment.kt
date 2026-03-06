package org.karatomo.app.ui

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import org.karatomo.app.R
import org.karatomo.app.network.KaraokeApi
import org.karatomo.app.ui.adapter.SongAdapter
import java.text.SimpleDateFormat
import java.util.*

class NewSongFragment : Fragment() {
    private lateinit var adapter: SongAdapter
    private lateinit var progressBar: ProgressBar
    private var currentBrand = "tj"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_new_song, container, false)
        
        progressBar = view.findViewById(R.id.progressBar)
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        val rg = view.findViewById<RadioGroup>(R.id.rgBrand)

        adapter = SongAdapter(mutableListOf())
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        rg.setOnCheckedChangeListener { _, checkedId ->
            currentBrand = when(checkedId) {
                R.id.rbKy -> "kumyoung"
                R.id.rbJoy -> "joysound"
                R.id.rbDam -> "dam"
                else -> "tj"
            }
            loadNewSongs()
        }

        loadNewSongs() // 초기 로딩
        return view
    }

    private fun loadNewSongs() {
        progressBar.visibility = View.VISIBLE
        
        // 현재 날짜 기준 YYYYMM 형식 생성 (예: 202603)
        val releaseDate = SimpleDateFormat("yyyyMM", Locale.getDefault()).format(Date())

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                // API 호출: release 파라미터 적용
                val list = KaraokeApi.service.getNewSongs(releaseDate, currentBrand)
                
                withContext(Dispatchers.Main) {
                    if (!isAdded) return@withContext
                    progressBar.visibility = View.GONE
                    adapter.updateData(list)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (!isAdded) return@withContext
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "신곡 로딩 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
