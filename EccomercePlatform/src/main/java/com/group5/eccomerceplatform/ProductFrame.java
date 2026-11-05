/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecomerceplatformmain;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProductFrame extends JFrame implements ActionListener {
    private final ShopManager manager;
    private DefaultTableModel productModel;
    private JTable productTable;
    private JScrollPane productScroll;
    private JTextField txtPID, txtName, txtCategory, txtPrice, txtStock;
    private JComboBox<String> cmbSort;
    private JLabel lbHeader, lbPID, lbName, lbCategory, lbPrice, lbStock, lbSort, lbHint1, lbHint2;
    private JButton btnAdd, btnUpdate, btnDelete, btnSearch, btnSort;

    ProductFrame(ShopManager manager) {
        this.manager = manager;
        String[] columns = {"Product ID", "Name", "Category", "Price (PHP)", "Stock", "Rating"};
        productModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

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

        lbName = new JLabel("Name:");
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

        refreshAll();
    }

    private void refreshAll() {
        String error = manager.loadDataFromDatabase();
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        }
        showProducts(manager.browse(0, "", "All", cmbSort.getSelectedIndex() + 1));
    }

    private void showProducts(ArrayList<Product> list) {
        productModel.setRowCount(0);
        for (Product p : list) {
            productModel.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), String.format("%,.2f", p.getPrice()),
                p.getStock(), p.getRatingText()});
        }
    }

    private void clearFields() {
        txtPID.setText("");
        txtName.setText("");
        txtCategory.setText("");
        txtPrice.setText("");
        txtStock.setText("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            if (!manager.isAdmin()) return;
            String PID = txtPID.getText().trim();
            String Name = txtName.getText().trim();
            String Category = txtCategory.getText().trim();
            String Price = txtPrice.getText().trim();
            String Stock = txtStock.getText().trim();

            if (!PID.isEmpty() && !Name.isEmpty() && !Category.isEmpty() && !Price.isEmpty() && !Stock.isEmpty()) {
                try {
                    manager.addProduct(Integer.parseInt(PID), Name, Double.parseDouble(Price), Category, Integer.parseInt(Stock));
                    refreshAll();
                    clearFields();
                    JOptionPane.showMessageDialog(this, "Product successfully added and saved to database!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Product ID and Stock must be whole numbers and Price must be a number", "Input Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all product details first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnUpdate) {
            if (!manager.isAdmin()) return;
            int rowSelected = productTable.getSelectedRow();
            if (rowSelected != -1) {
                String Name = txtName.getText().trim();
                String Category = txtCategory.getText().trim();
                String Price = txtPrice.getText().trim();
                String Stock = txtStock.getText().trim();

                if (!Name.isEmpty() && !Category.isEmpty() && !Price.isEmpty() && !Stock.isEmpty()) {
                    try {
                        int id = Integer.parseInt(productModel.getValueAt(rowSelected, 0).toString());
                        if (manager.updateProduct(id, Name, Double.parseDouble(Price), Category, Integer.parseInt(Stock))) {
                            refreshAll();
                            clearFields();
                            JOptionPane.showMessageDialog(this, "Product info has been updated in the database");
                        } else {
                            refreshAll();
                            JOptionPane.showMessageDialog(this, "Product no longer exists.", "Update Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Stock must be a whole number and Price must be a number", "Input Error", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter the new Name, Category, Price and Stock first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a product first before updating", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnDelete) {
            if (!manager.isAdmin()) return;
            int indexSelected = productTable.getSelectedRow();
            if (indexSelected != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Delete the selected product?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        int id = Integer.parseInt(productModel.getValueAt(indexSelected, 0).toString());
                        if (manager.deleteProduct(id)) {
                            refreshAll();
                            JOptionPane.showMessageDialog(this, "Product has been deleted from the database");
                        } else {
                            refreshAll();
                            JOptionPane.showMessageDialog(this, "Product no longer exists.", "Delete Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot Delete", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a product first before deleting", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnSearch) {
            String PID = txtPID.getText().trim();
            String Name = txtName.getText().trim();

            if (!PID.isEmpty() || !Name.isEmpty()) {
                try {
                    manager.loadDataFromDatabase();
                    ArrayList<Product> found = new ArrayList<>();
                    if (!PID.isEmpty()) {
                        Product p = manager.searchById(Integer.parseInt(PID));
                        if (p != null) found.add(p);
                    } else {
                        found = manager.searchByName(Name);
                    }

                    if (!found.isEmpty()) {
                        showProducts(found);
                        JOptionPane.showMessageDialog(this, found.size() + " match(es) found. Click Sort / Refresh / Show All to see every product.");
                    } else {
                        JOptionPane.showMessageDialog(this, "No matching product found!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Product ID must be a whole number", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Enter a Product ID or Name to search", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnSort) {
            refreshAll();
        }
    }
}