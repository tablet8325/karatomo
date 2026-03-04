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
    private var currentJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false) // fragment_search.xml 필요!

        progressBar = view.findViewById(R.id.progressBar)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        val btnSearch = view.findViewById<Button>(R.id.btnSearch)
        val rgBrand = view.findViewById<RadioGroup>(R.id.rgBrand)

        adapter = SongAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnSearch.setOnClickListener {
            val query = etSearch.text.toString()
            if (query.isBlank()) return@setOnClickListener
            
            val brand = when(rgBrand.checkedRadioButtonId) {
                R.id.rbKy -> "kumyoung"
                R.id.rbJoy -> "joysound"
                R.id.rbDam -> "dam"
                else -> "tj"
            }
            loadSongs(brand, query)
        }
        return view
    }

    private fun loadSongs(brand: String, query: String?) {
        currentJob?.cancel()
        progressBar.visibility = View.VISIBLE
        currentJob = lifecycleScope.launch(Dispatchers.IO) {
            try {
                val list = KaraokeApi.service.getSongs(brand, query)
                withContext(Dispatchers.Main) {
                    adapter.updateData(list)
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { progressBar.visibility = View.GONE }
            }
        }
    }
}
