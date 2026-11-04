/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.productmain;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author HP ELITEBOOK 840 G8
 */
public class Product extends JFrame implements ActionListener {
    private DefaultTableModel productModel;
    private JTable productTable;
    private JScrollPane productScroll;
    private JTextField txtPID, txtName, txtCategory, txtPrice, txtStock;
    private JComboBox<String> cmbSort;
    private JLabel lbHeader, lbPID, lbName, lbCategory, lbPrice, lbStock, lbSort, lbHint1, lbHint2;
    private JButton btnAdd, btnUpdate, btnDelete, btnSearch, btnSort;
    
Product(){
    setTitle("Manage Products [Admin Only]");
    setSize(950, 490);
    setLayout(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);

    lbHeader = new JLabel("Product Information");
    lbHeader.setBounds(10, 5, 250, 30);
    add(lbHeader);

    lbPID = new JLabel("Product ID:");
    lbPID.setBounds(10, 45, 100, 30);
    add(lbPID);

    txtPID = new JTextField();
    txtPID.setBounds(110, 45, 270, 30);
    add(txtPID); 
    
    lbName = new JLabel("Name: ");
    lbName.setBounds(10, 85, 100, 30);
    add(lbName);
    
    txtName = new JTextField();
    txtName.setBounds(110, 85, 270, 30);
    add(txtName);

    lbCategory = new JLabel("Category:");
    lbCategory.setBounds(10, 125, 100, 30);
    add(lbCategory);

    txtCategory = new JTextField();
    txtCategory.setBounds(110, 125, 270, 30);
    add(txtCategory);

    lbPrice = new JLabel("Price:");
    lbPrice.setBounds(10, 165, 100, 30);
    add(lbPrice);

    txtPrice = new JTextField();
    txtPrice.setBounds(110, 165, 270, 30);
    add(txtPrice);
    
    lbStock = new JLabel("Stock:");
    lbStock.setBounds(10, 205, 100, 30);
    add(lbStock);

    txtStock = new JTextField();
    txtStock.setBounds(110, 205, 270, 30);
    add(txtStock);

    btnAdd = new JButton("ADD");
    btnAdd.setBounds(10, 255, 85, 30);
    add(btnAdd);

    btnUpdate = new JButton("UPDATE");
    btnUpdate.setBounds(105, 255, 85, 30);
    add(btnUpdate);

    btnDelete = new JButton("DELETE");
    btnDelete.setBounds(200, 255, 85, 30);
    add(btnDelete);

    btnSearch = new JButton("SEARCH");
    btnSearch.setBounds(295, 255, 85, 30);
    add(btnSearch);

    lbSort = new JLabel("Sort by:");
    lbSort.setBounds(10, 305, 100, 30);
    add(lbSort);

    cmbSort = new JComboBox<>(new String[]{"Product ID", "Price (Low to High)", "Price (High to Low)", "Rating (Best First)", "Name (A-Z)"});
    cmbSort.setBounds(110, 305, 270, 30);
    add(cmbSort);

    btnSort = new JButton("Sort / Refresh / Show All");
    btnSort.setBounds(10, 345, 370, 30);
    add(btnSort);

    lbHint1 = new JLabel("Search by Product ID or Name (starts with).");
    lbHint1.setBounds(10, 390, 380, 20);
    add(lbHint1);

    lbHint2 = new JLabel("Update: select a row, then type the new details.");
    lbHint2.setBounds(10, 413, 380, 20);
    add(lbHint2);

    productTable = new JTable(productModel);
    productScroll = new JScrollPane(productTable);
    productScroll.setBounds(400, 10, 530, 430);
    add(productScroll);

    btnAdd.addActionListener(this);
    btnUpdate.addActionListener(this);
    btnDelete.addActionListener(this);
    btnSearch.addActionListener(this);
    btnSort.addActionListener(this);

    
}

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }

            
    }

        
        
        
    
    
    
    

        
    
    
    

