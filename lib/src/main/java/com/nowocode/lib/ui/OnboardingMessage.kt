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
    private val padding = 8 * scale // 8dp

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setUpPaints()
    }

    internal fun setUp(title: String, text: String) {
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

        val bottomPadding = padding * 6
        canvas?.drawRoundRect(
            padding * 2,
            padding,
            width.toFloat() - padding * 2,
            height.toFloat() - bottomPadding,
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

        val fl = lastYPosition + 2 * padding
        canvas.drawLine(
            0f + 2 * padding,
            fl,
            width - 2 * padding,
            fl + 3,
            textPaint
        )

        lastYPosition = renderText(canvas, text, textPaint, lastYPosition + padding)
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
                lastYPosition += padding + textRect.height()
                if (index == 0)
                    lastYPosition += padding

                canvas.drawText(
                    textPart,
                    width / 2 - (paint.measureText(textPart) / 2),
                    lastYPosition,
                    paint
                )
            }
        } else {
            lastYPosition += padding * 4
            canvas.drawText(
                this.title,
                width / 2 - paint.measureText(this.title) / 2 - padding,
                lastYPosition,
                paint
            )
        }

        return lastYPosition
    }

    private fun isTextWiderThanScreen(text: String, paint: Paint): Boolean {
        val measuredTextWidth = paint.measureText(text)
        return measuredTextWidth > (width - 16 * padding)
    }
}