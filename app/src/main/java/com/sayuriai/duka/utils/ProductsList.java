package com.sayuriai.duka.utils;

import com.sayuriai.duka.models.Product;
import com.sayuriai.duka.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductsList {

    List<Product> productsList = new ArrayList<>();
    public List<Product> getproductsList() {
        productsList.add(new Product("Books", "1"));
        productsList.add(new Product("Electronics", "2"));
        productsList.add(new Product("Food", "3"));
        productsList.add(new Product("Clothes", "4"));
        productsList.add(new Product("Shoes", "5"));
        productsList.add(new Product("Wigs", "6"));
        productsList.add(new Product("Bags", "7"));
        productsList.add(new Product("Perfume", "8"));
        return productsList;
    }
}
