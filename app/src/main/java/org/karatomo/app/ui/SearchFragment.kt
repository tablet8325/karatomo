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

class SearchFragment : Fragment() {
    private lateinit var adapter: SongAdapter
    private var tvNoResult: TextView? = null
    private var rvSearch: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        
        // XML ID와 매칭 확인 완료
        tvNoResult = view.findViewById(R.id.tvNoResult)
        rvSearch = view.findViewById(R.id.rvSearch)
        
        adapter = SongAdapter(emptyList())
        rvSearch?.layoutManager = LinearLayoutManager(context)
        rvSearch?.adapter = adapter

        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        view.findViewById<Button>(R.id.btnSearch).setOnClickListener {
            performSearch(etSearch.text.toString())
        }
        return view
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) return
        
        KaraokeApi.service.searchSongs(query, "tj", "title").enqueue(object : Callback<List<Song>> {
            override fun onResponse(call: Call<List<Song>>, response: Response<List<Song>>) {
                if (!isAdded) return
                val list = response.body()
                // 리스트가 실제 곡 정보를 담고 있는지 검증 (no 필드 체크)
                if (list.isNullOrEmpty() || list[0].no.isNullOrBlank()) {
                    tvNoResult?.visibility = View.VISIBLE
                    rvSearch?.visibility = View.GONE
                } else {
                    tvNoResult?.visibility = View.GONE
                    rvSearch?.visibility = View.VISIBLE
                    adapter.updateData(list)
                }
            }
            override fun onFailure(call: Call<List<Song>>, t: Throwable) {
                if (!isAdded) return
                Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
