package com.marwadiuniversity.abckids

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Build
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.marwadiuniversity.abckids.utils.AnimationHelper1
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class GlitterDrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var drawPath = Path()
    private var drawPaint = Paint()
    private var canvasPaint = Paint(Paint.DITHER_FLAG)
    private var drawCanvas: Canvas? = null
    private var canvasBitmap: Bitmap? = null

    private var brushSize = 10f
    private var currentColor = Color.BLACK
    private var isErasing = false
    private var isGlitterMode = false

    // Glitter particles list
    private val glitterParticles = mutableListOf<GlitterParticle>()

    data class GlitterParticle(
        var x: Float,
        var y: Float,
        var size: Float,
        var color: Int,
        var alpha: Int = 255
    )

    init {
        setupDrawing()
    }

    private fun setupDrawing() {
        drawPaint.apply {
            color = currentColor
            isAntiAlias = true
            strokeWidth = brushSize
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvasBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, canvasPaint)
        }

        if (isGlitterMode) {
            // Draw glitter particles
            val glitterPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
            }

            glitterParticles.forEach { particle ->
                glitterPaint.color = particle.color
                glitterPaint.alpha = particle.alpha
                canvas.drawCircle(particle.x, particle.y, particle.size, glitterPaint)
            }
        }

        canvas.drawPath(drawPath, drawPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                drawPath.moveTo(touchX, touchY)
                if (isGlitterMode && !isErasing) {
                    addGlitterParticles(touchX, touchY)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                drawPath.lineTo(touchX, touchY)
                if (isGlitterMode && !isErasing) {
                    addGlitterParticles(touchX, touchY)
                }
            }
            MotionEvent.ACTION_UP -> {
                drawCanvas?.drawPath(drawPath, drawPaint)

                // Draw glitter particles to canvas
                if (isGlitterMode && !isErasing) {
                    drawGlitterToCanvas()
                }

                drawPath.reset()
                glitterParticles.clear()
            }
        }
        invalidate()
        return true
    }

    private fun addGlitterParticles(x: Float, y: Float) {
        // Add glitter particles around the stroke
        repeat(8) {
            val offsetX = Random.nextFloat() * brushSize * 2 - brushSize
            val offsetY = Random.nextFloat() * brushSize * 2 - brushSize
            val particleSize = Random.nextFloat() * 3f + 1f

            val glitterColors = listOf(
                Color.parseColor("#FFD700"), // Gold
                Color.parseColor("#C0C0C0"), // Silver
                Color.parseColor("#FFFFFF"), // White
                Color.parseColor("#FFC0CB"), // Pink
                Color.parseColor("#87CEEB"), // Sky Blue
                createShimmerColor(currentColor)
            )

            val particle = GlitterParticle(
                x = x + offsetX,
                y = y + offsetY,
                size = particleSize,
                color = glitterColors.random(),
                alpha = Random.nextInt(150, 255)
            )

            glitterParticles.add(particle)
        }
    }

    private fun createShimmerColor(baseColor: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(baseColor, hsv)
        hsv[1] = hsv[1] * 0.7f // Reduce saturation
        hsv[2] = Math.min(1f, hsv[2] * 1.3f) // Increase brightness
        return Color.HSVToColor(hsv)
    }

    private fun drawGlitterToCanvas() {
        val glitterPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        glitterParticles.forEach { particle ->
            glitterPaint.color = particle.color
            glitterPaint.alpha = particle.alpha
            drawCanvas?.drawCircle(particle.x, particle.y, particle.size, glitterPaint)
        }
    }

    fun setColor(color: Int) {
        currentColor = color
        if (!isErasing) {
            drawPaint.color = if (isGlitterMode) createShimmerColor(currentColor) else currentColor
            drawPaint.xfermode = null
        }
    }

    fun setBrushSize(size: Float) {
        brushSize = size
        drawPaint.strokeWidth = brushSize
    }

    fun setEraserMode(erasing: Boolean) {
        isErasing = erasing
        if (isErasing) {
            drawPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            drawPaint.color = Color.TRANSPARENT
        } else {
            drawPaint.xfermode = null
            drawPaint.color = if (isGlitterMode) createShimmerColor(currentColor) else currentColor
        }
    }

    fun setGlitterMode(glitter: Boolean) {
        isGlitterMode = glitter
        if (!isErasing) {
            drawPaint.color = if (isGlitterMode) createShimmerColor(currentColor) else currentColor
        }
    }

    fun isEraserMode(): Boolean = isErasing

    fun clearCanvas() {
        drawCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        glitterParticles.clear()
        invalidate()
    }

    fun saveDrawing(): Bitmap? {
        return canvasBitmap
    }

    fun saveDrawingWithBackground(backgroundBitmap: Bitmap): Bitmap? {
        return canvasBitmap?.let { drawing ->
            val resultBitmap = Bitmap.createBitmap(drawing.width, drawing.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(resultBitmap)

            // Draw background first
            canvas.drawBitmap(backgroundBitmap, 0f, 0f, null)

            // Draw the artwork on top
            canvas.drawBitmap(drawing, 0f, 0f, null)

            resultBitmap
        }
    }
}

