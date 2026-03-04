package org.karatomo.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.karatomo.app.ui.NewSongFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, NewSongFragment())
                    .commit()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "프래그먼트 로딩 실패: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}
