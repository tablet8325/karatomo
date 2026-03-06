package org.karatomo.app.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.karatomo.app.R // [해결] R 명시적 임포트
import org.karatomo.app.managers.BookmarkManager
import org.karatomo.app.ui.adapter.PlaylistDetailAdapter

class PlaylistDetailActivity : AppCompatActivity() {
    private lateinit var detailAdapter: PlaylistDetailAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private var playlistName: String = "기본 플레이리스트"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_detail)

        playlistName = intent.getStringExtra("playlistName") ?: "기본 플레이리스트"
        findViewById<TextView>(R.id.tvPlaylistTitle).text = playlistName

        val songs = BookmarkManager.getSongs(playlistName).toMutableList()
        val rv = findViewById<RecyclerView>(R.id.rvPlaylistDetail)

        // 드래그 앤 드롭 설정
        val callback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                detailAdapter.moveItem(from, to)
                BookmarkManager.moveSong(this@PlaylistDetailActivity, playlistName, from, to)
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        }
        
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rv)

        detailAdapter = PlaylistDetailAdapter(songs, 
            onLongClick = { /* 삭제 */ },
            onDragStart = { vh -> itemTouchHelper.startDrag(vh) }
        )
        
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = detailAdapter

        findViewById<ImageView>(R.id.ivEditMode).setOnClickListener {
            detailAdapter.isEditMode = !detailAdapter.isEditMode
        }
    }
}
