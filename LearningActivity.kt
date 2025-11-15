package com.marwadiuniversity.abckids

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView

class LearningActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning)

        setupViews()
    }

    private fun setupViews() {
        val btnLearning = findViewById<Button>(R.id.btnLearning)
        val btnBack = findViewById<ImageView>(R.id.btn_back)

        btnLearning.setOnClickListener {
            startActivity(Intent(this, LearningOptionsActivity::class.java))
        }

        btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}