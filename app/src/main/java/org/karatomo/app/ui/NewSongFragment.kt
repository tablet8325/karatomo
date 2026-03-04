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

class NewSongFragment : Fragment() {

    private lateinit var adapter: SongAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var tvMessage: TextView
    private var currentJob: Job? = null
    private var selectedBrand: String = "tj"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_new_song, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        tvMessage = view.findViewById(R.id.tvMessage)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        val btnSearch = view.findViewById<Button>(R.id.btnSearch)

        adapter = SongAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // 검색 버튼 클릭 시
        btnSearch.setOnClickListener {
            loadSongs(selectedBrand, etSearch.text.toString())
        }

        // 브랜드 버튼들
        view.findViewById<Button>(R.id.btnTj).setOnClickListener { selectedBrand = "tj"; loadSongs(selectedBrand) }
        view.findViewById<Button>(R.id.btnKy).setOnClickListener { selectedBrand = "kumyoung"; loadSongs(selectedBrand) }
        view.findViewById<Button>(R.id.btnJoy).setOnClickListener { selectedBrand = "joysound"; loadSongs(selectedBrand) }
        view.findViewById<Button>(R.id.btnDam).setOnClickListener { selectedBrand = "dam"; loadSongs(selectedBrand) }

        loadSongs(selectedBrand)
        return view
    }

    private fun loadSongs(brand: String, query: String? = null) {
        currentJob?.cancel()
        progressBar.visibility = View.VISIBLE
        tvMessage.text = "데이터 로딩 중..."

        currentJob = lifecycleScope.launch(Dispatchers.IO) {
            try {
                val list = KaraokeApi.service.getSongs(brand, query)
                withContext(Dispatchers.Main) {
                    adapter.updateData(list)
                    progressBar.visibility = View.GONE
                    tvMessage.text = if (list.isEmpty()) "검색 결과가 없습니다." else ""
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    tvMessage.text = "에러: ${e.localizedMessage}"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentJob?.cancel()
    }
}
