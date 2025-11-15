package com.marwadiuniversity.abckids

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.widget.ImageView

class LearningOptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_options)

        setupOptionCards()
        setupBackButton()
    }

    private fun setupOptionCards() {
        // Phonics Card
        findViewById<CardView>(R.id.cardPhonics).setOnClickListener {
            startActivity(Intent(this, PhonicsActivity::class.java))
        }

        // Card Matching Card
        findViewById<CardView>(R.id.cardMatching).setOnClickListener {
            startActivity(Intent(this, CardMatchingActivity::class.java))
        }
    }

    private fun setupBackButton() {
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            onBackPressed()
        }
    }
}