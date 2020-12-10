package com.nowocode.lib

import android.app.Activity
import android.content.Context
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.FrameLayout
import com.nowocode.lib.ui.model.OnboardingAction
import com.nowocode.lib.ui.OnboardingScaffold
import com.nowocode.lib.ui.model.MessagePosition
import java.lang.ref.WeakReference

internal class OnboardingManagerImpl(private val context: Context) : OnboardingManager {
    private var onboardingScaffold: OnboardingScaffold? = null

    override fun setActivity(activity: Activity): OnboardingManager {
        val view = OnboardingScaffold(context)
        activity.addContentView(
            view,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        onboardingScaffold = view
        return this
    }

    override fun addOnboardingFeature(
        view: View,
        text: String,
        onNext: (() -> Unit)?,
        messagePosition: MessagePosition,
    ): OnboardingManager {
        onboardingScaffold?.addOnBoardingAction(
            OnboardingAction(
                WeakReference(view),
                text,
                text,
                messagePosition
            )
        )
        return this
    }

    override fun start() {
        onboardingScaffold?.bringToFront()
        onboardingScaffold?.invalidate()
    }

}