class ArtActivity : AppCompatActivity() {

    private lateinit var drawingView: GlitterDrawingView
    private lateinit var colorButtons: List<Button>
    private lateinit var brushSizeSeekBar: SeekBar
    private lateinit var clearButton: Button
    private lateinit var eraseButton: Button
    private lateinit var glitterButton: Button
    private lateinit var saveButton: ImageView
    private lateinit var backButton: ImageView

    private var isGlitterMode = false
    private var artMode = "DRAW" // Default mode

    private val colors = listOf(
        Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
        Color.parseColor("#800080"), // PURPLE
        Color.parseColor("#FFA500"), // ORANGE
        Color.parseColor("#FFC0CB"), // PINK
        Color.CYAN,
        Color.BLACK, Color.WHITE, Color.GRAY, Color.MAGENTA,
        Color.parseColor("#8B4513"), // BROWN
        Color.parseColor("#FFD700"), // GOLD
        Color.parseColor("#00FF7F"), // SPRING_GREEN
        Color.parseColor("#FF1493"), // DEEP_PINK
        Color.parseColor("#4169E1"), // ROYAL_BLUE
        Color.parseColor("#FF6347"), // TOMATO
        Color.parseColor("#9932CC"), // DARK_ORCHID
        Color.parseColor("#32CD32"), // LIME_GREEN
        Color.parseColor("#FF69B4"), // HOT_PINK
        Color.parseColor("#00CED1")  // DARK_TURQUOISE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_art)

        // Get art mode from intent
        artMode = intent.getStringExtra("ART_MODE") ?: "DRAW"

        initializeViews()
        setupToolbar()
        setupColorPalette()
        setupBrushControls()
        animateInterface()

        // Enable glitter mode by default for draw mode
        if (artMode == "DRAW") {
            isGlitterMode = false  // Changed to false by default
            drawingView.setGlitterMode(false)
            updateGlitterButton()
        }
    }

    private fun initializeViews() {
        drawingView = findViewById(R.id.drawing_view)
        brushSizeSeekBar = findViewById(R.id.brush_size_seekbar)
        clearButton = findViewById(R.id.clear_button)
        eraseButton = findViewById(R.id.erase_button)
        glitterButton = findViewById(R.id.glitter_button)
        saveButton = findViewById(R.id.btn_save)
        backButton = findViewById(R.id.btn_back)

        colorButtons = listOf(
            findViewById(R.id.color_red),
            findViewById(R.id.color_blue),
            findViewById(R.id.color_green),
            findViewById(R.id.color_yellow),
            findViewById(R.id.color_purple),
            findViewById(R.id.color_orange),
            findViewById(R.id.color_pink),
            findViewById(R.id.color_cyan),
            findViewById(R.id.color_black),
            findViewById(R.id.color_white),
            findViewById(R.id.color_gray),
            findViewById(R.id.color_magenta),
            findViewById(R.id.color_brown),
            findViewById(R.id.color_gold),
            findViewById(R.id.color_spring_green),
            findViewById(R.id.color_deep_pink),
            findViewById(R.id.color_royal_blue),
            findViewById(R.id.color_tomato),
            findViewById(R.id.color_dark_orchid),
            findViewById(R.id.color_lime_green),
            findViewById(R.id.color_hot_pink),
            findViewById(R.id.color_dark_turquoise)
        )
    }

    private fun setupToolbar() {
        backButton.setOnClickListener { finish() }

        saveButton.setOnClickListener {
            saveArtworkToGallery()
        }
    }

    private fun setupColorPalette() {
        colorButtons.forEachIndexed { index, button ->
            if (index < colors.size) {
                button.setBackgroundColor(colors[index])
                button.setOnClickListener {
                    selectColor(colors[index], button)
                }
            }
        }
    }

    private fun setupBrushControls() {
        brushSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val brushSize = (progress + 1) * 2f
                drawingView.setBrushSize(brushSize)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        clearButton.setOnClickListener {
            clearCanvas()
        }

        eraseButton.setOnClickListener {
            toggleEraser()
        }

        glitterButton.setOnClickListener {
            toggleGlitter()
        }
    }

    private fun selectColor(color: Int, button: Button) {
        drawingView.setColor(color)

        colorButtons.forEach { it.clearAnimation() }

        val pulseAnimation = AnimationHelper1.pulseAnimation(this)
        button.startAnimation(pulseAnimation)
    }

