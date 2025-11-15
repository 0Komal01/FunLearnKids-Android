package com.marwadiuniversity.abckids.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.marwadiuniversity.abckids.MatchingCard
import com.marwadiuniversity.abckids.R

class CardMatchingAdapter(
    private val cards: List<MatchingCard>,
    private val listener: OnCardInteractionListener,
    private val currentLevel: Int = 1
) : RecyclerView.Adapter<CardMatchingAdapter.CardViewHolder>() {

    interface OnCardInteractionListener {
        fun onCardClicked(card: MatchingCard, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_pair, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.bind(card, position)
    }

    override fun getItemCount() = cards.size

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardMemory)
        private val ivCardFront: ImageView = itemView.findViewById(R.id.ivCardFront)
        private val ivCardBack: ImageView = itemView.findViewById(R.id.ivCardBack)

        fun bind(card: MatchingCard, position: Int) {
            if (card.isEmpty) {
                // Empty slot - make it invisible
                itemView.visibility = View.INVISIBLE
                return
            }

            itemView.visibility = View.VISIBLE

            // Adjust card size for level 3
            if (currentLevel == 3) {
                val layoutParams = itemView.layoutParams
                layoutParams.height = dpToPx(85) // Smaller height for level 3
                itemView.layoutParams = layoutParams

                // Adjust margins for level 3
                if (layoutParams is ViewGroup.MarginLayoutParams) {
                    val smallMargin = dpToPx(4)
                    layoutParams.setMargins(smallMargin, smallMargin, smallMargin, smallMargin)
                }
            } else {
                val layoutParams = itemView.layoutParams
                layoutParams.height = dpToPx(120) // Normal height for level 1 & 2
                itemView.layoutParams = layoutParams

                // Normal margins for level 1 & 2
                if (layoutParams is ViewGroup.MarginLayoutParams) {
                    val normalMargin = dpToPx(8)
                    layoutParams.setMargins(normalMargin, normalMargin, normalMargin, normalMargin)
                }
            }

            // Show front or back based on card state
            if (card.isFlipped || card.isMatched) {
                ivCardFront.visibility = View.VISIBLE
                ivCardBack.visibility = View.GONE

                // Load card image safely
                setCardImage(itemView.context, card.imageResource, ivCardFront)
            } else {
                ivCardFront.visibility = View.GONE
                ivCardBack.visibility = View.VISIBLE
                // Set the card back color directly
                ivCardBack.setBackgroundColor(Color.parseColor("#FF48C9B0"))
            }

            // Change appearance for matched cards
            if (card.isMatched) {
                // Beautiful green gradient for matched cards
                cardView.setCardBackgroundColor(Color.parseColor("#4CAF50"))
                cardView.alpha = 0.9f
            } else {
                // Beautiful white background for normal cards
                cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                cardView.alpha = 1.0f
            }

            cardView.setOnClickListener {
                listener.onCardClicked(card, position)
            }
        }

        private fun setCardImage(context: Context, imageName: String, imageView: ImageView) {
            try {
                // Get drawable resource ID by name
                val resourceId = context.resources.getIdentifier(
                    imageName,
                    "drawable",
                    context.packageName
                )

                if (resourceId != 0) {
                    imageView.setImageResource(resourceId)
                    imageView.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    // Fallback: set a beautiful gradient color background instead of image
                    imageView.setBackgroundColor(getBeautifulColorForLetter(imageName))
                    imageView.setImageDrawable(null)
                }
            } catch (e: Exception) {
                // Emergency fallback: set a beautiful default color
                imageView.setBackgroundColor(Color.parseColor("#6C63FF"))
                imageView.setImageDrawable(null)
            }
        }

        private fun setCardBackBackground(imageView: ImageView) {
            // Set the turquoise color you specified
            imageView.setBackgroundColor(Color.parseColor("#FF48C9B0"))
        }

        private fun getBeautifulColorForLetter(letter: String): Int {
            // Beautiful gradient colors for each letter
            val beautifulColors = arrayOf(
                Color.parseColor("#FF6B6B"), // Coral Red - A
                Color.parseColor("#4ECDC4"), // Turquoise - B
                Color.parseColor("#45B7D1"), // Sky Blue - C
                Color.parseColor("#96CEB4"), // Mint Green - D
                Color.parseColor("#FECA57"), // Sunflower Yellow - E
                Color.parseColor("#FF9FF3"), // Pink - F
                Color.parseColor("#54A0FF"), // Royal Blue - G
                Color.parseColor("#5F27CD"), // Purple - H
                Color.parseColor("#00D2D3"), // Cyan - I
                Color.parseColor("#FF9F43"), // Orange - J
                Color.parseColor("#10AC84"), // Emerald - K
                Color.parseColor("#EE5A24"), // Red Orange - L
                Color.parseColor("#0ABDE3"), // Light Blue - M
                Color.parseColor("#006BA6"), // Deep Blue - N
                Color.parseColor("#F79F1F"), // Golden - O
                Color.parseColor("#A3CB38"), // Lime Green - P
                Color.parseColor("#EA2027"), // Red - Q
                Color.parseColor("#6C5CE7"), // Lavender - R
                Color.parseColor("#A29BFE"), // Light Purple - S
                Color.parseColor("#FD79A8"), // Rose - T
                Color.parseColor("#FDCB6E"), // Peach - U
                Color.parseColor("#6C63FF"), // Indigo - V
                Color.parseColor("#74B9FF"), // Light Blue - W
                Color.parseColor("#E17055"), // Terra Cotta - X
                Color.parseColor("#00B894"), // Teal - Y
                Color.parseColor("#E84393")  // Magenta - Z
            )

            val index = if (letter.isNotEmpty()) {
                (letter[0].lowercaseChar() - 'a').coerceIn(0, beautifulColors.size - 1)
            } else {
                0
            }

            return beautifulColors[index]
        }

        private fun dpToPx(dp: Int): Int {
            val density = itemView.context.resources.displayMetrics.density
            return (dp * density).toInt()
        }
    }
}