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
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SearchFragment : Fragment() {
    private lateinit var adapter: SongAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private var currentJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.recyclerView)
        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        val btnSearch = view.findViewById<Button>(R.id.btnSearch)
        val rgBrand = view.findViewById<RadioGroup>(R.id.rgBrand)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategory)

        adapter = SongAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().trim()
            if (query.isBlank()) {
                Toast.makeText(context, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val categoryPos = spinnerCategory.selectedItemPosition
            val brand = when(rgBrand.checkedRadioButtonId) {
                R.id.rbKy -> "kumyoung"
                R.id.rbJoy -> "joysound"
                R.id.rbDam -> "dam"
                else -> "tj"
            }
            loadSongs(categoryPos, brand, query)
        }
        return view
    }

    private fun loadSongs(category: Int, brand: String, query: String) {
        currentJob?.cancel()
        progressBar.visibility = View.VISIBLE
        currentJob = lifecycleScope.launch(Dispatchers.IO) {
            try {
                val list = when(category) {
                    0 -> KaraokeApi.service.searchByTitle(query, brand)
                    1 -> KaraokeApi.service.searchBySinger(query, brand)
                    2 -> KaraokeApi.service.searchByNo(query, brand)
                    3 -> KaraokeApi.service.searchByComposer(query, brand)
                    4 -> KaraokeApi.service.searchByLyricist(query, brand)
                    else -> emptyList()
                }

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    adapter.updateData(list)
                    if (list.isEmpty()) {
                        Toast.makeText(context, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        // [핵심] 리스트 최상단으로 자동 스크롤
                        recyclerView.scrollToPosition(0)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, "불러오기 실패. 인터넷을 확인하세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
