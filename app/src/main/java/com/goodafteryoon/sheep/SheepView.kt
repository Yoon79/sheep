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

    init {
        // 터치 이벤트 활성화
        isClickable = true
        isFocusable = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 터치한 위치에 양 생성
                createSheep(event.x, event.y)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun createSheep(x: Float, y: Float) {
        // 소리 재생
        playSheepSound()
        
        // ImageView 생성 (크기 2배로 증가)
        val imageView = ImageView(context).apply {
            layoutParams = LayoutParams(192, 192) // 96 -> 192로 2배 증가
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        
        // GIF 로드 (assets 폴더에서 로드)
        Glide.with(context)
            .load("file:///android_asset/sheep.gif")
            .into(imageView)
        
        // 랜덤한 방향 설정 (0 ~ 2π)
        val randomDirection = Random.nextFloat() * 2 * PI.toFloat()
        
        val sheep = Sheep(
            x = x,
            y = y,
            direction = randomDirection,
            imageView = imageView
        )
        
        sheepList.add(sheep)
        addView(imageView)
        
        // 애니메이션 시작
        if (!isAnimating) {
            startAnimation()
        }
    }
    
    private fun playSheepSound() {
        try {
            // 기존 MediaPlayer 정리
            mediaPlayer?.release()
            
            // 새로운 MediaPlayer 생성
            mediaPlayer = MediaPlayer.create(context, R.raw.sheep_sound)
            mediaPlayer?.setOnCompletionListener { mp ->
                mp.release()
            }
            mediaPlayer?.start()
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
