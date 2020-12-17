package com.nowocode.lib

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.nowocode.lib.ui.model.OnboardingAction
import com.nowocode.lib.ui.OnboardingScaffold
import com.nowocode.lib.ui.model.VerticalPosition
import java.lang.ref.WeakReference

internal class OnboardingManagerImpl(context: Context) : OnboardingManager {
    private var onBoardingView: OnboardingScaffold = OnboardingScaffold(context)
    private lateinit var activity: Activity

    override fun setActivity(activity: Activity): OnboardingManager {
        this.activity = activity
        return this
    }

    override fun setFadeIn(
        fadeIn: Boolean,
        durationInMs: Long,
        fromAlpha: Float,
        toAlpha: Float
    ): OnboardingManager {
        onBoardingView.shouldFadeIn = fadeIn
        onBoardingView.fadeInStartAlpha = fromAlpha
        onBoardingView.fadeInStopAlpha = toAlpha
        onBoardingView.fadeInDuration = durationInMs
        return this
    }


    override fun addAction(
        action: OnboardingAction,
        onNext: (() -> Unit)?,
    ): OnboardingManager {
        onBoardingView.addOnBoardingAction(
            action
        )
        return this
    }

    override fun start() {
        onBoardingView.initAnimator()
        activity.addContentView(
            onBoardingView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        onBoardingView.bringToFront()
        onBoardingView.invalidate()
    }

    override fun setOnFinishListener(callback: () -> Unit) {
        onBoardingView.onboardingDoneCallback
    }

}