package com.goodafteryoon.sheep

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import kotlin.math.cos
import kotlin.math.sin

data class Sheep(
    var x: Float,
    var y: Float,
    var direction: Float, // 라디안 단위
    var speed: Float = 3f,
    val drawable: Drawable
) {
    
    fun update(screenWidth: Int, screenHeight: Int) {
        // 현재 방향으로 이동
        x += cos(direction) * speed
        y += sin(direction) * speed
        
        // 화면 경계에 부딪혔을 때 방향 변경
        val sheepSize = 48f // 양의 크기
        
        if (x <= sheepSize / 2 || x >= screenWidth - sheepSize / 2) {
            // 좌우 경계에 부딪힘
            direction = (Math.PI - direction).toFloat()
            x = x.coerceIn(sheepSize / 2, screenWidth - sheepSize / 2)
        }
        
        if (y <= sheepSize / 2 || y >= screenHeight - sheepSize / 2) {
            // 상하 경계에 부딪힘
            direction = (-direction).toFloat()
            y = y.coerceIn(sheepSize / 2, screenHeight - sheepSize / 2)
        }
    }
    
    fun draw(canvas: Canvas) {
        drawable.setBounds(
            (x - 24).toInt(),
            (y - 24).toInt(),
            (x + 24).toInt(),
            (y + 24).toInt()
        )
        drawable.draw(canvas)
    }
}
