package org.karatomo.app.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import org.karatomo.app.BookmarkManager
import org.karatomo.app.databinding.FragmentBookmarkBinding

/**
 * [BookmarkFragment]
 * 보관함의 메인 화면으로 탭 관리, 뷰페이저 제어, 하단 정렬 및 편집 바를 관리합니다.
 */
class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
        setupTabSettings()
        setupBottomControls()
    }

    /**
     * 탭 레이아웃과 뷰페이저2 연동
     */
    private fun setupViewPager() {
        val adapter = PlaylistPagerAdapter(this)
        binding.bookmarkViewPager.adapter = adapter

        // TabLayout과 ViewPager2를 연결 (탭 이름 표시)
        TabLayoutMediator(binding.playlistTabLayout, binding.bookmarkViewPager) { tab, position ->
            tab.text = BookmarkManager.playlists[position].name
        }.attach()
    }

    /**
     * 톱니바퀴 버튼 클릭 시 탭 관리 다이얼로그 표시
     */
    private fun setupTabSettings() {
        binding.btnTabSettings.setOnClickListener {
            val options = arrayOf("탭 추가", "탭 이름 변경", "탭 삭제", "탭 순서 변경")
            AlertDialog.Builder(requireContext())
                .setTitle("보관함 탭 관리")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> showAddTabDialog()
                        1 -> showRenameTabDialog()
                        2 -> showDeleteTabDialog()
                        3 -> showMoveTabDialog()
                    }
                }
                .show()
        }
    }

    private fun showAddTabDialog() {
        val input = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("새 탭 이름 입력")
            .setView(input)
            .setPositiveButton("추가") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    if (BookmarkManager.addPlaylist(requireContext(), name)) {
                        refreshAll()
                    } else {
                        Toast.makeText(context, "이미 존재하는 이름입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun showRenameTabDialog() {
        val currentIdx = binding.bookmarkViewPager.currentItem
        if (BookmarkManager.playlists[currentIdx].isDefault) {
            Toast.makeText(context, "기본 탭은 이름을 바꿀 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val input = EditText(requireContext())
        input.setText(BookmarkManager.playlists[currentIdx].name)
        AlertDialog.Builder(requireContext())
            .setTitle("탭 이름 수정")
            .setView(input)
            .setPositiveButton("수정") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty()) {
                    BookmarkManager.renamePlaylist(requireContext(), currentIdx, newName)
                    refreshAll()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun showDeleteTabDialog() {
        val currentIdx = binding.bookmarkViewPager.currentItem
        if (BookmarkManager.playlists[currentIdx].isDefault) {
            Toast.makeText(context, "기본 탭은 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("탭 삭제")
            .setMessage("'${BookmarkManager.playlists[currentIdx].name}' 탭을 삭제할까요?\n안에 담긴 곡들도 모두 삭제됩니다.")
            .setPositiveButton("삭제") { _, _ ->
                BookmarkManager.deletePlaylist(requireContext(), currentIdx)
                refreshAll()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun showMoveTabDialog() {
        val names = BookmarkManager.playlists.map { it.name }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle("순서를 바꿀 탭 선택")
            .setItems(names) { _, fromIdx ->
                val toOptions = names.indices.map { "${it + 1}번 위치로 이동" }.toTypedArray()
                AlertDialog.Builder(requireContext())
                    .setTitle("이동할 위치 선택")
                    .setItems(toOptions) { _, toIdx ->
                        BookmarkManager.movePlaylist(requireContext(), fromIdx, toIdx)
                        refreshAll()
                    }
                    .show()
            }
            .show()
    }

    /**
     * 하단 제어 바 설정 (정렬 및 순서 변경 모드)
     */
    private fun setupBottomControls() {
        // 정렬 스피너 리스너
        binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // TODO: 5단계에서 각 탭의 Fragment에 정렬 명령 전달 로직 구현
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 순서 바꾸기 버튼
        binding.btnEditOrder.setOnClickListener {
            // TODO: 5단계에서 드래그 앤 드롭 편집 모드 토글 로직 구현
            Toast.makeText(context, "편집 모드 준비 중", Toast.LENGTH_SHORT).show()
        }
    }

    private fun refreshAll() {
        binding.bookmarkViewPager.adapter?.notifyDataSetChanged()
        // TabLayout 갱신을 위해 Mediator 재연결이 필요할 수 있음
        setupViewPager()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 뷰페이저 어댑터: 각 탭(Playlist)마다 별도의 곡 리스트 Fragment를 생성합니다.
     */
    inner class PlaylistPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = BookmarkManager.playlists.size
        override fun createFragment(position: Int): Fragment {
            // 각 탭에 해당하는 곡 목록 화면을 생성하여 반환 (5단계에서 구현)
            return PlaylistContentFragment.newInstance(position)
        }
    }
}
