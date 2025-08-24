package com.goodafteryoon.sheep

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.widget.ImageView
import kotlin.math.cos
import kotlin.math.sin

data class Sheep(
    var x: Float,
    var y: Float,
    var direction: Float, // 라디안 단위
    var speed: Float = 3f,
    val imageView: ImageView
) {
    
    fun update(screenWidth: Int, screenHeight: Int) {
        // 현재 방향으로 이동
        x += cos(direction) * speed
        y += sin(direction) * speed
        
        // 화면 경계에 부딪혔을 때 방향 변경
        val sheepSize = 192f // 양의 크기 (2배로 증가)
        
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
        
        // ImageView 위치 업데이트
        imageView.x = x - sheepSize / 2
        imageView.y = y - sheepSize / 2
        
        // 진행 방향에 따라 회전 (라디안을 도로 변환하고 180도 더해서 머리가 앞방향을 향하도록)
        val rotationDegrees = Math.toDegrees(direction.toDouble()).toFloat() + 180f
        imageView.rotation = rotationDegrees
    }
}
