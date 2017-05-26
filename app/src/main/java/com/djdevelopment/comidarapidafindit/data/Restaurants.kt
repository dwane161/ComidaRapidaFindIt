package com.djdevelopment.comidarapidafindit.data

import java.util.ArrayList
import java.util.HashMap
import java.util.StringTokenizer

/**
 * Created by User on 3/25/2017.
 */

class Restaurants {

    var restName = ""
    var menu: ArrayList<String> = ArrayList()
    var latLong = ""
    var creditCards = ""
    var telephones = ""
    var isValidated = false
    var rating: HashMap<String, String>? = HashMap()
    var urlImage: HashMap<String, String>? = HashMap()
    var delivery = false

    constructor(restName: String, menu: ArrayList<String>, latLong: String, creditCards: String, telephones: String, validated: Boolean, rating: HashMap<String, String>?, urlImage: HashMap<String, String>?, delivery: Boolean) {
        this.restName = restName
        this.menu = menu
        this.latLong = latLong
        this.creditCards = creditCards
        this.telephones = telephones
        this.isValidated = validated
        this.rating = rating
        this.urlImage = urlImage
        this.delivery = delivery
    }
    constructor()
}
