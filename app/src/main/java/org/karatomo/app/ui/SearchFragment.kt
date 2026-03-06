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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // XML ID와 1:1 매칭 (실종된 버튼과 에딧텍스트 복구)
        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        val btnSearch = view.findViewById<Button>(R.id.btnSearch)
        val rvSearch = view.findViewById<RecyclerView>(R.id.rvSearch)
        val tvNoResult = view.findViewById<TextView>(R.id.tvNoResult)

        adapter = SongAdapter(emptyList())
        rvSearch?.layoutManager = LinearLayoutManager(context)
        rvSearch?.adapter = adapter

        btnSearch?.setOnClickListener {
            val query = etSearch?.text.toString()
            if (query.isNotBlank()) {
                performSearch(query, tvNoResult, rvSearch)
            }
        }
    }

    private fun performSearch(query: String, tvNoResult: TextView?, rvSearch: RecyclerView?) {
        KaraokeApi.service.searchSongs(query, "tj", "title").enqueue(object : Callback<List<Song>> {
            override fun onResponse(call: Call<List<Song>>, response: Response<List<Song>>) {
                if (!isAdded) return
                val list = response.body()
                // 서버 데이터 유효성 체크 (알맹이가 있는지 확인)
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
