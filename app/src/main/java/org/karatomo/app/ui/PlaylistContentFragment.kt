package org.karatomo.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.karatomo.app.BookmarkManager
import org.karatomo.app.databinding.FragmentPlaylistContentBinding
import org.karatomo.app.network.SongAdapter

/**
 * [PlaylistContentFragment]
 * 보관함의 각 탭(페이지) 내부에서 실제 곡 목록을 보여주는 프래그먼트입니다.
 */
class PlaylistContentFragment : Fragment() {
    private var _binding: FragmentPlaylistContentBinding? = null
    private val binding get() = _binding!!
    private var playlistIndex: Int = 0
    private lateinit var adapter: SongAdapter

    companion object {
        /**
         * 특정 탭의 인덱스를 전달받아 프래그먼트 인스턴스를 생성합니다.
         */
        fun newInstance(index: Int) = PlaylistContentFragment().apply {
            arguments = Bundle().apply { putInt("index", index) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlistIndex = arguments?.getInt("index") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 해당 탭의 곡 목록 가져오기
        val playlist = BookmarkManager.playlists[playlistIndex]
        adapter = SongAdapter(requireContext(), playlist.songs)
        
        binding.rvPlaylist.layoutManager = LinearLayoutManager(context)
        binding.rvPlaylist.adapter = adapter
    }

    /**
     * 브랜드 필터(TJ, 금영 등)가 변경되었을 때 화면을 갱신합니다.
     */
    fun updateView(brand: String) {
        // 어댑터에게 데이터 변경(번호 표시 기준 변경 등)을 알립니다.
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
