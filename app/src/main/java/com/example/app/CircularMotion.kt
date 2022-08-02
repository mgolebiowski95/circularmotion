package com.example.app

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

class CircularMotion @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val bounds = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    var lastTime = System.currentTimeMillis()

    private val particles = mutableListOf<Particle>()

    private val c = Canvas()
    private val cPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var bitmap: Bitmap? = null

    init {
        for (index in 0 until 50) {
            val particle = Particle()
            particle.distance = (Random.nextInt(250) + 150).toDouble()
            particle.radius = (Random.nextInt(4) + 8f)
            particle.color = Color.parseColor(arrayOf("#00bdff", "#4d39ce", "#088eff").random())
            particles.add(particle)
        }

        cPaint.style = Paint.Style.STROKE
        cPaint.strokeWidth = 8f
    }

    private fun update(dt: Long) {
        particles.forEach { it.update(dt) }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bounds.set(0f, 0f, w.toFloat(), h.toFloat())

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        c.setBitmap(bitmap)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val now = System.currentTimeMillis()
        update(now - lastTime)
        lastTime = now
        render(canvas)
        bitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, cPaint)
            invalidate()
        }
    }

    private fun render(c: Canvas) {
        particles.forEach { particle ->
            paint.color = particle.color
            paint.strokeWidth = particle.radius
            paint.style = Paint.Style.STROKE

            c.drawLine(
                    particle.lastX.toFloat(), particle.lastY.toFloat(),
                    particle.x.toFloat(), particle.y.toFloat(),
                    paint)
        }
        c.drawColor(0x22ffffff)
    }

    companion object {
        var mouse = PointF()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mouse.x = event.x
        mouse.y = event.y
        return true
    }

    class Particle() {
        var x = 0.0
        var y = 0.0
        var radius = 0f
        var distance = 200.0
        var color = 0x000000
        private var radians = Math.random() * Math.PI * 2
        private var velocity = 0.003

        var lastX = 0.0
        var lastY = 0.0

        var lastMouse = PointF(x.toFloat(), y.toFloat())

        fun update(dt: Long) {
            lastX = x;
            lastY = y;

            lastMouse.x += (mouse.x - lastMouse.x) * 0.05f
            lastMouse.y += (mouse.y - lastMouse.y) * 0.05f

            radians += velocity * dt
            x = lastMouse.x + Math.cos(this.radians) * distance
            y = lastMouse.y + Math.sin(this.radians) * distance
        }
    }
}