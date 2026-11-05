/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecomerceplatformmain;

import java.util.ArrayList;

public class Order {
    private int orderId;
    private int userId;
    private ArrayList<OrderItem> productList;
    private double totalPrice;
    private String paymentMethod;
    private String date;

    public Order(int orderId, int userId, double totalPrice, String paymentMethod, String date) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.date = date;
        this.productList = new ArrayList<>();
    }

    public int getOrderId() { return orderId; }
    public int getUserId() { return userId; }
    public ArrayList<OrderItem> getProductList() { return productList; }
    public double getTotalPrice() { return totalPrice; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getDate() { return date; }

    public void addItem(OrderItem item) {
        productList.add(item);
    }

    public int getItemCount() {
        int count = 0;
        for (OrderItem item : productList) {
            count += item.getQuantity();
        }
        return count;
    }
}