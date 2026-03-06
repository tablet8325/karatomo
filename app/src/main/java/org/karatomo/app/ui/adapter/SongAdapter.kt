package org.karatomo.app.ui.adapter

import android.graphics.Color
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import org.karatomo.app.R
import org.karatomo.app.managers.BookmarkManager
import org.karatomo.app.network.Song

class SongAdapter(private var songs: List<Song>) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    fun updateData(newSongs: List<Song>) {
        songs = newSongs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]
        holder.tvTitle.text = song.title
        holder.tvSinger.text = song.singer
        holder.tvNo.text = song.no

        // onBindViewHolder 내부 색상 적용 부분
        val brandColor = when (song.brand?.lowercase()) {
            "tj" -> "#FF5722"
            "kumyoung" -> "#2196F3"
            "joysound" -> "#F44336"
            "dam" -> "#FF4081"
            else -> "#FFFFFF"
        }
        holder.tvNo.setTextColor(Color.parseColor(brandColor))

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val playlists = BookmarkManager.getPlaylistNames()
            val items = playlists.toTypedArray<CharSequence>()

            AlertDialog.Builder(context)
                .setTitle("플레이리스트 선택")
                .setItems(items) { _, which ->
                    val selectedName = playlists[which]
                    if (BookmarkManager.addSong(context, selectedName, song)) {
                        Toast.makeText(context, "추가되었습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "이미 추가된 곡입니다.", Toast.LENGTH_SHORT).show()
                    }
                }.show()
        }
    }

    override fun getItemCount(): Int = songs.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvSongTitle)
        val tvSinger: TextView = view.findViewById(R.id.tvSongSinger)
        val tvNo: TextView = view.findViewById(R.id.tvSongNo)
    }
}
