package org.karatomo.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.karatomo.app.ui.NewSongFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // 레이아웃 설치부터 감쌉니다.
            setContentView(R.layout.activity_main)
    
            if (savedInstanceState == null) {
                val fragment = NewSongFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commitAllowingStateLoss() // 더 안전한 커밋 방식
            }
        } catch (e: Throwable) { // Exception보다 넓은 범위인 Throwable 사용
            // 화면이 안 나와도 토스트는 띄우려고 시도합니다.
            Toast.makeText(this, "Fatal Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
