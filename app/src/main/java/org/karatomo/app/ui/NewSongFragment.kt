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
        tvMessage.text = ""

        currentJob = lifecycleScope.launch(Dispatchers.IO) {
            try {
                val list: List<Song> = KaraokeApi.service.getSongs(brand)
                withContext(Dispatchers.Main) {
                    adapter.updateData(list)
                    progressBar.visibility = View.GONE
                    tvMessage.text = if (list.isEmpty()) "곡이 없습니다." else ""
                    Toast.makeText(requireContext(), "$brand 곡 ${list.size}개 로드 완료", Toast.LENGTH_SHORT).show()
                }
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    tvMessage.text = "곡 로드 실패"
                    Toast.makeText(requireContext(), "$brand 곡 로드 실패", Toast.LENGTH_SHORT).show()
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
