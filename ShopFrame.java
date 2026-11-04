/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ds.ecommerce;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Rudy Anonuevo
 */
public class ShopFrame extends JFrame implements ActionListener {
    private final ShopManager manager;
    private DefaultTableModel productModel;
    private JTable productTable;
    private JScrollPane productScroll;
    private JTextField txtSearch, txtQty;
    private JComboBox<String> cmbSearchBy, cmbCategory, cmbSort, cmbRating;
    private JLabel lbSearch, lbCategory, lbSort, lbQty, lbRate, lbStatus, lbHint;
    private JButton btnApply, btnReset, btnAddCart, btnRate, btnRefresh;

    ShopFrame(ShopManager manager) {
        this.manager = manager;
        String[] columns = {"Product ID", "Name", "Category", "Price (PHP)", "Stock", "Rating"};
        productModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        setTitle("Browse Products");
        setSize(1000, 510);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        lbSearch = new JLabel("Search by:");
        lbSearch.setBounds(10, 10, 70, 30);
        add(lbSearch);

        cmbSearchBy = new JComboBox<>(new String[]{"Name", "Product ID"});
        cmbSearchBy.setBounds(80, 10, 100, 30);
        add(cmbSearchBy);

        txtSearch = new JTextField();
        txtSearch.setBounds(190, 10, 180, 30);
        add(txtSearch);

        lbCategory = new JLabel("Category:");
        lbCategory.setBounds(385, 10, 60, 30);
        add(lbCategory);

        cmbCategory = new JComboBox<>();
        cmbCategory.setBounds(445, 10, 130, 30);
        add(cmbCategory);

        lbSort = new JLabel("Sort by:");
        lbSort.setBounds(590, 10, 55, 30);
        add(lbSort);

        cmbSort = new JComboBox<>(new String[]{"Product ID", "Price (Low to High)", "Price (High to Low)", "Rating (Best First)", "Name (A-Z)"});
        cmbSort.setBounds(645, 10, 160, 30);
        add(cmbSort);

        btnApply = new JButton("Apply");
        btnApply.setBounds(815, 10, 80, 30);
        add(btnApply);

        btnReset = new JButton("Reset");
        btnReset.setBounds(900, 10, 80, 30);
        add(btnReset);

        productTable = new JTable(productModel);
        productScroll = new JScrollPane(productTable);
        productScroll.setBounds(10, 55, 970, 300);
        add(productScroll);

        lbQty = new JLabel("Quantity:");
        lbQty.setBounds(10, 370, 70, 30);
        add(lbQty);

        txtQty = new JTextField("1");
        txtQty.setBounds(80, 370, 60, 30);
        add(txtQty);

        btnAddCart = new JButton("Add to Cart");
        btnAddCart.setBounds(150, 370, 130, 30);
        add(btnAddCart);

        lbRate = new JLabel("Rate (1-5):");
        lbRate.setBounds(330, 370, 70, 30);
        add(lbRate);

        cmbRating = new JComboBox<>(new String[]{"5", "4", "3", "2", "1"});
        cmbRating.setBounds(400, 370, 60, 30);
        add(cmbRating);

        btnRate = new JButton("Rate Selected");
        btnRate.setBounds(470, 370, 130, 30);
        add(btnRate);

        btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(650, 370, 100, 30);
        add(btnRefresh);

        lbStatus = new JLabel("");
        lbStatus.setBounds(10, 415, 500, 25);
        add(lbStatus);

        lbHint = new JLabel("Select a product row, then Add to Cart. You can only rate products you have purchased.");
        lbHint.setBounds(10, 440, 700, 25);
        add(lbHint);

        btnApply.addActionListener(this);
        btnReset.addActionListener(this);
        btnAddCart.addActionListener(this);
        btnRate.addActionListener(this);
        btnRefresh.addActionListener(this);
        txtSearch.addActionListener(this);

        refreshAll();
    }

    private void refreshAll() {
        String error = manager.loadDataFromDatabase();
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        }
        loadCategories();
        applyFilters();
    }

    private void loadCategories() {
        Object selected = cmbCategory.getSelectedItem();
        cmbCategory.removeAllItems();
        cmbCategory.addItem("All");
        for (String c : manager.getCategories()) {
            cmbCategory.addItem(c);
        }
        if (selected != null) cmbCategory.setSelectedItem(selected);
    }

    private void applyFilters() {
        try {
            ArrayList<Product> list = manager.browse(cmbSearchBy.getSelectedIndex(), txtSearch.getText(),
                    cmbCategory.getSelectedItem().toString(), cmbSort.getSelectedIndex() + 1);
            productModel.setRowCount(0);
            for (Product p : list) {
                productModel.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), String.format("%,.2f", p.getPrice()),
                    p.getStock() == 0 ? "Out of stock" : String.valueOf(p.getStock()), p.getRatingText()});
            }
            lbStatus.setText("Showing " + list.size() + " product(s).");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Product ID must be a whole number", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnApply || e.getSource() == txtSearch) {
            applyFilters();
        } else if (e.getSource() == btnReset) {
            txtSearch.setText("");
            cmbSearchBy.setSelectedIndex(0);
            cmbCategory.setSelectedIndex(0);
            cmbSort.setSelectedIndex(0);
            applyFilters();
        } else if (e.getSource() == btnRefresh) {
            refreshAll();
        } else if (e.getSource() == btnAddCart) {
            if (!manager.isCustomer()) return;
            int rowSelected = productTable.getSelectedRow();
            if (rowSelected != -1) {
                try {
                    int id = Integer.parseInt(productModel.getValueAt(rowSelected, 0).toString());
                    int qty = Integer.parseInt(txtQty.getText().trim());
                    Product p = manager.searchById(id);
                    if (p == null) {
                        JOptionPane.showMessageDialog(this, "This product no longer exists. Please refresh.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        manager.addToCart(p, qty);
                        txtQty.setText("1");
                        JOptionPane.showMessageDialog(this, qty + " x " + p.getName() + " added to your cart!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Quantity must be a whole number", "Input Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot Add", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a product first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnRate) {
            if (!manager.isCustomer()) return;
            int rowSelected = productTable.getSelectedRow();
            if (rowSelected != -1) {
                try {
                    int id = Integer.parseInt(productModel.getValueAt(rowSelected, 0).toString());
                    int rating = Integer.parseInt(cmbRating.getSelectedItem().toString());
                    manager.rateProduct(id, rating);
                    applyFilters();
                    JOptionPane.showMessageDialog(this, "Thank you! Your rating has been saved.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot Rate", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a product to rate first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
