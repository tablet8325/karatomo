package org.karatomo.app

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.karatomo.app.managers.BookmarkManager
import org.karatomo.app.ui.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // [핵심 1] 앱 시작 시 매니저 초기화 (데이터 로딩)
        BookmarkManager.init(this)

        if (savedInstanceState == null) {
            // 시작 화면을 검색 화면으로 설정
            replaceFragment(SearchFragment())
        }

        // [핵심 2] 메뉴 연결 (LibraryFragment로 이름 통일)
        findViewById<Button>(R.id.btnNavSearch).setOnClickListener { 
            replaceFragment(SearchFragment()) 
        }
        findViewById<Button>(R.id.btnNavNew).setOnClickListener { 
            replaceFragment(NewSongFragment()) 
        }
        findViewById<Button>(R.id.btnNavBookmark).setOnClickListener { 
            // BookmarkFragment 대신 최신 버전인 LibraryFragment를 호출합니다.
            replaceFragment(LibraryFragment()) 
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
