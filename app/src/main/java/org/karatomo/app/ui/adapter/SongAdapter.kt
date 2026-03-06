package org.karatomo.app.ui.adapter

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.karatomo.app.R
import org.karatomo.app.network.Song

class SongAdapter(private var songs: List<Song>) : RecyclerView.Adapter<SongAdapter.VH>() {

    fun updateData(newSongs: List<Song>) {
        this.songs = newSongs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val song = songs[position]
        // [오류방지] 모든 텍스트에 대해 null safe 처리 (엘비스 연산자 사용)
        holder.tvTitle.text = song.title ?: "제목 없음"
        holder.tvSinger.text = song.singer ?: "가수 없음"
        holder.tvNo.text = song.no ?: "-"
    }

    override fun getItemCount() = songs.size

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.tvSongTitle)
        val tvSinger: TextView = v.findViewById(R.id.tvSongSinger)
        val tvNo: TextView = v.findViewById(R.id.tvSongNo)
    }
}
