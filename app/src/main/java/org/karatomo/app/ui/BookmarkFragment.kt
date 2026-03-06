package org.karatomo.app.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import org.karatomo.app.BookmarkManager
import org.karatomo.app.R
import org.karatomo.app.databinding.FragmentBookmarkBinding

/**
 * [BookmarkFragment]
 * 보관함의 메인 화면입니다. 탭 관리(설정), 브랜드 필터링, 정렬 기능을 제어합니다.
 */
class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    // 현재 선택된 브랜드 필터 (기본값: TJ)
    private var currentBrandFilter = "TJ"

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
        setupBrandFilter()
        setupBottomControls()
    }

    /**
     * 1. 탭 레이아웃 및 뷰페이저 설정
     */
    private fun setupViewPager() {
        val adapter = PlaylistPagerAdapter(this)
        binding.bookmarkViewPager.adapter = adapter

        // TabLayout과 ViewPager2 연결
        TabLayoutMediator(binding.playlistTabLayout, binding.bookmarkViewPager) { tab, position ->
            tab.text = BookmarkManager.playlists[position].name
        }.attach()
    }

    /**
     * 2. 브랜드 선택 필터 설정 (전체, TJ, 금영, Joysound, DAM)
     */
    private fun setupBrandFilter() {
        binding.rgBrandFilter.setOnCheckedChangeListener { _, checkedId ->
            currentBrandFilter = when (checkedId) {
                R.id.rbAll -> "전체"
                R.id.rbKy -> "KY"
                R.id.rbJoy -> "JOY"
                R.id.rbDam -> "DAM"
                else -> "TJ"
            }
            // 현재 활성화된 탭 페이지 갱신
            refreshCurrentPage()
        }
    }

    /**
     * 3. 하단 제어 바 설정 (정렬 및 순서 변경)
     */
    private fun setupBottomControls() {
        // 정렬 스피너
        binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sortCurrentPlaylist(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 순서 바꾸기 버튼
        binding.btnEditOrder.setOnClickListener {
            Toast.makeText(context, "순서 바꾸기 모드는 다음 업데이트에서 지원될 예정입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 현재 선택된 탭의 리스트를 정렬
     */
    private fun sortCurrentPlaylist(sortType: Int) {
        val currentIdx = binding.bookmarkViewPager.currentItem
        if (currentIdx !in BookmarkManager.playlists.indices) return

        val songs = BookmarkManager.playlists[currentIdx].songs
        when (sortType) {
            0 -> songs.sortBy { it.title }          // 제목 오름차순
            1 -> songs.sortByDescending { it.title } // 제목 내림차순
            2 -> songs.sortBy { it.singer }         // 가수 오름차순
            3 -> songs.sortByDescending { it.singer } // 가수 내림차순
            4 -> songs.sortByDescending { it.addedDate } // 추가 일자순
            5 -> songs.sortBy { it.noTj ?: "" }     // 곡 번호(TJ)순
        }
        BookmarkManager.updateSongList(requireContext())
        refreshCurrentPage()
    }

    /**
     * 톱니바퀴 버튼 클릭 시 탭 관리 다이얼로그
     */
    private fun setupTabSettings() {
        binding.btnTabSettings.setOnClickListener {
            val options = arrayOf("탭 추가", "탭 이름 변경", "탭 삭제")
            AlertDialog.Builder(requireContext())
                .setTitle("보관함 탭 관리")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> showAddTabDialog()
                        1 -> showRenameTabDialog()
                        2 -> showDeleteTabDialog()
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
                if (name.isNotEmpty() && BookmarkManager.addPlaylist(requireContext(), name)) {
                    setupViewPager() // 탭 갱신
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun showRenameTabDialog() {
        val currentIdx = binding.bookmarkViewPager.currentItem
        if (BookmarkManager.playlists[currentIdx].isDefault) {
            Toast.makeText(context, "기본 탭은 수정할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val input = EditText(requireContext()).apply { setText(BookmarkManager.playlists[currentIdx].name) }
        AlertDialog.Builder(requireContext())
            .setTitle("탭 이름 수정")
            .setView(input)
            .setPositiveButton("수정") { _, _ ->
                BookmarkManager.renamePlaylist(requireContext(), currentIdx, input.text.toString())
                setupViewPager()
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
            .setMessage("정말 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                BookmarkManager.deletePlaylist(requireContext(), currentIdx)
                setupViewPager()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun refreshCurrentPage() {
        // ViewPager2 내부의 현재 Fragment를 찾아 뷰 갱신 명령 전달
        val currentFragment = childFragmentManager.findFragmentByTag("f${binding.bookmarkViewPager.currentItem}") as? PlaylistContentFragment
        currentFragment?.updateView(currentBrandFilter)
    }

    inner class PlaylistPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = BookmarkManager.playlists.size
        override fun createFragment(position: Int): Fragment = PlaylistContentFragment.newInstance(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
