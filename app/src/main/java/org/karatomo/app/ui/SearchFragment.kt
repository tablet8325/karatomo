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
import org.karatomo.app.network.Song
import org.karatomo.app.ui.adapter.SongAdapter
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SearchFragment : Fragment() {
    private lateinit var adapter: SongAdapter
    private lateinit var progressBar: ProgressBar
    private var currentJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
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

            loadSongsByCategory(categoryPos, brand, query)
        }
        return view
    }

    private fun loadSongsByCategory(category: Int, brand: String, query: String) {
        currentJob?.cancel()
        progressBar.visibility = View.VISIBLE
        
        currentJob = lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 카테고리별 API 분기 호출
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
                    if (list.isEmpty()) {
                        adapter.updateData(mutableListOf())
                        Toast.makeText(context, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        adapter.updateData(list)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    val msg = when(e) {
                        is UnknownHostException -> "인터넷 연결을 확인해주세요."
                        is SocketTimeoutException -> "서버 응답 시간이 초과되었습니다."
                        else -> "결과를 불러올 수 없습니다."
                    }
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    adapter.updateData(mutableListOf())
                }
            }
        }
    }
}
