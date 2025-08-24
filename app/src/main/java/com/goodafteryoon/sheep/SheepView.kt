package com.goodafteryoon.sheep

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlin.math.PI
import kotlin.random.Random

class SheepView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val sheepList = mutableListOf<Sheep>()
    private var isAnimating = false
    private var mediaPlayer: MediaPlayer? = null
    private var sheepCount = 0 // 양의 개수를 추적

    init {
        // SheepView가 생성될 때도 무조건 true로 저장 (이중 안전장치)
        val sharedPreferences = context.applicationContext.getSharedPreferences("SheepToSleepSettings", Context.MODE_PRIVATE)
        if (!sharedPreferences.contains("sound_enabled")) {
            sharedPreferences.edit().putBoolean("sound_enabled", true).apply()
        }
        isClickable = true
        isFocusable = true
    }


    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 설정 버튼 영역의 터치는 무시 (화면 상단 15% 우측 영역)
                val buttonAreaTop = height * 0.15f
                if (event.x > width - 80 && event.y < buttonAreaTop) {
                    return false
                }
                
                // 터치한 위치에 양 생성
                createSheep(event.x, event.y)
                return true
            }
        }
        return super.onTouchEvent(event)
    }
    
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (!isEnabled) return false
        // 설정 버튼 영역은 터치를 가로채지 않음
        ev?.let { event ->
            val buttonAreaTop = height * 0.15f
            if (event.x > width - 80 && event.y < buttonAreaTop) {
                return false
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    private fun createSheep(x: Float, y: Float) {
        // 양 개수 증가
        sheepCount++
        
        // 소리 재생
        playSheepSound()
        
        // ImageView 생성 (크기 설정)
        val imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        
        // 100단위마다 특별한 양 표시
        val isSpecialSheep = sheepCount % 100 == 0
        
        // 크기 설정 (특별한 양은 5배 크게)
        val size = if (isSpecialSheep) {
            960 // 192 * 5 = 960
        } else {
            192 // 일반 양은 2배 크기
        }
        
        imageView.layoutParams = LayoutParams(size, size)
        
        // GIF 로드 (100단위면 특별한 양, 아니면 일반 양)
        val gifPath = if (isSpecialSheep) {
            "file:///android_asset/sheep100th.gif"
        } else {
            "file:///android_asset/sheep.gif"
        }
        
        Glide.with(context)
            .load(gifPath)
            .into(imageView)
        
        // 랜덤한 방향 설정 (0 ~ 2π)
        val randomDirection = Random.nextFloat() * 2 * PI.toFloat()
        
        val sheep = Sheep(
            x = x,
            y = y,
            direction = randomDirection,
            imageView = imageView,
            isSpecial = isSpecialSheep
        )
        
        sheepList.add(sheep)
        addView(imageView)
        
        // 애니메이션 시작
        if (!isAnimating) {
            startAnimation()
        }
    }
    
    private fun playSheepSound() {
        // 설정에서 소리 활성화 여부 확인 (기본값 true)
        val sharedPreferences = context.applicationContext.getSharedPreferences("SheepToSleepSettings", Context.MODE_PRIVATE)
        val soundEnabled = sharedPreferences.getBoolean("sound_enabled", true)
        if (!soundEnabled) return

        try {
            val mp = MediaPlayer.create(context, R.raw.sheep_sound)
            mp?.setVolume(1.0f, 1.0f)
            mp?.setOnCompletionListener { it.release() }
            mp?.setOnErrorListener { _, what, extra ->
                android.util.Log.e("SheepView", "MediaPlayer error: what=$what, extra=$extra")
                true
            }
            mp?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startAnimation() {
        isAnimating = true
        startAnimationLoop()
    }

    private fun startAnimationLoop() {
        if (!isAnimating) return
        
        // 모든 양 업데이트
        sheepList.forEach { sheep ->
            sheep.update(width, height)
        }
        
        // 다음 프레임 예약 (60 FPS)
        postDelayed({ startAnimationLoop() }, 16)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isAnimating = false
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) {
                mp.stop()
            }
            mp.release()
        }
        mediaPlayer = null
    }
}
