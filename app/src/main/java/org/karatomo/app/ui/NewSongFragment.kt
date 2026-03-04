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
    return try {
        _binding = FragmentNewSongBinding.inflate(inflater, container, false)
        // 일단 어댑터고 뭐고 다 주석 처리! 오직 화면만 띄워봅니다.
        /*
        adapter = SongAdapter(mutableListOf())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        */
        binding.root
    } catch (e: Exception) {
        // 만약 여기서 터진다면 XML 레이아웃 자체의 문제입니다.
        val tv = TextView(requireContext())
        tv.text = "XML 바인딩 실패: ${e.message}"
        tv
    }
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
