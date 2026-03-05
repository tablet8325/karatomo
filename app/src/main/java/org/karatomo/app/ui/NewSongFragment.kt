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
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val apiMonths = mutableListOf<String>()
    private val displayMonths = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_new_song, container, false)
        progressBar = view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.recyclerView)
        val rgBrand = view.findViewById<RadioGroup>(R.id.rgBrandNew)
        val spinnerMonth = view.findViewById<Spinner>(R.id.spinnerMonth)

        // 최근 12개월 생성
        setupMonths()
        spinnerMonth.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, displayMonths)

        adapter = SongAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // 리스너: 변경 시 자동 로드
        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                fetch(rgBrand, spinnerMonth)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        spinnerMonth.onItemSelectedListener = listener
        rgBrand.setOnCheckedChangeListener { _, _ -> fetch(rgBrand, spinnerMonth) }

        return view
    }

    private fun setupMonths() {
        val cal = Calendar.getInstance()
        val apiFmt = SimpleDateFormat("yyyyMM", Locale.KOREA)
        val dispFmt = SimpleDateFormat("yyyy년 MM월", Locale.KOREA)
        for (i in 0 until 12) {
            apiMonths.add(apiFmt.format(cal.time))
            displayMonths.add(dispFmt.format(cal.time))
            cal.add(Calendar.MONTH, -1)
        }
    }

    private fun fetch(rg: RadioGroup, sp: Spinner) {
        val brand = when(rg.checkedRadioButtonId) {
            R.id.rbKyNew -> "kumyoung"
            R.id.rbJoyNew -> "joysound"
            R.id.rbDamNew -> "dam"
            else -> "tj"
        }
        val month = apiMonths[sp.selectedItemPosition]
        load(month, brand)
    }

    private fun load(month: String, brand: String) {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val list = KaraokeApi.service.getReleaseSongs(month, brand)
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    adapter.updateData(list)
                    // [핵심] 리스트 최상단으로 자동 스크롤
                    recyclerView.scrollToPosition(0)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { progressBar.visibility = View.GONE }
            }
        }
    }
}
