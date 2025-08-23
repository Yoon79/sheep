package com.goodafteryoon.sheep

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.PI
import kotlin.random.Random

class SheepView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val sheepList = mutableListOf<Sheep>()
    private val sheepDrawable = context.getDrawable(R.drawable.sheep)
    private var isAnimating = false

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
        sheepDrawable?.let { drawable ->
            // 랜덤한 방향 설정 (0 ~ 2π)
            val randomDirection = Random.nextFloat() * 2 * PI.toFloat()
            
            val sheep = Sheep(
                x = x,
                y = y,
                direction = randomDirection,
                drawable = drawable
            )
            
            sheepList.add(sheep)
            
            // 애니메이션 시작
            if (!isAnimating) {
                startAnimation()
            }
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
        
        // 화면 다시 그리기
        invalidate()
        
        // 다음 프레임 예약 (60 FPS)
        postDelayed({ startAnimationLoop() }, 16)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // 모든 양 그리기
        sheepList.forEach { sheep ->
            sheep.draw(canvas)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isAnimating = false
    }
}
