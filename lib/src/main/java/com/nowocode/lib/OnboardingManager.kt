package com.nowocode.lib

import android.app.Activity
import android.content.Context
import android.view.View
import com.nowocode.lib.ui.model.MessagePosition

interface OnboardingManager {

    fun setActivity(activity: Activity): OnboardingManager

    fun addOnboardingFeature(
        view: View,
        title: String,
        text: String,
        messagePosition: MessagePosition,
        onNext: (() -> Unit)? = null,
    ): OnboardingManager

    fun start()


    companion object {
        fun instance(context: Context): OnboardingManager = OnboardingManagerImpl(context)
    }
}