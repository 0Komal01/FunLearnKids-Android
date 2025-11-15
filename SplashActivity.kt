package com.marwadiuniversity.abckids

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.marwadiuniversity.abckids.utils.AnimationHelper

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val SPLASH_DURATION = 1500L
    }

    private lateinit var title: TextView
    private lateinit var subtitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initializeViews()
        startAnimations()
        navigateToMainActivity()
    }

    private fun initializeViews() {
        title = findViewById(R.id.splash_title)
        subtitle = findViewById(R.id.splash_subtitle)
    }

    private fun startAnimations() {
        val bounceAnimation = AnimationHelper.bounceAnimation(this)
        val fadeInAnimation = AnimationHelper.fadeInAnimation(this)

//        logo.startAnimation(bounceAnimation)
        title.startAnimation(fadeInAnimation)
        subtitle.startAnimation(fadeInAnimation)
    }

    private fun navigateToMainActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, SPLASH_DURATION)
    }
}