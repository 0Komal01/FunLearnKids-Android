package com.marwadiuniversity.abckids.utils

import android.content.Context
import android.view.animation.*

object AnimationHelper1 {

    fun pulseAnimation(context: Context): Animation {
        val scaleUp = ScaleAnimation(
            1f, 1.2f, 1f, 1.2f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
        }

        val scaleDown = ScaleAnimation(
            1.2f, 1f, 1.2f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
            startOffset = 200
        }

        return AnimationSet(false).apply {
            addAnimation(scaleUp)
            addAnimation(scaleDown)
        }
    }

    fun bounceAnimation(context: Context): Animation {
        return ScaleAnimation(
            1f, 1.1f, 1f, 1.1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 300
            interpolator = BounceInterpolator()
        }
    }

    fun fadeInAnimation(context: Context): Animation {
        return AlphaAnimation(0f, 1f).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    fun slideUpAnimation(context: Context): Animation {
        return TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 1f,
            Animation.RELATIVE_TO_SELF, 0f
        ).apply {
            duration = 600
            interpolator = DecelerateInterpolator()
        }
    }

    fun slideInFromLeft(context: Context): Animation {
        return TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -1f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f
        ).apply {
            duration = 500
            interpolator = DecelerateInterpolator()
        }
    }

    fun slideInFromRight(context: Context): Animation {
        return TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 1f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f
        ).apply {
            duration = 500
            interpolator = DecelerateInterpolator()
        }
    }

    fun rotateAnimation(context: Context, degrees: Float = 360f): Animation {
        return RotateAnimation(
            0f, degrees,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 1000
            interpolator = LinearInterpolator()
        }
    }

    fun shakeAnimation(context: Context): Animation {
        return TranslateAnimation(
            0f, 10f, 0f, 0f
        ).apply {
            duration = 50
            repeatCount = 6
            repeatMode = Animation.REVERSE
            interpolator = CycleInterpolator(6f)
        }
    }

    fun sparkleAnimation(context: Context): Animation {
        val scaleAnimation = ScaleAnimation(
            0f, 1f, 0f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
        }

        val alphaAnimation = AlphaAnimation(0f, 1f).apply {
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
        }

        val rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 800
            interpolator = LinearInterpolator()
        }

        return AnimationSet(true).apply {
            addAnimation(scaleAnimation)
            addAnimation(alphaAnimation)
            addAnimation(rotateAnimation)
        }
    }
}