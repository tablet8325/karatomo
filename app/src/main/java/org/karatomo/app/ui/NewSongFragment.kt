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
import java.text.SimpleDateFormat
import java.util.*

class NewSongFragment : Fragment() {
    private lateinit var adapter: SongAdapter
    private lateinit var progressBar: ProgressBar
    private val monthList = mutableListOf<String>() // API용 (202603)
    private val displayList = mutableListOf<String>() // 화면 표시용 (2026년 03월)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_new_song, container, false)
        
        progressBar = view.findViewById(R.id.progressBar)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val rgBrand = view.findViewById<RadioGroup>(R.id.rgBrandNew) // XML에 추가 필요
        val spinnerMonth = view.findViewById<Spinner>(R.id.spinnerMonth)

        // 최근 12개월 리스트 생성
        setupMonthList()
        val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, displayList)
        spinnerMonth.adapter = monthAdapter

        adapter = SongAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // 브랜드나 날짜가 바뀔 때마다 자동 로드
        val onChangeListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                fetchSongs(rgBrand, spinnerMonth)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerMonth.onItemSelectedListener = onChangeListener
        rgBrand.setOnCheckedChangeListener { _, _ -> fetchSongs(rgBrand, spinnerMonth) }

        return view
    }

    private fun setupMonthList() {
        val cal = Calendar.getInstance()
        val apiFormat = SimpleDateFormat("yyyy breakout MM", Locale.KOREA).apply { 
            // API가 원하는 형식은 202603 이므로 공백 없이
            applyPattern("yyyyMM")
        }
        val displayFormat = SimpleDateFormat("yyyy년 MM월", Locale.KOREA)

        for (i in 0 until 12) {
            monthList.add(apiFormat.format(cal.time))
            displayList.add(displayFormat.format(cal.time))
            cal.add(Calendar.MONTH, -1) // 한 달씩 뒤로
        }
    }

    private fun fetchSongs(rgBrand: RadioGroup, spinner: Spinner) {
        val brand = when(rgBrand.checkedRadioButtonId) {
            R.id.rbKyNew -> "kumyoung"
            R.id.rbJoyNew -> "joysound"
            R.id.rbDamNew -> "dam"
            else -> "tj"
        }
        val selectedMonth = monthList[spinner.selectedItemPosition]
        
        loadReleaseSongs(selectedMonth, brand)
    }

    private fun loadReleaseSongs(month: String, brand: String) {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val list = KaraokeApi.service.getReleaseSongs(month, brand)
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
