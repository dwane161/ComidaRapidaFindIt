package com.djdevelopment.comidarapidafindit.tools

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView

import com.djdevelopment.comidarapidafindit.R
import com.djdevelopment.comidarapidafindit.data.Restaurants

import org.json.JSONException
import org.json.JSONObject
import java.lang.Math.*

import java.util.ArrayList

class ValuedPlacesAdapter(private val restaurants: List<Restaurants>, private val itemClickLister: ValuedPlacesAdapter.OnItemClickLister) : RecyclerView.Adapter<ValuedPlacesAdapter.ViewHolder>() {
    private var rating = 0f
    private val ratingList = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValuedPlacesAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_valued_places, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ValuedPlacesAdapter.ViewHolder, position: Int) {
        holder.bindView(restaurants[position], itemClickLister)
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    inner class ViewHolder(internal val itemsView: View) : RecyclerView.ViewHolder(itemsView) {

        internal var txtValuedPlaces: TextView = itemsView.findViewById(R.id.txtValuedPlaces) as TextView
        internal var ratingBarValuedPlaces: RatingBar = itemsView.findViewById(R.id.ratingBarValuedPlaces) as RatingBar

        fun bindView(restaurants: Restaurants, listener: OnItemClickLister) {
            try {

                txtValuedPlaces.text = restaurants.restName
                ratingBarValuedPlaces.rating = getRatingList(restaurants)

                itemsView.setOnClickListener{ listener.OnItemClick(restaurants, adapterPosition) }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }

    interface OnItemClickLister {
        fun OnItemClick(name: Restaurants, position: Int)
    }

    fun getRatingList(restaurants: Restaurants): Float {
        try {
            if (ratingList.size != 0) {
                rating = 0f
                ratingList.clear()
            }
            for (restRating in restaurants.rating!!.values) {
                try {
                    val jObj = JSONObject(restRating)
                    ratingList.add(jObj.getString("rating"))
                } catch (e: JSONException) {
                    Log.e("MYAPP", "unexpected JSON exception", e)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }
            var i: Int = 0
            while (i < ratingList.size) {
                rating += java.lang.Float.parseFloat(ratingList[i])
                i++

            }
            rating /= i
            rating = round(rating * 100.0f) / 100.0f
            return rating
        } catch (ex: Exception) {
            ex.printStackTrace()
            return 0f
        }

    }

}
