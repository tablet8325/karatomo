package org.karatomo.app.ui

import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import org.karatomo.app.databinding.FragmentNewSongBinding
import org.karatomo.app.network.KaraokeApi
import org.karatomo.app.network.Song
import org.karatomo.app.ui.adapter.SongAdapter

class NewSongFragment : Fragment() {

    private var _binding: FragmentNewSongBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SongAdapter

    private lateinit var progressBar: ProgressBar
    private lateinit var tvMessage: TextView

    private var currentJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewSongBinding.inflate(inflater, container, false)

        adapter = SongAdapter(mutableListOf())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        progressBar = binding.progressBar
        tvMessage = binding.tvMessage

        binding.btnTj.setOnClickListener { loadSongs("tj") }
        binding.btnKy.setOnClickListener { loadSongs("ky") }
        binding.btnJoy.setOnClickListener { loadSongs("joysound") }
        binding.btnDam.setOnClickListener { loadSongs("dam") }

        return binding.root
    }

private fun loadSongs(brand: String) {
    currentJob?.cancel()
    progressBar.visibility = View.VISIBLE
    tvMessage.text = "데이터 불러오는 중..."

    currentJob = lifecycleScope.launch(Dispatchers.IO) {
        try {
            // 1. API 호출 시도
            val list = KaraokeApi.service.getSongs(brand)
            
            withContext(Dispatchers.Main) {
                adapter.updateData(list)
                progressBar.visibility = View.GONE
                tvMessage.text = if (list.isEmpty()) "목록이 비어있습니다." else ""
            }
        } catch (e: Exception) {
            // 2. 에러가 나도 앱이 죽지 않게 잡아서 화면에 띄움
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                tvMessage.text = "에러 발생: ${e.localizedMessage}"
                // 사지방 환경에서 가장 중요한 에러 메시지 확인용 토스트
                Toast.makeText(requireContext(), "API 에러: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

    override fun onDestroyView() {
        super.onDestroyView()
        currentJob?.cancel()
        _binding = null
    }
}
