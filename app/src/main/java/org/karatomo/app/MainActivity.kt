package org.karatomo.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.karatomo.app.ui.NewSongFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, NewSongFragment())
                    .commit()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "초기화 에러: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
