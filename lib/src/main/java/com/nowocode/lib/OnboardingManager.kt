package com.nowocode.lib

import android.app.Activity
import android.content.Context
import android.view.View
import com.nowocode.lib.ui.model.OnboardingAction
import com.nowocode.lib.ui.model.VerticalPosition

interface OnboardingManager {

    fun setOnFinishListener(callback: () -> Unit)

    fun setActivity(activity: Activity): OnboardingManager

    fun setFadeIn(
        fadeIn: Boolean,
        durationInMs: Long = 150000L,
        fromAlpha: Float = 0f,
        toAlpha: Float = 0.8f
    ): OnboardingManager

    fun addAction(
        action: OnboardingAction,
        onNext: (() -> Unit)? = null,
    ): OnboardingManager

    fun start()


    companion object {
        fun instance(context: Context): OnboardingManager = OnboardingManagerImpl(context)
    }
}