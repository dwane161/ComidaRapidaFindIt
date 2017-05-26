package com.djdevelopment.comidarapidafindit.data

import java.util.Comparator
import java.util.Date

/**
 * Created by Dwane Jimenez on 5/2/2017.
 */

data class Ratings( var comment: String? = null) : Comparator<Ratings> {

    var rating: Float = 0f
    var userName = ""
    var photoURL = ""
    var fecha = ""

    override fun compare(ratings1: Ratings, ratings2: Ratings): Int {
        val fecha1 = Date(ratings1.fecha)
        val fecha2 = Date(ratings2.fecha)
        return fecha1.compareTo(fecha2)
    }
}
