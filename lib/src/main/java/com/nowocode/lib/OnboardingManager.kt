package com.nowocode.lib

import android.app.Activity
import android.content.Context
import android.view.View
import com.nowocode.lib.ui.model.OnboardingAction
import com.nowocode.lib.ui.model.VerticalPosition

interface OnboardingManager {

    /**
     * A callback that is executed after user has gone through the onboarding flow
     */
    fun onDone(callback: () -> Unit): OnboardingManager

    /**
     * The current visible activity. It is required for adding the mask
     */
    fun setActivity(activity: Activity): OnboardingManager

    /**
     * If called then the onboarding flow wil have an initial fade-in animation
     *
     * @param durationInMs the total length of the fade-in animation
     * @param fromAlpha the start value at which the fade-in should happen (recommended is 0)
     * @param toAlpha the final alpha level to which the onboarding flow will be animated to
     */
    fun setFadeIn(
        durationInMs: Long = 1500L,
        fromAlpha: Float = 0f,
        toAlpha: Float = 0.8f
    ): OnboardingManager


    /**
     * Sets the hint text at the bottom of the scaffold, which tells the user how to
     * continue through the onboarding process.
     *
     * @param text the hint text that should be displayed
     */
    fun setContinueHintText(text: String): OnboardingManager

    /**
     * Adds an [OnboardingAction], the actions are shown in insertion order
     *
     * @param action an action contains all the information about your feature that you want to
     *               display within your flow
     */
    fun addAction(
        action: OnboardingAction
    ): OnboardingManager

    /**
     * Starts the onboarding flow.
     */
    fun start()


    companion object {
        fun instance(context: Context): OnboardingManager = OnboardingManagerImpl(context)
    }
}