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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_new_song, container, false)

        // findViewById로 안전하게 연결
        progressBar = view.findViewById(R.id.progressBar)
        tvMessage = view.findViewById(R.id.tvMessage)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        adapter = SongAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        view.findViewById<Button>(R.id.btnTj).setOnClickListener { loadSongs("tj") }
        view.findViewById<Button>(R.id.btnKy).setOnClickListener { loadSongs("ky") }
        view.findViewById<Button>(R.id.btnJoy).setOnClickListener { loadSongs("joysound") }
        view.findViewById<Button>(R.id.btnDam).setOnClickListener { loadSongs("dam") }

        // 시작 시 자동으로 불러오고 싶다면 주석 해제
        loadSongs("tj")

        return view
    }

    private fun loadSongs(brand: String) {
        currentJob?.cancel()
        progressBar.visibility = View.VISIBLE
        tvMessage.text = "데이터 불러오는 중..."

        currentJob = lifecycleScope.launch(Dispatchers.IO) {
            try {
                val list = KaraokeApi.service.getSongs(brand)
                withContext(Dispatchers.Main) {
                    adapter.updateData(list)
                    progressBar.visibility = View.GONE
                    tvMessage.text = if (list.isEmpty()) "목록이 비어있습니다." else ""
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    tvMessage.text = "에러: ${e.localizedMessage}"
                    Toast.makeText(requireContext(), "API 에러: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentJob?.cancel()
    }
}
