package org.karatomo.app.ui

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.karatomo.app.databinding.FragmentBookmarkBinding
import org.karatomo.app.manager.BookmarkManager

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = 
        FragmentBookmarkBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PlaylistAdapter(BookmarkManager.playlists)
        binding.rvPlaylists.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlaylists.adapter = adapter

        binding.btnAddPlaylist.setOnClickListener {
            val editText = EditText(requireContext())
            AlertDialog.Builder(requireContext())
                .setTitle("플레이리스트 이름")
                .setView(editText)
                .setPositiveButton("추가") { _, _ ->
                    val name = editText.text.toString()
                    if (BookmarkManager.createPlaylist(name)) {
                        adapter.notifyDataSetChanged()
                        Toast.makeText(requireContext(), "$name 생성 완료", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "이미 존재하는 이름", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("취소", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
