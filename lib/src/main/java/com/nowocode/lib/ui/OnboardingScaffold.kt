package com.nowocode.lib.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.nowocode.lib.ui.model.OnboardingAction
import com.nowocode.lib.ui.model.VerticalPosition
import com.nowocode.lib.ui.model.inverse


internal class OnboardingScaffold : FrameLayout {
    private val featurePaint = Paint()
    private val arrowIndicatorPaint = Paint()
    private val pathPaint = Paint()

    private val actions: MutableList<OnboardingAction> = mutableListOf()
    private val featureViewCoordinates = IntArray(2)
    private val scale = context.resources.displayMetrics.density
    private val PADDING: Float = 8 * scale
    private var currentDisplayedAction = 0
    private val clickThreshHold = 1000
    private var onboardingMessage: OnboardingMessage = OnboardingMessage(context)
    private var onBoardingMessageLayoutParams = LayoutParams(0, 0)
    private val opacityAnimator = ValueAnimator()
    private var hasAnimated = false
    private var canvas: Canvas? = null
    private var currentOpacityBackgroundLevel = 0

    /** fade in settings */
    internal var shouldFadeIn = false
    internal var fadeInDuration: Long = 0
    internal var fadeInStartAlpha = 0f
    internal var fadeInStopAlpha = 0.75f

    internal var onboardingDoneCallback: (() -> Unit)? = null

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
            currentOpacityBackgroundLevel = opacityLevel
            invalidate()
        }
    }

    private fun setUpBackgroundPaint() {
        featurePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        featurePaint.isAntiAlias = true

        arrowIndicatorPaint.style = Paint.Style.FILL
        arrowIndicatorPaint.strokeWidth = 3f
        arrowIndicatorPaint.isAntiAlias = true
        arrowIndicatorPaint.color = Color.RED

        pathPaint.color = Color.WHITE
        pathPaint.isAntiAlias = true
        pathPaint.apply {
            strokeWidth = 5f
            style = Paint.Style.FILL
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }

        setWillNotDraw(false)
    }

    fun addOnBoardingAction(action: OnboardingAction) {
        actions.add(action)
    }

    /**
     * Right now there are some allocations happening in onDraw.
     * This should not be a noticable performance issue but still is not nice,
     * so I'll leave cleaning it up as a TODO here and suppress the lint warning until then
     */
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        if (actions.isEmpty())
            return

        this.canvas = canvas
        Log.d(this.javaClass.name, "OnboardingScaffold - onDraw")
        val screenBitMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val screenCanvas = Canvas(screenBitMap)
        if (hasAnimated || !shouldFadeIn) {
            screenCanvas.drawARGB(
                currentOpacityBackgroundLevel,
                0,
                0,
                0
            )
        } else {
            hasAnimated = true
            opacityAnimator.start()
        }

        var dialogTopY: Float
        actions[currentDisplayedAction].let { element ->
            val viewWidth = element.view.width.toFloat()
            val viewHeight = element.view.height.toFloat()
            val v = element.view
            v.getLocationOnScreen(featureViewCoordinates)
            val featureViewRect =
                if (v.javaClass.superclass == AppCompatTextView::class.java && v is TextView) {
                    RectF(
                        featureViewCoordinates[0].toFloat() - PADDING,
                        featureViewCoordinates[1].toFloat() - viewHeight - PADDING,
                        featureViewCoordinates[0].toFloat() + viewWidth + PADDING,
                        featureViewCoordinates[1].toFloat() + (v.minHeight) + PADDING,
                    )
                } else {
                    RectF(
                        featureViewCoordinates[0].toFloat() - PADDING,
                        featureViewCoordinates[1].toFloat() - (viewHeight / 2) - PADDING,
                        featureViewCoordinates[0].toFloat() + viewWidth + PADDING,
                        featureViewCoordinates[1].toFloat() + (viewHeight / 2) + PADDING,
                    )
                }
            screenCanvas.drawRoundRect(
                featureViewRect,
                15f,
                15f,
                featurePaint
            )

            // Get Dialog position
            val messageHeight = height * 0.3f
            dialogTopY = element.getTopOrBottomValue(
                topValue = featureViewCoordinates[1] - PADDING - messageHeight - viewHeight / 2,
                botValue = featureViewCoordinates[1] + 3 * viewHeight / 2 + PADDING
            )

            onboardingMessage.setUp(
                element.title,
                element.text,
                actions[currentDisplayedAction].verticalPosition.inverse(),
                featureViewCoordinates[0].toFloat()
            )
            onboardingMessage.layoutParams = onBoardingMessageLayoutParams

            val p = Path()
            val arcRect = RectF(
                featureViewCoordinates[0] + viewWidth / 2 - PADDING,
                element.getTopOrBottomValue(
                    dialogTopY
                            + (messageHeight / 2),
                    dialogTopY - PADDING
                ),
                featureViewCoordinates[0] + viewWidth / 2 + PADDING,
                element.getTopOrBottomValue(
                    dialogTopY + (messageHeight / 2) + 8 * PADDING,
                    dialogTopY + messageHeight / 2
                )
            )
            p.addArc(
                arcRect,
                340f,
                -180f
            )
            p.addArc(
                arcRect,
                340f,
                +180f
            )
            screenCanvas.drawPath(p, pathPaint)
        }

        onboardingMessage.translationY = dialogTopY

        canvas?.drawBitmap(screenBitMap, 0f, 0f, Paint())
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
                if (currentDisplayedAction < actions.size - 1) {
                    currentDisplayedAction++
                    invalidate()
                } else {
                    // We displayed our features, we can now safely delete the Scaffold
                    this.actions.clear()
                    (parent as ViewGroup)
                        .removeView(this)
                    this.onboardingDoneCallback?.invoke()
                    this.onboardingDoneCallback = null
                }
            }
        }

        return true
    }

    private fun <T> OnboardingAction.getTopOrBottomValue(topValue: T, botValue: T): T =
        if (this.verticalPosition == VerticalPosition.TOP) topValue else botValue
}