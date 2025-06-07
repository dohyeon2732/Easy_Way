// 파일 이름: MyApplication.kt
package com.example.mobile_project_3 // 본인의 패키지 이름과 동일해야 합니다.

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 가장 먼저 Firebase 앱을 초기화합니다.
        FirebaseApp.initializeApp(this)

        // 그 다음, Firebase App Check을 초기화합니다.
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
    }
}