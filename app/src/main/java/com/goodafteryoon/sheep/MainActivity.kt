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

class MainActivity : AppCompatActivity() {
    private var backgroundMusic: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        // 저장된 설정 불러오기
        musicSwitch.isChecked = sharedPreferences.getBoolean("music_enabled", true)
        soundSwitch.isChecked = sharedPreferences.getBoolean("sound_enabled", true)

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

        // 음악 스위치 리스너
        musicSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("music_enabled", isChecked).apply()
            if (isChecked && backgroundMusic?.isPlaying == false) {
                backgroundMusic?.start()
            } else if (!isChecked && backgroundMusic?.isPlaying == true) {
                backgroundMusic?.pause()
            }
        }

        // 소리 스위치 리스너
        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("sound_enabled", isChecked).apply()
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
