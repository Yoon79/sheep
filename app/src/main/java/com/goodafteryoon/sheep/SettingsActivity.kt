package com.goodafteryoon.sheep

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var sharedPreferences: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        sharedPreferences = getSharedPreferences("SheepToSleepSettings", Context.MODE_PRIVATE)
        
        val musicSwitch = findViewById<Switch>(R.id.musicSwitch)
        val soundSwitch = findViewById<Switch>(R.id.soundSwitch)
        
        // 저장된 설정 불러오기
        musicSwitch.isChecked = sharedPreferences.getBoolean("music_enabled", true)
        soundSwitch.isChecked = sharedPreferences.getBoolean("sound_enabled", true)
        
        // 음악 스위치 리스너
        musicSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("music_enabled", isChecked).apply()
        }
        
        // 소리 스위치 리스너
        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("sound_enabled", isChecked).apply()
        }
    }
}
