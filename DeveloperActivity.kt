package com.marwadiuniversity.abckids

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DeveloperActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var developer1Name: TextView
    private lateinit var developer1Branch: TextView
    private lateinit var developer2Name: TextView
    private lateinit var developer2Branch: TextView
    private lateinit var guideInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)

        initializeViews()
        setupDeveloperInfo()
        setupClickListeners()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btn_back)
        developer1Name = findViewById(R.id.developer1_name)
        developer1Branch = findViewById(R.id.developer1_branch)
        developer2Name = findViewById(R.id.developer2_name)
        developer2Branch = findViewById(R.id.developer2_branch)
        guideInfo = findViewById(R.id.guide_info)
    }

    private fun setupDeveloperInfo() {
        // Set developer 1 information
        developer1Name.text = "Komal Kumari"
        developer1Branch.text = "B.Tech, Computer Engineering"

        // Set developer 2 information
        developer2Name.text = "Thun Thingyan Phoo"
        developer2Branch.text = "B.Tech, Computer Engineering"

        // Set guide information
        guideInfo.text = "Mr. Jigar Dave\nAssistant Professor\nFoET, Marwadi University"
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }
}