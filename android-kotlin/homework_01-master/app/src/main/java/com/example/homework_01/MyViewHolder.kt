package com.example.homework_01

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val text = view.findViewById<TextView>(R.id.text_1)
    val image = view.findViewById<ImageView>(R.id.image_view)

    fun bind(number: Int, position: Int) {

        text.text = "$number"

        if (position % 2 == 0) {
            image.setBackgroundColor(ContextCompat.getColor(image.context, R.color.red))
        } else {
            image.setBackgroundColor(ContextCompat.getColor(image.context, R.color.blue))
        }
    }
}