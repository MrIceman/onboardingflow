package com.nowocode.lib.ui.model

import android.view.View
import java.lang.ref.WeakReference

internal data class OnboardingAction(
    val view: WeakReference<View>,
    val text: String,
    val title: String,
    val messagePosition: MessagePosition
)