package org.karatomo.app.ui.adapter

import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import org.karatomo.app.R
import org.karatomo.app.manager.BookmarkManager
import org.karatomo.app.network.Song

class SongAdapter(private val songs: MutableList<Song>) :
    RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvSinger: TextView = view.findViewById(R.id.tvSinger)
        val tvNo: TextView = view.findViewById(R.id.tvNo)
        val btnBookmark: Button = view.findViewById(R.id.btnBookmark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]
        holder.tvTitle.text = song.title ?: "(제목 없음)"
        holder.tvSinger.text = song.singer ?: "(가수 없음)"
        holder.tvNo.text = "${song.brand ?: ""} ${song.no ?: ""}"

        holder.btnBookmark.setOnClickListener {
            BookmarkManager.addSong(song)
            Toast.makeText(holder.itemView.context, "${song.title} 북마크 추가", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = songs.size

    fun updateData(newList: List<Song>) {
        songs.clear()
        songs.addAll(newList)
        notifyDataSetChanged()
    }
}
