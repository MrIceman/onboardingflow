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
    private var title: String = "Title"
    private var text: String = "blabla"
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
        titlePaint.textSize = 18 * scale
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

    private fun isTextWiderThanScreen(text: String, paint: Paint): Boolean {
        val measuredTextWidth = paint.measureText(text)
        return measuredTextWidth > (width - 2 * PADDING_IN_DP)
    }

    private fun drawContent(canvas: Canvas) {
        if (isTextWiderThanScreen(this.title, titlePaint)) {
            // we need to break the title down into multipe lines
            val textParts = mutableListOf<String>()
            var tempStringHolder = ""
            this.title.split(" ")
                .map { titleWord ->
                    if (!isTextWiderThanScreen(tempStringHolder, titlePaint))
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
            var currentDrawnTextLine = 0
            val textRect = Rect()
            textParts.forEach { textPart ->
                titlePaint.getTextBounds(
                    textPart,
                    0,
                    textPart.length,
                    textRect
                )
                canvas.drawText(
                    textPart,
                    width / 2 - titlePaint.measureText(this.title) / 2 - PADDING_IN_DP,
                    PADDING_IN_DP * 4 + currentDrawnTextLine * (textRect.bottom + (PADDING_IN_DP / currentDrawnTextLine)),
                    titlePaint
                )

                currentDrawnTextLine++
            }
        } else {
            canvas.drawText(
                this.title,
                width / 2 - titlePaint.measureText(this.title) / 2 - PADDING_IN_DP,
                PADDING_IN_DP * 4,
                titlePaint
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}