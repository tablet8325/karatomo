package org.karatomo.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.karatomo.app.databinding.ActivityMainBinding

/**
 * [MainActivity]
 * 앱의 메인 컨테이너입니다. 기존의 검색, 신곡, 보관함 화면 전환 기능을 유지하며
 * 앱 시작 시 보관함 데이터를 안전하게 초기화합니다.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. [보관함 데이터 초기화] 기존 기능에 앞서 데이터를 먼저 로드합니다.
        // 이 과정에서 '기본 탭'이 생성되어 보관함 화면이 멈추는 것을 방지합니다.
        BookmarkManager.initialize(this)

        // 2. [뷰 바인딩] 기존 레이아웃 설정을 그대로 유지합니다.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. [네비게이션 설정] 기존의 검색(Search), 신곡(NewSong), 보관함(Bookmark) 전환 기능을 유지합니다.
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // 하단 네비게이션 바와 각 화면(Fragment)을 연결합니다.
        // 이 코드가 있어야 검색 화면이 첫 화면으로 뜨고 메뉴 클릭 시 화면이 전환됩니다.
        navView.setupWithNavController(navController)
    }
}
