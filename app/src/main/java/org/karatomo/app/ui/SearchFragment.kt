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

class SearchFragment : Fragment() {
    private lateinit var adapter: SongAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        val btnSearch = view.findViewById<Button>(R.id.btnSearch)
        val rgBrand = view.findViewById<RadioGroup>(R.id.rgBrand)
        val spinner = view.findViewById<Spinner>(R.id.spinnerCategory)

        adapter = SongAdapter(mutableListOf())
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().trim()
            if (query.isEmpty()) return@setOnClickListener

            val brand = when(rgBrand.checkedRadioButtonId) {
                R.id.rbKy -> "kumyoung"
                R.id.rbJoy -> "joysound"
                R.id.rbDam -> "dam"
                else -> "tj"
            }
            
            val categoryPos = spinner.selectedItemPosition
            loadSongs(categoryPos, brand, query)
        }
        return view
    }

    private fun loadSongs(category: Int, brand: String, query: String) {
        progressBar.visibility = View.VISIBLE
        
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 선택한 카테고리에 맞는 전용 통로를 호출합니다.
                val list = when(category) {
                    0 -> KaraokeApi.service.searchByTitle(query, brand)
                    1 -> KaraokeApi.service.searchBySinger(query, brand)
                    2 -> KaraokeApi.service.searchByNo(query, brand)
                    3 -> KaraokeApi.service.searchByComposer(query, brand)
                    4 -> KaraokeApi.service.searchByLyricist(query, brand)
                    else -> emptyList()
                }

                withContext(Dispatchers.Main) {
                    if (!isAdded) return@withContext
                    progressBar.visibility = View.GONE
                    adapter.updateData(list)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (!isAdded) return@withContext
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "검색 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
