package org.karatomo.app.ui

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import org.karatomo.app.R
import org.karatomo.app.network.*
import org.karatomo.app.ui.adapter.SongAdapter
import retrofit2.*
import java.text.SimpleDateFormat
import java.util.*

class NewSongFragment : Fragment() {
    private lateinit var adapter: SongAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var spinnerMonth: Spinner
    private var currentBrand = "tj"
    private val monthList = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_new_song, container, false)
        
        progressBar = view.findViewById(R.id.progressBar)
        spinnerMonth = view.findViewById(R.id.spinnerMonth)
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        val rg = view.findViewById<RadioGroup>(R.id.rgBrand)

        adapter = SongAdapter(emptyList())
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = adapter

        // 최근 12개월 생성 (yyyyMM 형식)
        val sdf = SimpleDateFormat("yyyyMM", Locale.getDefault())
        val cal = Calendar.getInstance()
        for (i in 0 until 12) {
            monthList.add(sdf.format(cal.time))
            cal.add(Calendar.MONTH, -1)
        }

        val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, monthList)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonth.adapter = monthAdapter

        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                loadNewSongs()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        rg.setOnCheckedChangeListener { _, checkedId ->
            currentBrand = when(checkedId) {
                R.id.rbKy -> "kumyoung"
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
        progressBar.visibility = View.VISIBLE
        
        // [핵심 수정] Call 객체를 명시적으로 받고 Callback을 연결하여 타입 미스매치 방지
        val call: Call<List<Song>> = KaraokeApi.service.getReleaseSongs(selectedMonth, currentBrand)
        
        call.enqueue(object : Callback<List<Song>> {
            override fun onResponse(call: Call<List<Song>>, response: Response<List<Song>>) {
                if (!isAdded) return // 프래그먼트가 유효할 때만 처리
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val list = response.body()
                    adapter.updateData(list ?: emptyList())
                } else {
                    Toast.makeText(context, "에러: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Song>>, t: Throwable) {
                if (!isAdded) return
                progressBar.visibility = View.GONE
                Toast.makeText(context, "연결 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
