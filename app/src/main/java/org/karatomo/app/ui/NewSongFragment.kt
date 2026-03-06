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
    private lateinit var spinnerMonth: Spinner
    private var currentBrand = "tj" // 기본값
    private val monthList = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_new_song, container, false)
        
        progressBar = view.findViewById(R.id.progressBar)
        spinnerMonth = view.findViewById(R.id.spinnerMonth)
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        val rg = view.findViewById<RadioGroup>(R.id.rgBrand)

        // 1. 리사이클러뷰 설정
        adapter = SongAdapter(mutableListOf())
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        // 2. 최근 12개월 목록 생성 (서버 규격: yyyyMM)
        val sdf = SimpleDateFormat("yyyyMM", Locale.getDefault())
        val cal = Calendar.getInstance()
        for (i in 0 until 12) {
            monthList.add(sdf.format(cal.time))
            cal.add(Calendar.MONTH, -1)
        }

        val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, monthList)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonth.adapter = monthAdapter

        // 3. 월 선택 이벤트
        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                loadNewSongs()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        // 4. 브랜드 선택 이벤트 (서버 규격으로 변환)
        rg.setOnCheckedChangeListener { _, checkedId ->
            currentBrand = when(checkedId) {
                R.id.rbKy -> "kumyoung" // 'ky'가 아니라 'kumyoung'이어야 함
                R.id.rbJoy -> "joysound"
                R.id.rbDam -> "dam"
                else -> "tj"
            }
            loadNewSongs()
        }

        return view
    }

    private fun loadNewSongs() {
        val selectedMonth = spinnerMonth.selectedItem?.toString() ?: return
        val apiUrl = "https://api.manana.kr/karaoke/release.json?release=$selectedMonth&brand=$currentBrand"
        
        // [중요] 빌드 후 이 주소를 직접 확인해보세요.
        android.widget.Toast.makeText(requireContext(), "URL: $apiUrl", android.widget.Toast.LENGTH_LONG).show()
    
        progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val list = org.karatomo.app.network.KaraokeApi.service.getReleaseSongs(selectedMonth, currentBrand)
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    if (!isAdded) return@withContext
                    progressBar.visibility = View.GONE
                    if (list.isEmpty()) {
                        // 결과 없음 처리
                        android.widget.Toast.makeText(requireContext(), "해당 조건의 신곡이 없습니다.", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    adapter.updateData(list)
                }
            } catch (e: Exception) {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    android.widget.Toast.makeText(requireContext(), "서버 연결 실패", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
