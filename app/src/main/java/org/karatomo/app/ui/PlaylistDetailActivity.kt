package org.karatomo.app.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import org.karatomo.app.R
import org.karatomo.app.managers.BookmarkManager
import org.karatomo.app.ui.adapter.PlaylistDetailAdapter

class PlaylistDetailActivity : AppCompatActivity() {
    private lateinit var adapter: PlaylistDetailAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private var playlistName: String = "기본 플레이리스트"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_detail)

        playlistName = intent.getStringExtra("playlistName") ?: "기본 플레이리스트"
        findViewById<TextView>(R.id.tvPlaylistTitle).text = playlistName

        val songs = BookmarkManager.getSongs(playlistName).toMutableList()
        val rv = findViewById<RecyclerView>(R.id.rvPlaylistDetail)

        // 드래그 기능 설정
        val callback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val from = vh.adapterPosition
                val to = target.adapterPosition
                adapter.moveItem(from, to)
                BookmarkManager.moveSong(this@PlaylistDetailActivity, playlistName, from, to)
                return true
            }
            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {}
        }
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rv)

        adapter = PlaylistDetailAdapter(songs, 
            onLongClick = { /* 삭제 로직 */ },
            onDragStart = { viewHolder -> itemTouchHelper.startDrag(viewHolder) }
        )
        
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        //
        findViewById<ImageView>(R.id.ivEditMode).setOnClickListener {
            adapter.isEditMode = !adapter.isEditMode
        }
    }
}
