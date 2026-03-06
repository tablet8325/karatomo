package org.karatomo.app.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.karatomo.app.R
import org.karatomo.app.managers.BookmarkManager
import org.karatomo.app.ui.adapter.PlaylistDetailAdapter

class PlaylistDetailActivity : AppCompatActivity() {
    private lateinit var adapter: PlaylistDetailAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private var playlistName: String = "기본 플레이리스트"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // [수정] R.layout 참조 확인
        setContentView(R.layout.activity_playlist_detail)

        playlistName = intent.getStringExtra("playlistName") ?: "기본 플레이리스트"
        findViewById<TextView>(R.id.tvPlaylistTitle).text = playlistName

        val songs = BookmarkManager.getSongs(playlistName).toMutableList()
        val rv = findViewById<RecyclerView>(R.id.rvPlaylistDetail)

        // 드래그 콜백 설정
        val callback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                adapter.moveItem(from, to)
                BookmarkManager.moveSong(this@PlaylistDetailActivity, playlistName, from, to)
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        }
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rv)

        adapter = PlaylistDetailAdapter(songs, 
            onLongClick = { /* 삭제 다이얼로그 추가 가능 */ },
            onDragStart = { viewHolder -> itemTouchHelper.startDrag(viewHolder) }
        )
        
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        // 편집 모드 전환 버튼
        findViewById<ImageView>(R.id.ivEditMode).setOnClickListener {
            adapter.isEditMode = !adapter.isEditMode
            val msg = if(adapter.isEditMode) "편집 모드" else "조회 모드"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }
}
