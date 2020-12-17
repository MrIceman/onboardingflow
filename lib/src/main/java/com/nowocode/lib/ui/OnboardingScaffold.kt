package com.nowocode.lib.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.nowocode.lib.ui.model.OnboardingAction
import com.nowocode.lib.ui.model.VerticalPosition


internal class OnboardingScaffold : FrameLayout {
    private val featurePaint = Paint()
    private val arrowIndicatorPaint = Paint()
    private val pathPaint = Paint()
    private val actionIndexCirclePaint = Paint()
    private val currentActionIndexCirclePaint = Paint()

    private val scale = context.resources.displayMetrics.density
    private val padding: Float = 8 * scale // 8dp
    private val actions: MutableList<OnboardingAction> = mutableListOf()
    private val featureViewCoordinates = IntArray(2)
    private var onboardingMessage: OnboardingMessage = OnboardingMessage(context)
    private val opacityAnimator = ValueAnimator()
    private var currentDisplayedAction = 0
    private val clickThreshHold = 1000
    private var onBoardingMessageLayoutParams = LayoutParams(0, 0)

    /** fade in settings */
    private var isAnimating = false
    private var currentOpacityBackgroundLevel = 0
    internal var shouldFadeIn = false
    internal var fadeInDuration: Long = 0
    internal var fadeInStartAlpha = 0f
    internal var fadeInStopAlpha = 0.75f

    /** message bubble */
    private var messageHeight = height * 0.3f

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

        actionIndexCirclePaint.apply {
            color = Color.WHITE
            isAntiAlias = true
            strokeWidth = 1f
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }

        currentActionIndexCirclePaint.apply {
            color = Color.WHITE
            isAntiAlias = true
            strokeWidth = 1f
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

        val screenBitMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val screenCanvas = Canvas(screenBitMap)
        if (isAnimating || !shouldFadeIn) {
            screenCanvas.drawARGB(
                currentOpacityBackgroundLevel,
                0,
                0,
                0
            )
        } else {
            isAnimating = true
            opacityAnimator.start()
            return
        }

        // Draw the action
        actions[currentDisplayedAction].let { element ->
            // Save the X, Y positions of the view into an int array
            element.view.getLocationOnScreen(featureViewCoordinates)

            drawFeatureViewRect(screenCanvas, element)
            setUpMessageBubble(screenCanvas, element)
        }

        canvas?.drawBitmap(screenBitMap, 0f, 0f, Paint())
    }

    private fun drawFeatureViewRect(canvas: Canvas, action: OnboardingAction) {
        val v = action.view
        val viewHeight = v.height
        val viewWidth = v.width

        val featureViewRect =
            if (v.javaClass.superclass == AppCompatTextView::class.java && v is TextView) {
                RectF(
                    featureViewCoordinates[0].toFloat() - padding,
                    featureViewCoordinates[1].toFloat() - viewHeight - padding,
                    featureViewCoordinates[0].toFloat() + viewWidth + padding,
                    featureViewCoordinates[1].toFloat() + (v.minHeight) + padding,
                )
            } else {
                RectF(
                    featureViewCoordinates[0].toFloat() - padding,
                    featureViewCoordinates[1].toFloat() - (viewHeight / 2) - padding,
                    featureViewCoordinates[0].toFloat() + viewWidth + padding,
                    featureViewCoordinates[1].toFloat() + (viewHeight / 2) + padding,
                )
            }

        canvas.drawRoundRect(
            featureViewRect,
            15f,
            15f,
            featurePaint
        )
    }

    private fun setUpMessageBubble(canvas: Canvas, action: OnboardingAction) {
        val viewHeight = action.view.height.toFloat()
        val messageBubbleYPosition = action.getTopOrBottomValue(
            topValue = featureViewCoordinates[1] - padding - messageHeight - viewHeight / 2,
            botValue = featureViewCoordinates[1] + 3 * viewHeight / 2 + padding
        )

        onboardingMessage.setUp(
            action.title,
            action.text
        )
        onboardingMessage.layoutParams = onBoardingMessageLayoutParams
        onboardingMessage.translationY = messageBubbleYPosition

        drawMessageBubbleArrow(
            canvas,
            action,
            messageBubbleYPosition
        )

        drawActionIndexIndicator(canvas)
    }

    private fun drawMessageBubbleArrow(
        canvas: Canvas,
        element: OnboardingAction,
        messageBubbleYPosition: Float
    ) {
        val p = Path()
        val viewWidth = element.view.width
        val arcRect = RectF(
            featureViewCoordinates[0] + viewWidth / 2 - padding,
            element.getTopOrBottomValue(
                messageBubbleYPosition
                        + (messageHeight / 2),
                messageBubbleYPosition - padding
            ),
            featureViewCoordinates[0] + viewWidth / 2 + padding,
            element.getTopOrBottomValue(
                messageBubbleYPosition + (messageHeight / 2) + 8 * padding,
                messageBubbleYPosition + messageHeight / 2
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

        canvas.drawPath(p, pathPaint)
    }

    private fun drawActionIndexIndicator(canvas: Canvas) {
        val countOfActions = actions.size
        val circleRadius = padding / 2

        for (i in 0 until countOfActions) {
            canvas.drawCircle(
                width / 2f - ((countOfActions / 2) - i) * (1.5f * padding),
                height - 6 * padding,
                circleRadius,
                if (i == currentDisplayedAction)
                    currentActionIndexCirclePaint
                else
                    actionIndexCirclePaint
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        messageHeight = h * 0.3f
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

    // returns the topValue if the element is placed at the top else bottom value
    // this is an useful helper method when you need to calculate two different values
    // based on the position of the message bubble
    private fun <T> OnboardingAction.getTopOrBottomValue(topValue: T, botValue: T): T =
        if (this.verticalPosition == VerticalPosition.TOP) topValue else botValue
}