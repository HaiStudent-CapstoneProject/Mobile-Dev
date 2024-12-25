package com.dicoding.picodiploma.loginwithanimation.data.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.response.ResponseClassification

class QuestionAdapter : ListAdapter<ResponseClassification, QuestionAdapter.QuestionViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = getItem(position)
        holder.bind(question)
    }

    class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(question: ResponseClassification) {
            val questionText = itemView.findViewById<TextView>(R.id.tvQuestionText)
            val questionDifficulty = itemView.findViewById<TextView>(R.id.tvQuestionDifficulty)
            val questionConfidence = itemView.findViewById<TextView>(R.id.tvQuestionConfidence)
            val questionTimestamp = itemView.findViewById<TextView>(R.id.tvQuestionTimestamp)
            val forumLink = itemView.findViewById<TextView>(R.id.tvForumLink)

            // Set values
            questionText.text = question.text ?: "No Text Available"
            questionDifficulty.text = question.classification?.difficulty ?: "Unknown Difficulty"
            questionConfidence.text = "Confidence: ${question.classification?.confidence ?: "Unknown"}"
            questionTimestamp.text = question.classification?.timestamp ?: "No Timestamp"
            forumLink.text = "Go to Forum"

            // Set click listener for Forum Link
            forumLink.setOnClickListener {
                val context = itemView.context
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(question.forumLink ?: "https://histudent-web.web.app/"))
                context.startActivity(intent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ResponseClassification>() {
            override fun areItemsTheSame(oldItem: ResponseClassification, newItem: ResponseClassification): Boolean {
                return oldItem.text == newItem.text
            }

            override fun areContentsTheSame(oldItem: ResponseClassification, newItem: ResponseClassification): Boolean {
                return oldItem == newItem
            }
        }
    }
}
