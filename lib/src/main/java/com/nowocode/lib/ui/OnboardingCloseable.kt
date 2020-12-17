package com.nowocode.lib.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class OnboardingCloseable : View {
    private val clickThreshHold = 1000
    internal var onClose: (() -> Unit)? = null
    private val whiteStrokePaint = Paint()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        whiteStrokePaint.apply {
            color = Color.WHITE
            isAntiAlias = true
            strokeWidth = 4f
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawCircle(
            width / 2f,
            height / 2f,
            height.toFloat() / 2.3f,
            whiteStrokePaint
        )

        val closeableInnerOffset = 8

        canvas?.drawLine(
            width / 2f - closeableInnerOffset,
            height / 2f - closeableInnerOffset,
            width / 2f + closeableInnerOffset,
            height / 2f + closeableInnerOffset,
            whiteStrokePaint
        )

        canvas?.drawLine(
            width / 2f + closeableInnerOffset,
            height / 2f - closeableInnerOffset,
            width / 2f - closeableInnerOffset,
            height / 2f + closeableInnerOffset,
            whiteStrokePaint
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            val duration = it.eventTime - it.downTime
            if (duration < clickThreshHold && event.action == MotionEvent.ACTION_UP) {
                this.onClose?.invoke()
            }
        }

        return true
    }
}