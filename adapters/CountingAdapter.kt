package com.marwadiuniversity.abckids.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.marwadiuniversity.abckids.R
import com.marwadiuniversity.abckids.CountingQuestion

class CountingAdapter(
    private val question: CountingQuestion,
    private val listener: OnCountingInteractionListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_OBJECT = 0
        const val VIEW_TYPE_OPTION = 1
    }

    interface OnCountingInteractionListener {
        fun onAnswerSelected(selectedAnswer: Int, correctAnswer: Int)
        fun onObjectTapped(objectType: String, count: Int)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < question.visualObjects.size) VIEW_TYPE_OBJECT else VIEW_TYPE_OPTION
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_OBJECT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_counting_object, parent, false)
                ObjectViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_counting_option, parent, false)
                OptionViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ObjectViewHolder -> {
                val obj = question.visualObjects[position]
                holder.bind(obj)
            }
            is OptionViewHolder -> {
                val optionIndex = position - question.visualObjects.size
                val option = question.options[optionIndex]
                holder.bind(option)
            }
        }
    }

    override fun getItemCount() = question.visualObjects.size + question.options.size

    inner class ObjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivObject: ImageView = itemView.findViewById(R.id.ivObject)
        private val tvCount: TextView = itemView.findViewById(R.id.tvCount)

        fun bind(obj: com.marwadiuniversity.abckids.CountingObject) {
            tvCount.text = obj.count.toString()

            ivObject.setOnClickListener {
                listener.onObjectTapped(obj.type, obj.count)
            }
        }
    }

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val btnOption: Button = itemView.findViewById(R.id.btnOption)

        fun bind(option: Int) {
            btnOption.text = option.toString()

            btnOption.setOnClickListener {
                listener.onAnswerSelected(option, question.correctAnswer)
            }
        }
    }
}
