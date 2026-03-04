package org.karatomo.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.karatomo.app.ui.NewSongFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, NewSongFragment())
            .commit()
    }
}
