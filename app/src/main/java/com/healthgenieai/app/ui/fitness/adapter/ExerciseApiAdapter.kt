package com.healthgenieai.app.ui.fitness.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.healthgenieai.app.R
import com.healthgenieai.app.ui.fitness.model.ExerciseResponse

class ExerciseApiAdapter(
    private val list: List<ExerciseResponse>,
    private val onClick: (ExerciseResponse) -> Unit
) : RecyclerView.Adapter<ExerciseApiAdapter.MyViewHolder>() {

    class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.exerciseImg)
        val name: TextView = v.findViewById(R.id.exerciseName)
        val target: TextView = v.findViewById(R.id.exerciseDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val ex = list[position]

        holder.name.text = ex.name
        holder.target.text = "Target: ${ex.target}"

        Glide.with(holder.itemView.context)
            .load(ex.gifUrl)
            .into(holder.img)

        holder.itemView.setOnClickListener {
            onClick(ex)
        }
    }
}