    private fun clearCanvas() {
        drawingView.clearCanvas()

        val bounceAnimation = AnimationHelper1.bounceAnimation(this)
        clearButton.startAnimation(bounceAnimation)
    }

    private fun toggleEraser() {
        val isCurrentlyErasing = drawingView.isEraserMode()

        if (isCurrentlyErasing) {
            drawingView.setEraserMode(false)
            eraseButton.setBackgroundColor(Color.GRAY)
            eraseButton.text = "🗑️ Erase"
        } else {
            drawingView.setEraserMode(true)
            eraseButton.setBackgroundColor(Color.parseColor("#87CEEB"))
            eraseButton.text = "✏️ Draw"
        }

        val pulseAnimation = AnimationHelper1.pulseAnimation(this)
        eraseButton.startAnimation(pulseAnimation)
    }

    private fun toggleGlitter() {
        isGlitterMode = !isGlitterMode
        drawingView.setGlitterMode(isGlitterMode)
        updateGlitterButton()

        val pulseAnimation = AnimationHelper1.pulseAnimation(this)
        glitterButton.startAnimation(pulseAnimation)
    }

    private fun updateGlitterButton() {
        if (isGlitterMode) {
            glitterButton.setBackgroundColor(Color.parseColor("#FFD700")) // Gold
            glitterButton.text = "✨ Glitter ON"
        } else {
            glitterButton.setBackgroundColor(Color.GRAY)
            glitterButton.text = "✨ Glitter OFF"
        }
    }

    private fun saveArtworkToGallery() {
        try {
            val bitmap = createBitmapWithBackground()
            if (bitmap != null) {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val filename = "glitter_art_$timestamp.png"

                val success = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // For Android 10+ (API 29+) - Use MediaStore API
                    saveImageToGalleryAPI29Plus(bitmap, filename)
                } else {
                    // For older Android versions
                    saveImageToGalleryLegacy(bitmap, filename)
                }

                if (success) {
                    Toast.makeText(this, "Artwork saved to Gallery!", Toast.LENGTH_LONG).show()
                    val pulseAnimation = AnimationHelper1.pulseAnimation(this)
                    saveButton.startAnimation(pulseAnimation)
                } else {
                    Toast.makeText(this, "Failed to save artwork", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Nothing to save!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving artwork: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun createBitmapWithBackground(): Bitmap? {
        val drawing = drawingView.saveDrawing() ?: return null

        // Create background bitmap with white background
        val backgroundBitmap = Bitmap.createBitmap(drawing.width, drawing.height, Bitmap.Config.ARGB_8888)
        val backgroundCanvas = Canvas(backgroundBitmap)

        // Fill with white background
        backgroundCanvas.drawColor(Color.WHITE)

        // Combine background and drawing
        val resultBitmap = Bitmap.createBitmap(drawing.width, drawing.height, Bitmap.Config.ARGB_8888)
        val resultCanvas = Canvas(resultBitmap)

        // Draw white background first
        resultCanvas.drawBitmap(backgroundBitmap, 0f, 0f, null)

        // Draw the artwork on top
        resultCanvas.drawBitmap(drawing, 0f, 0f, null)

        return resultBitmap
    }

    private fun saveImageToGalleryAPI29Plus(bitmap: Bitmap, filename: String): Boolean {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/GlitterArt")
        }

        return try {
            val resolver = contentResolver
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            imageUri?.let { uri ->
                resolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    true
                }
            } ?: false
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun saveImageToGalleryLegacy(bitmap: Bitmap, filename: String): Boolean {
        return try {
            val savedImageURL = MediaStore.Images.Media.insertImage(
                contentResolver,
                bitmap,
                filename,
                "Glitter Art created with Fun Learn Kids app"
            )
            !savedImageURL.isNullOrEmpty()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun animateInterface() {
        val fadeInAnimation = AnimationHelper1.fadeInAnimation(this)

        colorButtons.forEachIndexed { index, button ->
            button.animateWithDelay(fadeInAnimation, index * 30L)
        }

        val slideUpAnimation = AnimationHelper1.slideUpAnimation(this)
        brushSizeSeekBar.animateWithDelay(slideUpAnimation, 600L)
        clearButton.animateWithDelay(slideUpAnimation, 700L)
        eraseButton.animateWithDelay(slideUpAnimation, 800L)
        glitterButton.animateWithDelay(slideUpAnimation, 900L)
        saveButton.animateWithDelay(slideUpAnimation, 1000L)
    }

    private fun View.animateWithDelay(animation: android.view.animation.Animation, delay: Long) {
        animation.startOffset = delay
        startAnimation(animation)
    }
}