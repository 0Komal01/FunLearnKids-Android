package com.marwadiuniversity.abckids

import android.animation.ObjectAnimator
import android.content.ClipData
import android.graphics.*
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PhonicsActivity : AppCompatActivity() {

    private lateinit var lettersRecyclerView: RecyclerView
    private lateinit var objectsRecyclerView: RecyclerView

    private lateinit var tvLevel: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvDifficulty: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var lettersAdapter: LettersAdapter
    private lateinit var objectsAdapter: ObjectsAdapter

    // Sound system
    private var soundPool: SoundPool? = null
    private var correctSoundId: Int = 0
    private var incorrectSoundId: Int = 0
    private var levelCompleteSoundId: Int = 0
    private var gameCompleteSoundId: Int = 0

    private var selectedLetter: PhonicsLetter? = null
    private var isDragInProgress = false

    // Game state variables
    private var currentLevel = 1
    private val totalLevels = 4
    private var correctMatches = 0
    private var totalScore = 0
    private var currentLevelItems = mutableListOf<PhonicsLetter>()
    private var matchedPairs = mutableSetOf<String>()

    // Complete phonics data A-Z
    private val allPhonicsData = listOf(
        PhonicsLetter("A", "a", "Apple", "a", "audio_apple", "A for Apple"),
        PhonicsLetter("B", "b", "Ball", "b", "audio_ball", "B for Ball"),
        PhonicsLetter("C", "c", "Cat", "c", "audio_cat", "C for Cat"),
        PhonicsLetter("D", "d", "Dog", "d", "audio_dog", "D for Dog"),
        PhonicsLetter("E", "e", "Elephant", "e", "audio_elephant", "E for Elephant"),
        PhonicsLetter("F", "f", "Fish", "f", "audio_fish", "F for Fish"),
        PhonicsLetter("G", "g", "Grapes", "g", "audio_grapes", "G for Grapes"),
        PhonicsLetter("H", "h", "Hat", "h", "audio_hat", "H for Hat"),
        PhonicsLetter("I", "i", "Ice", "i", "audio_ice", "I for Ice"),
        PhonicsLetter("J", "j", "Juice", "j", "audio_juice", "J for Juice"),
        PhonicsLetter("K", "k", "Kite", "k", "audio_kite", "K for Kite"),
        PhonicsLetter("L", "l", "Lion", "l", "audio_lion", "L for Lion"),
        PhonicsLetter("M", "m", "Monkey", "m", "audio_monkey", "M for Monkey"),
        PhonicsLetter("N", "n", "Nest", "n", "audio_nest", "N for Nest"),
        PhonicsLetter("O", "o", "Orange", "o", "audio_orange", "O for Orange"),
        PhonicsLetter("P", "p", "Pizza", "p", "audio_pizza", "P for Pizza"),
        PhonicsLetter("Q", "q", "Queen", "q", "audio_queen", "Q for Queen"),
        PhonicsLetter("R", "r", "Rose", "r", "audio_rose", "R for Rose"),
        PhonicsLetter("S", "s", "Sun", "s", "audio_sun", "S for Sun"),
        PhonicsLetter("T", "t", "Tree", "t", "audio_tree", "T for Tree"),
        PhonicsLetter("U", "u", "Umbrella", "u", "audio_umbrella", "U for Umbrella"),
        PhonicsLetter("V", "v", "Van", "v", "audio_van", "V for Van"),
        PhonicsLetter("W", "w", "Watch", "w", "audio_watch", "W for Watch"),
        PhonicsLetter("X", "x", "Xray", "x", "audio_xray", "X for Xray"),
        PhonicsLetter("Y", "y", "Yak", "y", "audio_yak", "Y for Yak"),
        PhonicsLetter("Z", "z", "Zebra", "z", "audio_zebra", "Z for Zebra")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phonics)

        try {
            initSoundPool()
            setupViews()
            setupBackButton()
            startLevel(currentLevel)
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }

    private fun initSoundPool() {
        try {
            soundPool = SoundPool.Builder().setMaxStreams(4).build()
            correctSoundId = soundPool!!.load(this, R.raw.correct_sound, 1)
            incorrectSoundId = soundPool!!.load(this, R.raw.incorrect_sound, 1)
            levelCompleteSoundId = soundPool!!.load(this, R.raw.level_complete_sound, 1)
            gameCompleteSoundId = soundPool!!.load(this, R.raw.game_complete_sound, 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupViews() {
        try {
            lettersRecyclerView = findViewById(R.id.rvLetters)
            objectsRecyclerView = findViewById(R.id.rvObjects)
            tvLevel = findViewById(R.id.tvLevel)
            tvScore = findViewById(R.id.tvScore)
            tvDifficulty = findViewById(R.id.tvDifficulty)
            progressBar = findViewById(R.id.progressBar)

            lettersRecyclerView.layoutManager = LinearLayoutManager(this)
            objectsRecyclerView.layoutManager = LinearLayoutManager(this)

            updateUI()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupBackButton() {
        try {
            findViewById<ImageView>(R.id.btn_back).setOnClickListener {
                onBackPressed()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startLevel(level: Int) {
        try {
            currentLevel = level
            correctMatches = 0
            matchedPairs.clear()
            selectedLetter = null
            isDragInProgress = false

            // Progressive difficulty system with FRESH random selection each time
            val (itemCount, lettersOrder, objectsOrder, difficultyText) = when (level) {
                1 -> {
                    val randomItems = allPhonicsData.shuffled().take(5)
                    val shuffledLetters = randomItems.shuffled()
                    val shuffledObjects = randomItems.shuffled()
                    Tuple4(5, shuffledLetters, shuffledObjects, "Easy: 5 Random Letters")
                }
                2 -> {
                    val randomItems = allPhonicsData.shuffled().take(6)
                    val shuffledLetters = randomItems.shuffled()
                    val shuffledObjects = randomItems.shuffled()
                    Tuple4(6, shuffledLetters, shuffledObjects, "Medium: 6 Random Letters")
                }
                3 -> {
                    val randomItems = allPhonicsData.shuffled().take(8)
                    val shuffledLetters = randomItems.shuffled()
                    val shuffledObjects = randomItems.shuffled()
                    Tuple4(8, shuffledLetters, shuffledObjects, "Hard: 8 Random Letters")
                }
                4 -> {
                    val randomItems = allPhonicsData.shuffled().take(10)
                    val shuffledLetters = randomItems.shuffled()
                    val shuffledObjects = randomItems.shuffled()
                    Tuple4(10, shuffledLetters, shuffledObjects, "Expert: 10 Random Letters")
                }
                else -> {
                    val randomItems = allPhonicsData.shuffled().take(5)
                    val shuffledLetters = randomItems.shuffled()
                    val shuffledObjects = randomItems.shuffled()
                    Tuple4(5, shuffledLetters, shuffledObjects, "Default: 5 Random Letters")
                }
            }

            currentLevelItems = lettersOrder.toMutableList()

            lettersAdapter = LettersAdapter(lettersOrder)
            objectsAdapter = ObjectsAdapter(objectsOrder)

            lettersRecyclerView.adapter = lettersAdapter
            objectsRecyclerView.adapter = objectsAdapter

            updateUI()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateUI() {
        try {
            tvLevel.text = "Level $currentLevel/$totalLevels"
            tvScore.text = "Score: $totalScore"

            val difficultyTexts = mapOf(
                1 to "Easy: 5 Random Letters",
                2 to "Medium: 6 Random Letters",
                3 to "Hard: 8 Random Letters",
                4 to "Expert: 10 Random Letters"
            )
            tvDifficulty.text = difficultyTexts[currentLevel] ?: ""

            val maxItems = when (currentLevel) {
                1 -> 5
                2 -> 6
                3 -> 8
                4 -> 10
                else -> 5
            }
            progressBar.max = maxItems
            progressBar.progress = correctMatches
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onLetterClicked(letter: PhonicsLetter, view: View) {
        try {
            if (matchedPairs.contains(letter.letter) || isDragInProgress) return

            // If same letter clicked again, unselect it
            if (selectedLetter?.letter == letter.letter) {
                selectedLetter = null
                lettersAdapter.clearSelection()
                return
            }

            // Select new letter (unselects previous if any)
            selectedLetter = letter
            lettersAdapter.setSelectedLetter(letter.letter)

            // Don't start drag automatically - only on long press
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onLetterLongClicked(letter: PhonicsLetter, view: View) {
        try {
            if (matchedPairs.contains(letter.letter) || isDragInProgress) return

            // Select the letter if not already selected
            if (selectedLetter?.letter != letter.letter) {
                selectedLetter = letter
                lettersAdapter.setSelectedLetter(letter.letter)
            }

            // Start drag and drop on long press
            startDragAndDrop(view, letter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startDragAndDrop(view: View, letter: PhonicsLetter) {
        try {
            isDragInProgress = true

            val shadowBuilder = object : View.DragShadowBuilder(view) {
                override fun onProvideShadowMetrics(size: Point, touch: Point) {
                    val width = view.width
                    val height = view.height
                    size.set(width, height)
                    touch.set(width / 2, height / 2)
                }

                override fun onDrawShadow(canvas: Canvas) {
                    val paint = Paint().apply {
                        color = Color.parseColor("#80E3F2FD")
                        style = Paint.Style.FILL
                    }
                    canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), paint)

                    val textPaint = Paint().apply {
                        color = Color.parseColor("#1976D2")
                        textSize = 48f
                        textAlign = Paint.Align.CENTER
                        isAntiAlias = true
                    }
                    canvas.drawText(
                        letter.letter,
                        canvas.width / 2f,
                        canvas.height / 2f + 16f,
                        textPaint
                    )
                }
            }

            val clipData = ClipData.newPlainText("letter", letter.letter)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                view.startDragAndDrop(clipData, shadowBuilder, letter, 0)
            } else {
                @Suppress("DEPRECATION")
                view.startDrag(clipData, shadowBuilder, letter, 0)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            isDragInProgress = false
        }
    }

    private fun handleCorrectMatch(letter: PhonicsLetter) {
        try {
            correctMatches++
            totalScore += (currentLevel * 10)
            matchedPairs.add(letter.letter)

            playCorrectSound()
            animateSuccess()
            updateUI()

            lettersAdapter.setMatchedLetter(letter.letter)
            objectsAdapter.setMatchedLetter(letter.letter)

            resetSelection()

            Handler(Looper.getMainLooper()).postDelayed({
                checkLevelCompletion()
            }, 1000)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleIncorrectMatch() {
        try {
            playIncorrectSound()
            animateWrongMatch()

            Handler(Looper.getMainLooper()).postDelayed({
                resetSelection()
            }, 500)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun resetSelection() {
        selectedLetter = null
        isDragInProgress = false
        lettersAdapter.clearSelection()
    }

    private fun checkLevelCompletion() {
        try {
            val requiredMatches = when (currentLevel) {
                1 -> 5
                2 -> 6
                3 -> 8
                4 -> 10
                else -> 5
            }

            if (correctMatches >= requiredMatches) {
                playLevelCompleteSound()

                if (currentLevel < totalLevels) {
                    showLevelCompletedDialog()
                } else {
                    playGameCompleteSound()
                    showGameCompletedDialog()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showLevelCompletedDialog() {
        try {
            val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Level $currentLevel Completed!")
                .setMessage("Outstanding work!\nScore: $totalScore\nReady for the next challenge?")
                .setPositiveButton("Next Level") { _, _ ->
                    startLevel(currentLevel + 1)
                }
                .setNegativeButton("Replay") { _, _ ->
                    startLevel(currentLevel)
                }
                .setCancelable(false)
                .create()

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showGameCompletedDialog() {
        try {
            val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Phonics Master!")
                .setMessage("Congratulations!\nYou completed all levels!\nFinal Score: $totalScore\nYou're a phonics champion!")
                .setPositiveButton("Play Again") { _, _ ->
                    resetGame()
                }
                .setNegativeButton("Exit") { _, _ ->
                    finish()
                }
                .setCancelable(false)
                .create()

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun resetGame() {
        try {
            currentLevel = 1
            totalScore = 0
            correctMatches = 0
            matchedPairs.clear()
            resetSelection()
            startLevel(1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playCorrectSound() {
        try {
            soundPool?.play(correctSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun playIncorrectSound() {
        try {
            soundPool?.play(incorrectSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun playLevelCompleteSound() {
        try {
            soundPool?.play(levelCompleteSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun playGameCompleteSound() {
        try {
            soundPool?.play(gameCompleteSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun animateSuccess() {
        try {
            val scaleX = ObjectAnimator.ofFloat(progressBar, "scaleX", 1.0f, 1.2f, 1.0f)
            val scaleY = ObjectAnimator.ofFloat(progressBar, "scaleY", 1.0f, 1.2f, 1.0f)
            scaleX.duration = 400
            scaleY.duration = 400
            scaleX.start()
            scaleY.start()

            val scoreScale = ObjectAnimator.ofFloat(tvScore, "scaleX", 1.0f, 1.3f, 1.0f)
            scoreScale.duration = 300
            scoreScale.start()
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun animateWrongMatch() {
        try {
            val shake = ObjectAnimator.ofFloat(objectsRecyclerView, "translationX", 0f, 15f, -15f, 15f, -15f, 0f)
            shake.duration = 500
            shake.start()
        } catch (e: Exception) { e.printStackTrace() }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            soundPool?.release()
        } catch (e: Exception) { e.printStackTrace() }
    }

    data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    inner class LettersAdapter(private val letters: List<PhonicsLetter>) :
        RecyclerView.Adapter<LettersAdapter.LetterViewHolder>() {

        private val matchedLetters = mutableSetOf<String>()
        private var selectedLetterKey: String? = null

        fun setMatchedLetter(letterKey: String) {
            matchedLetters.add(letterKey)
            notifyDataSetChanged()
        }

        fun setSelectedLetter(letterKey: String) {
            selectedLetterKey = letterKey
            notifyDataSetChanged()
        }

        fun clearSelection() {
            selectedLetterKey = null
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_phonics_letter, parent, false)
            return LetterViewHolder(view)
        }

        override fun onBindViewHolder(holder: LetterViewHolder, position: Int) {
            try {
                val letter = letters[position]
                holder.bind(letter, position)
            } catch (e: Exception) { e.printStackTrace() }
        }

        override fun getItemCount() = letters.size

        inner class LetterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvLetter: TextView = itemView.findViewById(R.id.tvLetter)

            fun bind(letter: PhonicsLetter, position: Int) {
                try {
                    tvLetter.text = letter.letter

                    when {
                        matchedLetters.contains(letter.letter) -> {
                            itemView.setBackgroundColor(Color.parseColor("#4CAF50"))
                            tvLetter.setTextColor(Color.WHITE)
                            itemView.alpha = 0.8f
                        }
                        selectedLetterKey == letter.letter -> {
                            itemView.setBackgroundColor(Color.parseColor("#2196F3"))
                            tvLetter.setTextColor(Color.WHITE)
                            itemView.alpha = 1.0f
                        }
                        else -> {
                            itemView.setBackgroundColor(Color.parseColor("#E3F2FD"))
                            tvLetter.setTextColor(Color.parseColor("#1976D2"))
                            itemView.alpha = 1.0f
                        }
                    }

                    itemView.isClickable = true
                    itemView.isLongClickable = true

                    itemView.setOnClickListener {
                        if (!matchedLetters.contains(letter.letter) && !isDragInProgress) {
                            onLetterClicked(letter, itemView)
                        }
                    }

                    itemView.setOnLongClickListener {
                        if (!matchedLetters.contains(letter.letter) && !isDragInProgress) {
                            onLetterLongClicked(letter, itemView)
                            true
                        } else {
                            false
                        }
                    }

                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }

    inner class ObjectsAdapter(private val objects: List<PhonicsLetter>) :
        RecyclerView.Adapter<ObjectsAdapter.ObjectViewHolder>() {

        private val matchedLetters = mutableSetOf<String>()

        fun setMatchedLetter(letterKey: String) {
            matchedLetters.add(letterKey)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_phonics_object, parent, false)
            return ObjectViewHolder(view)
        }

        override fun onBindViewHolder(holder: ObjectViewHolder, position: Int) {
            try {
                val letter = objects[position]
                holder.bind(letter, position)
            } catch (e: Exception) { e.printStackTrace() }
        }

        override fun getItemCount() = objects.size

        inner class ObjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val ivImage: ImageView = itemView.findViewById(R.id.ivImage)

            fun bind(letter: PhonicsLetter, position: Int) {
                try {
                    val imageResId = itemView.context.resources.getIdentifier(
                        letter.letter.lowercase(), "drawable", itemView.context.packageName
                    )

                    if (imageResId != 0) {
                        ivImage.setImageResource(imageResId)
                    } else {
                        ivImage.setImageResource(android.R.drawable.ic_menu_gallery)
                    }

                    if (matchedLetters.contains(letter.letter)) {
                        itemView.setBackgroundColor(Color.parseColor("#4CAF50"))
                        itemView.alpha = 0.8f
                    } else {
                        itemView.setBackgroundColor(Color.parseColor("#FFF3E0"))
                        itemView.alpha = 1.0f
                    }

                    itemView.setOnDragListener { view, dragEvent ->
                        try {
                            when (dragEvent.action) {
                                DragEvent.ACTION_DRAG_STARTED -> {
                                    val canAccept = !matchedLetters.contains(letter.letter) &&
                                            dragEvent.clipDescription?.hasMimeType("text/plain") == true
                                    canAccept
                                }
                                DragEvent.ACTION_DRAG_ENTERED -> {
                                    if (!matchedLetters.contains(letter.letter)) {
                                        view.setBackgroundColor(Color.parseColor("#FFEB3B"))
                                        view.alpha = 0.8f
                                        view.scaleX = 1.1f
                                        view.scaleY = 1.1f
                                    }
                                    true
                                }
                                DragEvent.ACTION_DRAG_EXITED -> {
                                    if (!matchedLetters.contains(letter.letter)) {
                                        view.setBackgroundColor(Color.parseColor("#FFF3E0"))
                                        view.alpha = 1.0f
                                        view.scaleX = 1.0f
                                        view.scaleY = 1.0f
                                    }
                                    true
                                }
                                DragEvent.ACTION_DROP -> {
                                    if (!matchedLetters.contains(letter.letter)) {
                                        val draggedLetter = dragEvent.localState as? PhonicsLetter

                                        if (draggedLetter != null) {
                                            if (draggedLetter.letter.equals(letter.letter, ignoreCase = true)) {
                                                handleCorrectMatch(letter)
                                            } else {
                                                handleIncorrectMatch()
                                            }
                                        } else {
                                            val clipData = dragEvent.clipData
                                            if (clipData != null && clipData.itemCount > 0) {
                                                val draggedLetterText = clipData.getItemAt(0).text.toString()
                                                if (draggedLetterText.equals(letter.letter, ignoreCase = true)) {
                                                    handleCorrectMatch(letter)
                                                } else {
                                                    handleIncorrectMatch()
                                                }
                                            }
                                        }

                                        view.setBackgroundColor(Color.parseColor("#FFF3E0"))
                                        view.alpha = 1.0f
                                        view.scaleX = 1.0f
                                        view.scaleY = 1.0f
                                    }
                                    true
                                }
                                DragEvent.ACTION_DRAG_ENDED -> {
                                    if (!matchedLetters.contains(letter.letter)) {
                                        view.setBackgroundColor(Color.parseColor("#FFF3E0"))
                                        view.alpha = 1.0f
                                    }
                                    view.scaleX = 1.0f
                                    view.scaleY = 1.0f

                                    isDragInProgress = false
                                    true
                                }
                                else -> false
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            isDragInProgress = false
                            view.scaleX = 1.0f
                            view.scaleY = 1.0f
                            false
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    ivImage.setImageResource(android.R.drawable.ic_menu_gallery)
                }
            }
        }
    }
}