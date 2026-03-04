package org.karatomo.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 레이아웃 파일을 부르지 않고, 코드로 직접 글자를 띄워봅니다.
        // 이렇게 해서 앱이 켜진다면 '레이아웃 XML'이나 '프래그먼트'가 범인입니다.
        val tv = TextView(this)
        tv.text = "메인 화면 접속 성공! 프래그먼트 로딩 전입니다."
        setContentView(tv)
    }
}
