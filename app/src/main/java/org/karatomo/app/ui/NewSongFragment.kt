package org.karatomo.app.ui

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.karatomo.app.databinding.FragmentNewSongBinding
import org.karatomo.app.network.KaraokeApi
import org.karatomo.app.network.SongAdapter
import kotlinx.coroutines.*
import java.util.*

class NewSongFragment : Fragment() {
    private var _binding: FragmentNewSongBinding? = null
    private val binding get() = _binding!!
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    
    private var selectedDate = "202603"
    private var selectedBrand = "tj"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewSongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDateSpinner()
        setupBrandButtons()
        fetchNewSongs() // 초기 실행 (202603, TJ)
    }

    private fun setupDateSpinner() {
        val dates = mutableListOf<String>()
        val cal = Calendar.getInstance().apply { set(2026, Calendar.MARCH, 1) }
        
        for (i in 0..11) {
            val year = cal.get(Calendar.YEAR)
            val month = String.format("%02d", cal.get(Calendar.MONTH) + 1)
            dates.add("$year$month")
            cal.add(Calendar.MONTH, -1)
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, dates)
        binding.spinnerDate.adapter = adapter
        binding.spinnerDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                selectedDate = dates[pos]
                fetchNewSongs()
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
    }

    private fun setupBrandButtons() {
        binding.rgBrand.setOnCheckedChangeListener { _, id ->
            selectedBrand = when(id) {
                org.karatomo.app.R.id.rbKy -> "kumyoung"
                else -> "tj"
            }
            fetchNewSongs()
        }
    }

    private fun fetchNewSongs() {
        binding.progressBar.visibility = View.VISIBLE
        scope.launch {
            try {
                // 수정된 API 경로 호출: /karaoke/release/YYYYMM/brand.json
                val response = KaraokeApi.service.getNewSongs(selectedDate, selectedBrand)
                if (response.isSuccessful) {
                    val songs = response.body() ?: emptyList()
                    binding.rvNewSongs.layoutManager = LinearLayoutManager(context)
                    binding.rvNewSongs.adapter = SongAdapter(requireContext(), songs)
                }
            } catch (e: Exception) {
                // 에러 발생 시 처리
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}
