package com.djdevelopment.comidarapidafindit.tools

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView

import com.djdevelopment.comidarapidafindit.R
import com.djdevelopment.comidarapidafindit.data.Ratings
import com.djdevelopment.comidarapidafindit.tools.UtilUI.Companion.getBitmapFromURL

import java.text.SimpleDateFormat
import java.util.Date

class RatingAdapter(private val ratings: List<Ratings>, private val isShowMore: Boolean) : RecyclerView.Adapter<RatingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingAdapter.ViewHolder {
        val ratingView = LayoutInflater.from(parent.context).inflate(R.layout.layout_rating_detail, parent, false)
        return ViewHolder(ratingView)
    }

    override fun onBindViewHolder(holder: RatingAdapter.ViewHolder, position: Int) {
        holder.bindView(ratings[position])
    }

    override fun getItemCount(): Int {
        if (ratings.size >= 4 && !isShowMore) {
            return 4
        } else {
            return ratings.size
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var nombreUsuarioRating: TextView = itemView.findViewById(R.id.nombreUsuarioRating) as TextView
        internal var ratingBarRating: RatingBar = itemView.findViewById(R.id.ratingBarRating) as RatingBar
        internal var commentRating: TextView = itemView.findViewById(R.id.commentRating) as TextView
        internal var imageViewRating: ImageView = itemView.findViewById(R.id.imageViewRating) as ImageView
        internal var fechaRating: TextView = itemView.findViewById(R.id.fechaRating) as TextView

        fun bindView(ratings: Ratings) {

            val date = Date(java.lang.Long.parseLong(ratings.fecha))
            val df2 = SimpleDateFormat("dd/MM/yy HH:mm:ss")
            val dateText = df2.format(date)

            nombreUsuarioRating.text = ratings.userName
            ratingBarRating.rating = ratings.rating
            imageViewRating.setImageBitmap(getBitmapFromURL(ratings.photoURL))
            fechaRating.text = dateText
            commentRating.text = ratings.comment
        }
    }
}
