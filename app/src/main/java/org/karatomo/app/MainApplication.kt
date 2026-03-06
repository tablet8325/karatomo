package org.karatomo.app

import android.app.Application
import org.karatomo.app.managers.BookmarkManager

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // [오류 해결] 앱 시작 시 데이터베이스(매니저) 초기화 필수
        BookmarkManager.init(this)
    }
}
