package org.karatomo.app.ui.adapter

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import org.karatomo.app.R
import org.karatomo.app.network.Song

class PlaylistDetailAdapter(
    private val songs: MutableList<Song>,
    private val onLongClick: (Song) -> Unit,
    private val onDragStart: (RecyclerView.ViewHolder) -> Unit
) : RecyclerView.Adapter<PlaylistDetailAdapter.ViewHolder>() {

    // 편집 모드 여부에 따라 뷰 전환
    var isEditMode: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_playlist_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]
        holder.tvTitle.text = song.title
        holder.tvSinger.text = song.singer
        holder.tvNo.text = song.no

        // 편집 모드일 때 번호 숨기고 핸들 노출
        if (isEditMode) {
            holder.tvNo.visibility = View.GONE
            holder.ivDrag.visibility = View.VISIBLE
            holder.ivDrag.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    onDragStart(holder)
                }
                false
            }
        } else {
            holder.tvNo.visibility = View.VISIBLE
            holder.ivDrag.visibility = View.GONE
        }

        holder.itemView.setOnLongClickListener {
            onLongClick(song)
            true
        }
    }

    override fun getItemCount(): Int = songs.size

    fun moveItem(fromPos: Int, toPos: Int) {
        val movedItem = songs.removeAt(fromPos)
        songs.add(toPos, movedItem)
        notifyItemMoved(fromPos, toPos)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvSongTitle)
        val tvSinger: TextView = view.findViewById(R.id.tvSongSinger)
        val tvNo: TextView = view.findViewById(R.id.tvSongNo)
        val ivDrag: ImageView = view.findViewById(R.id.ivDragHandle)
    }
}
