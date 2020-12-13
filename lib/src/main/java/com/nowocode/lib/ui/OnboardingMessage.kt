package com.nowocode.lib.ui

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import androidx.annotation.RequiresApi

class OnboardingMessage : FrameLayout {
    private val backgroundPaint = Paint()
    private val trianglePaint = Paint()
    private val titlePaint = Paint()
    private val textPaint = Paint()
    private var title: String =
        "Too many people are not thinking."
    private var text: String =
        "Using this feature you can do this and that, but unfortunately it is a very rare skill! The people today are not thinking anymore. The problems and scandals of the past are unresolved and will stay unresolved because of the ignorant, uneducated new popularism generation."
    private val scale = context.resources.displayMetrics.density
    private val PADDING_IN_DP = 8 * scale

    // if is Top is true then the dialog triangle is at the bottom, reverse else
    var isTop: Boolean = true

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setUpPaints()
    }

    fun setContent(title: String, text: String) {
        this.title = title
        this.text = text
    }

    private fun setUpPaints() {
        backgroundPaint.color = Color.WHITE
        backgroundPaint.style = Paint.Style.FILL

        titlePaint.color = Color.BLACK
        titlePaint.isAntiAlias = true
        titlePaint.textSize = 14 * scale
        titlePaint.style = Paint.Style.FILL
        titlePaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD);

        textPaint.color = Color.BLACK
        textPaint.isAntiAlias = true
        textPaint.textSize = 12 * scale


        trianglePaint.color = Color.BLACK
        trianglePaint.style = Paint.Style.FILL
        setWillNotDraw(false)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas?) {
        Log.d(this.javaClass.name, "OnboardingMessage - onDraw")
        val dialogWidth = width * 0.85
        val lineWidth = 30 * scale
        canvas?.drawRoundRect(
            PADDING_IN_DP * 2,
            PADDING_IN_DP,
            width.toFloat() - PADDING_IN_DP * 2,
            height.toFloat(),
            15f,
            15f,
            backgroundPaint
        )

        canvas?.let {
            drawContent(it)
        }
    }

    private fun drawContent(canvas: Canvas) {
        // Contains the latest Y position after a widget has been rendered.
        // It is used to render new elements below the latest rendered one
        var lastYPosition = renderText(canvas, title, titlePaint, 0f)

        val fl = lastYPosition + 2 * PADDING_IN_DP
        canvas.drawLine(
            0f + 2 * PADDING_IN_DP,
            fl,
            width - 2 * PADDING_IN_DP,
            fl + 3,
            textPaint
        )

        lastYPosition = renderText(canvas, text, textPaint, lastYPosition + PADDING_IN_DP)
    }

    private fun renderText(canvas: Canvas, text: String, paint: Paint, yOffset: Float): Float {
        var lastYPosition = yOffset
        if (isTextWiderThanScreen(text, paint)) {
            // we need to break the title down into multipe lines
            val textParts = mutableListOf<String>()
            var tempStringHolder = ""
            text.split(" ")
                .map { titleWord ->
                    if (!isTextWiderThanScreen(tempStringHolder, paint))
                        tempStringHolder += "$titleWord "
                    else {
                        // text is too long
                        textParts.add(tempStringHolder)
                        tempStringHolder = ""
                    }
                }
            if (tempStringHolder != "") {
                textParts.add("$tempStringHolder ")
                tempStringHolder = ""
            }
            val textRect = Rect()
            textParts.forEachIndexed { index, textPart ->
                paint.getTextBounds(
                    textPart,
                    0,
                    textPart.length,
                    textRect
                )
                lastYPosition += PADDING_IN_DP + textRect.height()
                if (index == 0)
                    lastYPosition += PADDING_IN_DP

                canvas.drawText(
                    textPart,
                    width / 2 - (paint.measureText(textPart) / 2),
                    lastYPosition,
                    paint
                )
            }
        } else {
            lastYPosition += PADDING_IN_DP * 4
            canvas.drawText(
                this.title,
                width / 2 - paint.measureText(this.title) / 2 - PADDING_IN_DP,
                lastYPosition,
                paint
            )
        }

        return lastYPosition
    }

    private fun isTextWiderThanScreen(text: String, paint: Paint): Boolean {
        val measuredTextWidth = paint.measureText(text)
        return measuredTextWidth > (width - 16 * PADDING_IN_DP)
    }
}