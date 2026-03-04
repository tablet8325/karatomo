package org.karatomo.app

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.karatomo.app.ui.BookmarkFragment
import org.karatomo.app.ui.NewSongFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NewSongFragment())
                .commit()
        }

        findViewById<Button>(R.id.btnNavNew).setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NewSongFragment())
                .commit()
        }

        findViewById<Button>(R.id.btnNavBookmark).setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BookmarkFragment())
                .commit()
        }
    }
}
