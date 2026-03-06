package org.karatomo.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.karatomo.app.BookmarkManager
import org.karatomo.app.databinding.FragmentPlaylistContentBinding
import org.karatomo.app.network.SongAdapter

class PlaylistContentFragment : Fragment() {
    private var _binding: FragmentPlaylistContentBinding? = null
    private val binding get() = _binding!!
    private var playlistIndex: Int = 0

    companion object {
        fun newInstance(index: Int) = PlaylistContentFragment().apply {
            arguments = Bundle().apply { putInt("index", index) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlistIndex = arguments?.getInt("index") ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlaylistContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val playlist = BookmarkManager.playlists[playlistIndex]
        val adapter = SongAdapter(requireContext(), playlist.songs)
        
        binding.rvPlaylist.layoutManager = LinearLayoutManager(context)
        binding.rvPlaylist.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
