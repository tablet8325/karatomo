package org.karatomo.app.ui

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
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
        
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val list = KaraokeApi.service.getReleaseSongs(selectedMonth, currentBrand)
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    adapter.updateData(list)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "오류: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
