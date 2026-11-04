/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ds.ecommerce;

/**
 *
 * @author Rudy Anonuevo
 */
public class Product {
    private int id;
    private String name;
    private double price;
    private String category;
    private int stock;
    private double rating;
    private int ratingCount;

    public Product(int id, String name, double price, String category, int stock, double rating, int ratingCount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.stock = stock;
        this.rating = rating;
        this.ratingCount = ratingCount;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public int getStock() { return stock; }
    public double getRating() { return rating; }
    public int getRatingCount() { return ratingCount; }

    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setStock(int stock) { this.stock = stock; }

    public String getRatingText() {
        if (ratingCount == 0) return "No ratings";
        return String.format("%.1f (%d)", rating, ratingCount);
    }
}
