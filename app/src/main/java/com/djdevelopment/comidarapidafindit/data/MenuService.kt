package com.djdevelopment.comidarapidafindit.data

/**
 * Created by Dwane Jimenez on 2/23/2017.
 */

class MenuService{
    var menuName = ""
    var price = 0.0
    var currencyType = 0

    constructor(menuName: String, price: Double, currencyType: Int) {
        this.menuName = menuName
        this.price = price
        this.currencyType = currencyType
    }
    constructor()
}
