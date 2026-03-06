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
        
        // 브랜드 필터에 따른 번호 표시 (기존 로직)
        val displayNo = when {
            !song.noTj.isNullOrEmpty() -> "TJ: ${song.noTj}"
            !song.noKy.isNullOrEmpty() -> "KY: ${song.noKy}"
            else -> song.no ?: ""
        }
        holder.binding.tvNo.text = displayNo

        // 단일 클릭: 보관함 추가 다이얼로그
        holder.itemView.setOnClickListener {
            showAddSongDialog(song)
        }

        // 길게 누르기: 곡 상세 정보 팝업
        holder.itemView.setOnLongClickListener {
            showSongDetailDialog(song)
            true
        }
    }

    /**
     * 곡의 상세 정보를 보여주는 팝업 (작곡, 작사, 추가일 등)
     */
    private fun showSongDetailDialog(song: Song) {
        val detailMsg = """
            [곡 정보]
            제목: ${song.originalTitle ?: song.title}
            가수: ${song.singer}
            작곡: ${song.composer.ifEmpty { "정보 없음" }}
            작사: ${song.lyricist.ifEmpty { "정보 없음" }}
            
            [보관함 정보]
            추가일: ${song.addedDate.ifEmpty { "기록 없음" }}
            
            [브랜드별 번호]
            TJ: ${song.noTj ?: "-"}
            KY: ${song.noKy ?: "-"}
            JOY: ${song.noJoy ?: "-"}
            DAM: ${song.noDam ?: "-"}
        """.trimIndent()

        AlertDialog.Builder(context)
            .setTitle("상세 정보")
            .setMessage(detailMsg)
            .setPositiveButton("확인", null)
            .show()
    }

    override fun getItemCount(): Int = songList.size

    /**
     * 강화된 제목 정제 로직
     * 1. 괄호와 그 안의 내용 전체 삭제: (드라마 OST), [Live] 등
     * 2. 특수기호 이후 내용 삭제: - (하이픈), / (슬래시) 뒤에 오는 부제 제거
     * 3. 양끝 공백 제거
     */
    private fun cleanTitle(title: String): String {
        return title
            .replace(Regex("\\(.*?\\)"), "") // 괄호() 내용 삭제
            .replace(Regex("\\[.*?\\]"), "") // 대괄호[] 내용 삭제
            .split("-")[0]                   // 하이픈 뒤 삭제
            .split("/")[0]                   // 슬래시 뒤 삭제
            .trim()                          // 양끝 공백 제거
    }

    private fun showAddSongDialog(baseSong: Song) {
        val cleanedTitle = cleanTitle(baseSong.title)
        val playlistNames = BookmarkManager.playlists.map { it.name }.toTypedArray()
        
        if (playlistNames.isEmpty()) {
            Toast.makeText(context, "생성된 탭이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(context)
            .setTitle("추가할 탭 선택")
            .setItems(playlistNames) { _, which ->
                performIntegratedSearch(baseSong, cleanedTitle, which)
            }
            .show()
    }

    private fun performIntegratedSearch(baseSong: Song, query: String, playlistIndex: Int) {
        scope.launch {
            Toast.makeText(context, "'$query' 키워드로 통합 검색 중...", Toast.LENGTH_SHORT).show()

            val brands = listOf("tj", "kumyoung", "joysound", "dam")
            val resultsMap = mutableMapOf<String, List<Song>>()

            // 병렬 네트워크 통신
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

            showSelectionDialog(baseSong, resultsMap, playlistIndex, query)
        }
    }

    private fun showSelectionDialog(baseSong: Song, resultsMap: Map<String, List<Song>>, playlistIndex: Int, cleanedTitle: String) {
        // 새 통합 곡 객체 생성
        val finalSong = Song(
            title = cleanedTitle, 
            originalTitle = baseSong.title,
            singer = baseSong.singer,
            // 기본 정보 채우기
            noTj = if (baseSong.brand == "tj") baseSong.no else null,
            noKy = if (baseSong.brand == "kumyoung") baseSong.no else null,
            noDam = if (baseSong.brand == "dam") baseSong.no else null,
            noJoy = if (baseSong.brand == "joysound") baseSong.no else null
        )

        // 브랜드별 최적의 매칭 곡 찾기 (제목 포함 및 가수 일치)
        resultsMap.forEach { (brand, list) ->
            val match = list.find { 
                cleanTitle(it.title).equals(cleanedTitle, ignoreCase = true) && 
                it.singer.replace(" ", "").equals(baseSong.singer.replace(" ", ""), ignoreCase = true)
            }
            
            when (brand) {
                "tj" -> if (finalSong.noTj == null) finalSong.noTj = match?.no
                "kumyoung" -> if (finalSong.noKy == null) finalSong.noKy = match?.no
                "dam" -> if (finalSong.noDam == null) finalSong.noDam = match?.no
                "joysound" -> if (finalSong.noJoy == null) finalSong.noJoy = match?.no
            }
        }

        val msg = """
            제목: ${finalSong.title}
            가수: ${finalSong.singer}
            
            [검색된 번호]
            TJ: ${finalSong.noTj ?: "-----"}
            금영: ${finalSong.noKy ?: "-----"}
            DAM: ${finalSong.noDam ?: "-----"}
            JOY: ${finalSong.noJoy ?: "-----"}
            
            이 구성으로 보관함에 저장할까요?
        """.trimIndent()

        AlertDialog.Builder(context)
            .setTitle("보관함 저장 확인")
            .setMessage(msg)
            .setPositiveButton("저장") { _, _ ->
                val success = BookmarkManager.addSongToPlaylist(context, playlistIndex, finalSong)
                if (success) {
                    Toast.makeText(context, "보관함에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "이미 해당 탭에 존재하는 곡입니다.", Toast.LENGTH_SHORT).show()
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
