package com.marwadiuniversity.abckids.utils

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import com.marwadiuniversity.abckids.R

object AnimationHelper {

    fun bounceAnimation(context: Context): Animation =
        AnimationUtils.loadAnimation(context, R.anim.bounce)

    fun fadeInAnimation(context: Context): Animation =
        AnimationUtils.loadAnimation(context, R.anim.fade_in)

    fun slideInRightAnimation(context: Context): Animation =
        AnimationUtils.loadAnimation(context, R.anim.slide_in_right)

    fun slideInLeftAnimation(context: Context): Animation =
        AnimationUtils.loadAnimation(context, R.anim.slide_in_left)

    fun slideUpAnimation(context: Context): Animation =
        AnimationUtils.loadAnimation(context, R.anim.slide_up)

    fun pulseAnimation(context: Context): Animation =
        AnimationUtils.loadAnimation(context, R.anim.pulse)

    fun rotateAnimation(context: Context): Animation =
        AnimationUtils.loadAnimation(context, R.anim.rotate_360)

    fun correctPulseAnimation(context: Context): Animation =
        AnimationUtils.loadAnimation(context, R.anim.correct_pulse)

    fun incorrectShakeAnimation(context: Context): Animation =
        AnimationUtils.loadAnimation(context, R.anim.incorrect_shake)

    fun slideInBottomAnimation(context: Context): Animation =
        AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)

    fun scaleUpAnimation(context: Context): Animation {
        val scaleAnimation = ScaleAnimation(
            1.0f, 1.2f, // Scale from 100% to 120% on X-axis
            1.0f, 1.2f, // Scale from 100% to 120% on Y-axis
            Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point X (center)
            Animation.RELATIVE_TO_SELF, 0.5f  // Pivot point Y (center)
        )

        scaleAnimation.duration = 300
        scaleAnimation.repeatCount = 1
        scaleAnimation.repeatMode = Animation.REVERSE
        scaleAnimation.interpolator = android.view.animation.AccelerateDecelerateInterpolator()

        return scaleAnimation
    }

    // Extension function for easier animation application
    fun View.animateWithDelay(animation: Animation, delay: Long = 0) {
        animation.startOffset = delay
        this.startAnimation(animation)
    }
}