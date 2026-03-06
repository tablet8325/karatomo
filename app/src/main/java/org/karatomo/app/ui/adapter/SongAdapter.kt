package org.karatomo.app.ui.adapter

import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import org.karatomo.app.R
import org.karatomo.app.managers.BookmarkManager
import org.karatomo.app.network.Song

class SongAdapter(private val songs: MutableList<Song>) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

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
        holder.tvNo.text = "${song.brand?.uppercase() ?: ""} ${song.no ?: ""}"

        holder.btnBookmark.setOnClickListener {
            val playlists = BookmarkManager.playlists
            if (playlists.isEmpty()) {
                Toast.makeText(it.context, "보관함 탭에서 플레이리스트를 먼저 만들어주세요!", Toast.LENGTH_SHORT).show()
            } else {
                val names = playlists.map { it.name }.toTypedArray()
                AlertDialog.Builder(it.context)
                    .setTitle("플레이리스트 선택")
                    .setItems(names) { _, which ->
                        playlists[which].songs.add(song)
                        Toast.makeText(it.context, "${playlists[which].name}에 추가됨", Toast.LENGTH_SHORT).show()
                    }.show()
            }
        }
    }

    override fun getItemCount() = songs.size
    fun updateData(newList: List<Song>) { songs.clear(); songs.addAll(newList); notifyDataSetChanged() }
}
