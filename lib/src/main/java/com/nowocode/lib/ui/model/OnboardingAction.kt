package com.nowocode.lib.ui.model

import android.view.View
import java.lang.ref.WeakReference

data class OnboardingAction(
    val view: View,
    val text: String,
    val title: String,
    val verticalPosition: VerticalPosition
)