package org.karatomo.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import org.karatomo.app.databinding.FragmentNewSongBinding
import org.karatomo.app.network.KaraokeApi
import org.karatomo.app.network.SongAdapter
import java.util.*

class NewSongFragment : Fragment() {
    private var _binding: FragmentNewSongBinding? = null
    private val binding get() = _binding!!
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    
    private var selectedDate = ""
    private var selectedBrand = "tj"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewSongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupDateSpinner()
        setupBrandButtons()
        
        // 초기 데이터 로드 (현재 날짜 기준)
        fetchNewSongs()
    }

    private fun setupDateSpinner() {
        val dates = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        // 2026년 3월부터 역순으로 12개월 리스트 생성
        calendar.set(2026, Calendar.MARCH, 1)
        
        for (i in 0..11) {
            val year = calendar.get(Calendar.YEAR)
            val month = String.format("%02d", calendar.get(Calendar.MONTH) + 1)
            dates.add("$year$month")
            calendar.add(Calendar.MONTH, -1)
        }

        val dateAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, dates)
        binding.spinnerDate.adapter = dateAdapter
        binding.spinnerDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedDate = dates[position]
                fetchNewSongs()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupBrandButtons() {
        binding.btnTj.setOnClickListener { selectedBrand = "tj"; fetchNewSongs() }
        binding.btnKy.setOnClickListener { selectedBrand = "kumyoung"; fetchNewSongs() }
        binding.btnJoy.setOnClickListener { selectedBrand = "joysound"; fetchNewSongs() }
        binding.btnDam.setOnClickListener { selectedBrand = "dam"; fetchNewSongs() }
    }

    private fun fetchNewSongs() {
        if (selectedDate.isEmpty()) return
        
        binding.progressBar.visibility = View.VISIBLE
        binding.tvMessage.visibility = View.GONE

        scope.launch {
            try {
                // KaraokeApi.kt에 추가한 getNewSongs 호출
                val response = KaraokeApi.service.getNewSongs(selectedDate, selectedBrand)
                if (response.isSuccessful) {
                    val songs = response.body() ?: emptyList()
                    binding.recyclerView.layoutManager = LinearLayoutManager(context)
                    binding.recyclerView.adapter = SongAdapter(requireContext(), songs)
                } else {
                    binding.tvMessage.text = "데이터를 불러오지 못했습니다."
                    binding.tvMessage.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                binding.tvMessage.text = "네트워크 오류가 발생했습니다."
                binding.tvMessage.visibility = View.VISIBLE
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        scope.cancel()
    }
}
