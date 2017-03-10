package com.djdevelopment.comidarapidafindit.data;

import java.io.Serializable;

/**
 * Created by Dwane Jimenez on 2/23/2017.
 */

public class MenuService implements Serializable {
    private String menuName = null;
    private double price = 0;
    private int currencyType = 0;

    public MenuService(String menuName, double price, int currencyType) {
        this.menuName = menuName;
        this.price = price;
        this.currencyType = currencyType;
    }

    public MenuService(String service, double price) {
        super();
        this.menuName = service;
        this.price = price;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(int currencyType) {
        this.currencyType = currencyType;
    }
}
