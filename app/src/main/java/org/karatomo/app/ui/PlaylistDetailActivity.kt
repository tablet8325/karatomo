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
    private lateinit var detailAdapter: PlaylistDetailAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private var playlistName: String = "기본 플레이리스트"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_detail)

        // 1. 데이터 가져오기 (이름표를 "playlist_name"으로 통일 권장)
        playlistName = intent.getStringExtra("playlist_name") ?: "기본 플레이리스트"
        
        // 2. 제목 설정 (XML에 tvPlaylistTitle이 있는지 꼭 확인!)
        val tvTitle = findViewById<TextView>(R.id.tvPlaylistTitle)
        tvTitle.text = playlistName

        // 3. 데이터 로드
        val songs = BookmarkManager.getSongs(playlistName).toMutableList()
        val rv = findViewById<RecyclerView>(R.id.rvPlaylistDetail)

        // 4. 어댑터 먼저 생성
        detailAdapter = PlaylistDetailAdapter(songs, 
            onLongClick = { song ->
                // 삭제 로직 추가 가능
                BookmarkManager.removeSong(this, playlistName, song)
                // 리스트 갱신 코드 필요시 추가
            },
            onDragStart = { vh -> itemTouchHelper.startDrag(vh) }
        )

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = detailAdapter

        // 5. 드래그 앤 드롭 설정
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

        // 6. 편집 모드 버튼 (XML에 ivEditMode가 있는지 확인)
        findViewById<ImageView>(R.id.ivEditMode).setOnClickListener {
            detailAdapter.isEditMode = !detailAdapter.isEditMode
            detailAdapter.notifyDataSetChanged() // 화면 갱신
        }
    }
}
