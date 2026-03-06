package org.karatomo.app.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import org.karatomo.app.R
import org.karatomo.app.managers.BookmarkManager
import org.karatomo.app.ui.adapter.SongAdapter

class PlaylistDetailActivity : AppCompatActivity() {
    private lateinit var adapter: SongAdapter
    private var playlistName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_detail)

        playlistName = intent.getStringExtra("playlist_name") ?: "기본 플레이리스트"
        val tvTitle = findViewById<TextView>(R.id.tvPlaylistTitle)
        tvTitle.text = playlistName

        val rv = findViewById<RecyclerView>(R.id.rvPlaylistDetail)
        adapter = SongAdapter(BookmarkManager.getSongs(playlistName))
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        // 편집 아이콘 클릭 시 메뉴 노출
        findViewById<ImageView>(R.id.ivEditMode).setOnClickListener {
            showEditMenu()
        }
    }

    private fun showEditMenu() {
        val menus = arrayOf("이름 변경", "브랜드 필터", "플레이리스트 삭제")
        AlertDialog.Builder(this).setItems(menus) { _, which ->
            when (which) {
                0 -> showRenameDialog()
                1 -> showFilterDialog()
                2 -> showDeleteDialog()
            }
        }.show()
    }

    private fun showRenameDialog() {
        val et = EditText(this).apply { setText(playlistName) }
        AlertDialog.Builder(this).setTitle("이름 변경").setView(et)
            .setPositiveButton("변경") { _, _ ->
                val newName = et.text.toString()
                if (BookmarkManager.renamePlaylist(this, playlistName, newName)) {
                    playlistName = newName
                    findViewById<TextView>(R.id.tvPlaylistTitle).text = playlistName
                }
            }.show()
    }

private fun showFilterDialog() {
    val brands = arrayOf("전체", "TJ", "금영", "Joysound", "DAM")
    AlertDialog.Builder(this).setTitle("브랜드 선택").setItems(brands) { _, which ->
        val filter = when(which) {
            1 -> "tj"
            2 -> "kumyoung"
            3 -> "joysound"
            4 -> "dam"
            else -> "all"
        }
        val filtered = if (filter == "all") BookmarkManager.getSongs(playlistName)
        else BookmarkManager.getSongs(playlistName).filter { it.brand?.lowercase() == filter }
        adapter.updateData(filtered)
    }.show()
}

    private fun showDeleteDialog() {
        AlertDialog.Builder(this).setTitle("삭제").setMessage("이 플레이리스트를 삭제할까요?")
            .setPositiveButton("삭제") { _, _ ->
                BookmarkManager.deletePlaylist(this, playlistName)
                finish()
            }.show()
    }
}
