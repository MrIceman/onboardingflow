package com.nowocode.lib.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.nowocode.lib.ui.model.VerticalPosition
import com.nowocode.lib.ui.model.OnboardingAction
import com.nowocode.lib.ui.model.inverse


internal class OnboardingScaffold : FrameLayout {
    private val backgroundPaint = Paint()
    private val featurePaint = Paint()
    private val actions: MutableList<OnboardingAction> = mutableListOf()
    private val actionPosLocHolder = IntArray(2)
    private val scale = context.resources.displayMetrics.density
    private val PADDING = 8 * scale
    private var currentDisplayedAction = 0
    private val clickThreshHold = 1000
    private var onboardingMessage: OnboardingMessage = OnboardingMessage(context)
    private var onBoardingMessageLayoutParams = LayoutParams(0, 0)
    private val opacityAnimator = ValueAnimator()
    private var hasAnimated = false
    private var canvas: Canvas? = null

    /** fade in settings */
    internal var shouldFadeIn = false
    internal var fadeInDuration: Long = 0
    internal var fadeInStartAlpha = 0f
    internal var fadeInStopAlpha = 0.75f

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setUpBackgroundPaint()
        addView(onboardingMessage, onBoardingMessageLayoutParams)
    }

    internal fun initAnimator() {
        opacityAnimator.duration = fadeInDuration
        opacityAnimator.setIntValues(
            (255 * fadeInStartAlpha).toInt(),
            (255 * fadeInStopAlpha).toInt()
        )
        opacityAnimator.addUpdateListener {
            val opacityLevel = it.animatedValue as Int
            println("opacity level: $opacityLevel")
            backgroundPaint.alpha = opacityLevel // opacityLevel.toInt()
            invalidate()
        }
    }

    private fun setUpBackgroundPaint() {
        backgroundPaint.color = Color.BLACK
        backgroundPaint.alpha = (255 * 0.8).toInt()
        backgroundPaint.style = Paint.Style.FILL

        featurePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        featurePaint.isAntiAlias = true
        setWillNotDraw(false)
    }

    fun addOnBoardingAction(action: OnboardingAction) {
        actions.add(action)
    }

    override fun onDraw(canvas: Canvas?) {
        if (actions.isEmpty())
            return

        this.canvas = canvas
        Log.d(this.javaClass.name, "OnboardingScaffold - onDraw")
        val screenBitMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val screenCanvas = Canvas(screenBitMap)
        if (hasAnimated || !shouldFadeIn) {
            screenCanvas.drawRect(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                backgroundPaint
            )
        } else {
            hasAnimated = true
            opacityAnimator.start()
        }

        var dialogTopY = 0f

        actions[currentDisplayedAction].let { element ->
            val viewWidth = element.view.get()?.width?.toFloat()!!
            val viewHeight = element.view.get()?.height?.toFloat()!!
            val v = element.view.get() ?: throw IllegalStateException("No View passed in)")
            v.getLocationOnScreen(actionPosLocHolder)

            if (v.javaClass.superclass == AppCompatTextView::class.java && v is TextView) {

                screenCanvas.drawRoundRect(
                    RectF(
                        actionPosLocHolder[0].toFloat() - PADDING,
                        actionPosLocHolder[1].toFloat() - viewHeight - PADDING,
                        actionPosLocHolder[0].toFloat() + viewWidth + PADDING,
                        actionPosLocHolder[1].toFloat() + (v.minHeight) + PADDING,
                    ),
                    15f,
                    15f,
                    featurePaint
                )
            } else {
                screenCanvas.drawRoundRect(
                    RectF(
                        actionPosLocHolder[0].toFloat() - PADDING,
                        actionPosLocHolder[1].toFloat() - (viewHeight / 2) - PADDING,
                        actionPosLocHolder[0].toFloat() + viewWidth + PADDING,
                        actionPosLocHolder[1].toFloat() + (viewHeight / 2) + PADDING,
                    ),
                    15f,
                    15f,
                    featurePaint
                )
            }

            // Get Dialog position
            val messageHeight = height * 0.3f
            dialogTopY = when (element.verticalPosition) {
                VerticalPosition.TOP -> {
                    actionPosLocHolder[1] - PADDING - messageHeight - viewHeight
                }
                VerticalPosition.BOTTOM -> {
                    actionPosLocHolder[1] + viewHeight + PADDING

                }
            }

            onboardingMessage.setUp(
                element.title,
                element.text,
                actions[currentDisplayedAction].verticalPosition.inverse(),
                actionPosLocHolder[0].toFloat()
            )
            onboardingMessage.layoutParams = onBoardingMessageLayoutParams
        }

        canvas?.drawBitmap(screenBitMap, 0f, 0f, Paint())
        /** TODO This should not happen within onDraw as this causes a recursion */
        onboardingMessage.translationY = dialogTopY
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        onBoardingMessageLayoutParams = LayoutParams(
            w,
            (h * 0.3).toInt()
        )

        onboardingMessage.invalidate()

        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            val duration = it.eventTime - it.downTime
            if (duration < clickThreshHold && event.action == MotionEvent.ACTION_UP) {
                if (currentDisplayedAction < actions.size - 1)
                    currentDisplayedAction++
                else
                    currentDisplayedAction = 0
                invalidate()
            }
        }

        return true
    }

    private fun Int.toDp(): Int = (this * scale).toInt()
}