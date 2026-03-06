package org.karatomo.app.ui.adapter

import android.graphics.Color
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.karatomo.app.R
import org.karatomo.app.ui.MergedSong

class MergedSongAdapter(private var items: List<MergedSong>, private var filter: String = "전체") : RecyclerView.Adapter<MergedSongAdapter.VH>() {

    fun updateData(newItems: List<MergedSong>, newFilter: String) {
        items = newItems
        filter = newFilter
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_merged_song, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvTitle.text = item.title
        holder.tvSinger.text = item.singer
        
        // 번호 표시 로직 (이미지처럼 우측에 브랜드별 번호 나열)
        val sb = StringBuilder()
        if (filter == "전체") {
            item.brandNumbers.forEach { (b, no) -> sb.append("[$b] $no  ") }
        } else {
            val key = filter.lowercase().replace("금영", "kumyoung")
            sb.append(item.brandNumbers[key] ?: "")
        }
        holder.tvNo.text = sb.toString()
    }

    override fun getItemCount() = items.size

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.tvSongTitle)
        val tvSinger: TextView = v.findViewById(R.id.tvSongSinger)
        val tvNo: TextView = v.findViewById(R.id.tvSongNo)
    }
}
