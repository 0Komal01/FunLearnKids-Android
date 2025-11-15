package com.marwadiuniversity.abckids

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ArtModeSelectionActivity : AppCompatActivity() {

    private var drawModeButton: Button? = null
    private var colorFillModeButton: Button? = null
    private var backButton: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_art_mode_selection)

            if (!initializeViews()) {
                Log.e("ArtModeSelection", "Failed to initialize views")
                Toast.makeText(this, "Failed to load activity", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            setupButtons()
            animateInterface()
        } catch (e: Exception) {
            Log.e("ArtModeSelection", "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initializeViews(): Boolean {
        return try {
            drawModeButton = findViewById(R.id.draw_mode_button)
            colorFillModeButton = findViewById(R.id.color_fill_mode_button)
            backButton = findViewById(R.id.btn_back)

            if (drawModeButton == null || colorFillModeButton == null || backButton == null) {
                Log.e("ArtModeSelection", "One or more views are null")
                return false
            }

            true
        } catch (e: Exception) {
            Log.e("ArtModeSelection", "Error finding views: ${e.message}", e)
            false
        }
    }

    private fun setupButtons() {
        try {
            drawModeButton?.setOnClickListener {
                try {
                    val intent = Intent(this, ArtActivity::class.java)
                    intent.putExtra("ART_MODE", "DRAW")
                    startActivity(intent)
                    animateButton(it as Button)
                } catch (e: Exception) {
                    Log.e("ArtModeSelection", "Error starting ArtActivity: ${e.message}", e)
                    Toast.makeText(this, "Cannot open drawing mode", Toast.LENGTH_SHORT).show()
                }
            }

            colorFillModeButton?.setOnClickListener {
                try {
                    val intent = Intent(this, ColorFillActivity::class.java)
                    startActivity(intent)
                    animateButton(it as Button)
                } catch (e: Exception) {
                    Log.e("ArtModeSelection", "Error starting ColorFillActivity: ${e.message}", e)
                    Toast.makeText(this, "Cannot open color fill mode", Toast.LENGTH_SHORT).show()
                }
            }

            backButton?.setOnClickListener {
                try {
                    finish()
                } catch (e: Exception) {
                    Log.e("ArtModeSelection", "Error in back button: ${e.message}", e)
                }
            }
        } catch (e: Exception) {
            Log.e("ArtModeSelection", "Error setting up buttons: ${e.message}", e)
        }
    }

    private fun animateInterface() {
        try {
            val slideUpAnimation = android.view.animation.AnimationUtils.loadAnimation(
                this,
                android.R.anim.slide_in_left
            )

            drawModeButton?.postDelayed({
                try {
                    drawModeButton?.startAnimation(slideUpAnimation)
                } catch (e: Exception) {
                    Log.w("ArtModeSelection", "Draw button animation failed", e)
                }
            }, 200L)

            colorFillModeButton?.postDelayed({
                try {
                    val anim = android.view.animation.AnimationUtils.loadAnimation(
                        this,
                        android.R.anim.slide_in_left
                    )
                    colorFillModeButton?.startAnimation(anim)
                } catch (e: Exception) {
                    Log.w("ArtModeSelection", "Color fill button animation failed", e)
                }
            }, 400L)
        } catch (e: Exception) {
            Log.w("ArtModeSelection", "Animation setup failed: ${e.message}", e)
        }
    }

    private fun animateButton(view: Button) {
        try {
            val scaleAnimation = android.view.animation.ScaleAnimation(
                1f, 1.2f,
                1f, 1.2f,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 100
                repeatCount = 1
                repeatMode = android.view.animation.Animation.REVERSE
            }
            view.startAnimation(scaleAnimation)
        } catch (e: Exception) {
            Log.w("ArtModeSelection", "Button animation failed: ${e.message}", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            drawModeButton = null
            colorFillModeButton = null
            backButton = null
        } catch (e: Exception) {
            Log.e("ArtModeSelection", "Error in onDestroy: ${e.message}", e)
        }
    }
}