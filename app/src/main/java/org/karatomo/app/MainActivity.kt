package org.karatomo.app

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.karatomo.app.ui.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            replaceFragment(SearchFragment()) // 시작은 검색화면
        }

        findViewById<Button>(R.id.btnNavSearch).setOnClickListener { replaceFragment(SearchFragment()) }
        findViewById<Button>(R.id.btnNavNew).setOnClickListener { replaceFragment(NewSongFragment()) }
        findViewById<Button>(R.id.btnNavBookmark).setOnClickListener { replaceFragment(BookmarkFragment()) }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
