package org.karatomo.app.network

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import org.karatomo.app.BookmarkManager
import org.karatomo.app.R
import org.karatomo.app.databinding.ItemSongBinding

class SongAdapter(private val context: Context, private var songList: List<Song>) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private val scope = CoroutineScope(Dispatchers.Main + Job())

    inner class SongViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(context), parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songList[position]
        holder.binding.tvTitle.text = song.title
        holder.binding.tvSinger.text = song.singer
        holder.binding.tvNo.text = song.no ?: (song.noTj ?: song.noKy ?: "")

        // 추가 버튼(별표 등) 클릭 시 지능형 추가 로직 실행
        holder.itemView.setOnClickListener {
            showAddSongDialog(song)
        }
    }

    override fun getItemCount(): Int = songList.size

    /**
     * 제목에서 괄호 및 특수문자를 제거하여 순수 키워드만 추출
     */
    private fun cleanTitle(title: String): String {
        return title.replace(Regex("\\(.*?\\)"), "").trim()
    }

    /**
     * 타 브랜드 번호 통합 검색 및 추가 다이얼로그
     */
    private fun showAddSongDialog(baseSong: Song) {
        val cleanedTitle = cleanTitle(baseSong.title)
        val dialogView = LayoutInflater.from(context).inflate(android.R.layout.select_dialog_multichoice, null)
        val builder = AlertDialog.Builder(context)
        builder.setTitle("보관함에 추가 (타 브랜드 검색 중...)")

        // 탭 선택을 위한 목록 준비
        val playlistNames = BookmarkManager.playlists.map { it.name }.toTypedArray()
        
        // 1. 먼저 어느 탭에 넣을지 선택
        AlertDialog.Builder(context)
            .setTitle("추가할 탭 선택")
            .setItems(playlistNames) { _, which ->
                performIntegratedSearch(baseSong, cleanedTitle, which)
            }
            .show()
    }

    private fun performIntegratedSearch(baseSong: Song, query: String, playlistIndex: Int) {
        scope.launch {
            // Toast로 진행 상태 알림 (로그 확인 불가 대안)
            Toast.makeText(context, "'$query'로 타 브랜드 번호를 검색합니다.", Toast.LENGTH_SHORT).show()

            val brands = listOf("tj", "kumyoung", "joysound", "dam")
            val resultsMap = mutableMapOf<String, List<Song>>()

            // 모든 브랜드 병렬 검색
            val jobs = brands.map { brand ->
                async(Dispatchers.IO) {
                    try {
                        val response = KaraokeApi.service.getSongs(brand, query)
                        if (response.isSuccessful) {
                            resultsMap[brand] = response.body() ?: emptyList()
                        }
                    } catch (e: Exception) {
                        resultsMap[brand] = emptyList()
                    }
                }
            }
            jobs.awaitAll()

            // 검색된 결과를 바탕으로 최종 선택 다이얼로그 표시
            showSelectionDialog(baseSong, resultsMap, playlistIndex)
        }
    }

    private fun showSelectionDialog(baseSong: Song, resultsMap: Map<String, List<Song>>, playlistIndex: Int) {
        val finalSong = Song(
            title = cleanTitle(baseSong.title),
            originalTitle = baseSong.title,
            singer = baseSong.singer,
            noTj = if (baseSong.brand == "tj") baseSong.no else null,
            noKy = if (baseSong.brand == "kumyoung") baseSong.no else null,
            noDam = if (baseSong.brand == "dam") baseSong.no else null,
            noJoy = if (baseSong.brand == "joysound") baseSong.no else null
        )

        // 각 브랜드별로 가장 유사한(제목/가수 일치) 곡 자동 매칭
        resultsMap.forEach { (brand, list) ->
            val match = list.find { it.title.contains(finalSong.title) && it.singer == finalSong.singer }
            when (brand) {
                "tj" -> if (finalSong.noTj == null) finalSong.noTj = match?.no
                "kumyoung" -> if (finalSong.noKy == null) finalSong.noKy = match?.no
                "dam" -> if (finalSong.noDam == null) finalSong.noDam = match?.no
                "joysound" -> if (finalSong.noJoy == null) finalSong.noJoy = match?.no
            }
        }

        // 결과 확인 및 저장
        val msg = StringBuilder().apply {
            append("다음 번호들이 저장됩니다:\n")
            append("TJ: ${finalSong.noTj ?: "없음"}\n")
            append("금영: ${finalSong.noKy ?: "없음"}\n")
            append("DAM: ${finalSong.noDam ?: "없음"}\n")
            append("JOY: ${finalSong.noJoy ?: "없음"}")
        }.toString()

        AlertDialog.Builder(context)
            .setTitle("보관함 저장 확인")
            .setMessage(msg)
            .setPositiveButton("저장") { _, _ ->
                val success = BookmarkManager.addSongToPlaylist(context, playlistIndex, finalSong)
                if (success) {
                    Toast.makeText(context, "보관함에 저장되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "이미 존재하는 곡입니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    fun updateData(newList: List<Song>) {
        this.songList = newList
        notifyDataSetChanged()
    }
}
