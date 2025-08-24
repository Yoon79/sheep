package com.goodafteryoon.sheep

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.AdView

class MainActivity : AppCompatActivity() {
    private var backgroundMusic: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // SharedPreferences에 기본값 무조건 true로 저장 (가장 먼저!)
        val sharedPreferences = getSharedPreferences("SheepToSleepSettings", MODE_PRIVATE)
        if (!sharedPreferences.contains("music_enabled")) {
            sharedPreferences.edit().putBoolean("music_enabled", true).apply()
        }
        if (!sharedPreferences.contains("sound_enabled")) {
            sharedPreferences.edit().putBoolean("sound_enabled", true).apply()
        }
        setContentView(R.layout.activity_main)

        // AdMob 광고 초기화 및 로드
        MobileAds.initialize(this) {}
        val adView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        setupSettingsPopup()
        startBackgroundMusic()
    }

    private fun setupSettingsPopup() {
        val settingsButton = findViewById<ImageButton>(R.id.settingsButton)
        val settingsOverlay = findViewById<FrameLayout>(R.id.settingsOverlay)
        val settingsDialog = findViewById<View>(R.id.settingsDialog)
        val closeButton = findViewById<Button>(R.id.closeButton)
        val musicSwitch = findViewById<Switch>(R.id.musicSwitch)
        val soundSwitch = findViewById<Switch>(R.id.soundSwitch)
        val sheepView = findViewById<View>(R.id.sheepView)

        val sharedPreferences = getSharedPreferences("SheepToSleepSettings", MODE_PRIVATE)

        // 1. SharedPreferences에 값이 없으면 true로 저장 (최초 실행 시)
        if (!sharedPreferences.contains("music_enabled")) {
            sharedPreferences.edit().putBoolean("music_enabled", true).apply()
        }
        if (!sharedPreferences.contains("sound_enabled")) {
            sharedPreferences.edit().putBoolean("sound_enabled", true).apply()
        }

        // 2. 항상 true로 동기화
        musicSwitch.isChecked = true
        soundSwitch.isChecked = true

        // 3. 음악 상태도 항상 ON으로 동기화
        if (backgroundMusic?.isPlaying != true) {
            backgroundMusic?.start()
        }

        // 음악 스위치 리스너 (여기서만 음악 상태를 바꿈)
        musicSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("music_enabled", isChecked).apply()
            if (isChecked) {
                backgroundMusic?.start()
            } else {
                backgroundMusic?.pause()
            }
        }
        // 소리 스위치 리스너
        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("sound_enabled", isChecked).apply()
        }

        // 설정 버튼 클릭 시 팝업 표시 및 SheepView 비활성화
        settingsButton.setOnClickListener {
            settingsOverlay.visibility = View.VISIBLE
            sheepView.isEnabled = false
        }

        // 오버레이 클릭 시 팝업 닫기 및 SheepView 활성화
        settingsOverlay.setOnClickListener {
            settingsOverlay.visibility = View.GONE
            sheepView.isEnabled = true
        }

        // 다이얼로그 내부 클릭 시 이벤트 전파 방지
        settingsDialog.setOnClickListener { }

        // 닫기 버튼 클릭 시 팝업 닫기 및 SheepView 활성화
        closeButton.setOnClickListener {
            settingsOverlay.visibility = View.GONE
            sheepView.isEnabled = true
        }
    }

    private fun startBackgroundMusic() {
        try {
            backgroundMusic = MediaPlayer.create(this, R.raw.background_music)
            backgroundMusic?.isLooping = true
            backgroundMusic?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundMusic?.release()
        backgroundMusic = null
    }
}
