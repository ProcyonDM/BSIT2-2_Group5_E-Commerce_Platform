/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eccomerceplatform;

import java.util.ArrayList;

public class User {
    private int id;
    private String username;
    private String name;
    private String email;
    private String role; 
    private ArrayList<Order> orderHistory;

    public User(int id, String username, String name, String email, String role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
        this.orderHistory = new ArrayList<>();
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public ArrayList<Order> getOrderHistory() { return orderHistory; }

    public void addOrder(Order order) {
        orderHistory.add(order);
    }
}
