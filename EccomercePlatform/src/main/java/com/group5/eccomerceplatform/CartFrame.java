/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecomerceplatformmain;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author HP ELITEBOOK 840 G8
 */
public class CartFrame extends JFrame implements ActionListener {
    private final ShopManager manager;
    private DefaultTableModel cartModel;
    private JTable cartTable;
    private JScrollPane cartScroll;
    private JTextField txtDetails;
    private JComboBox<String> cmbMethod;
    private JLabel lbTotal, lbMethod, lbDetails, lbHelp;
    private JButton btnRemove, btnClear, btnRefresh, btnPay;

    CartFrame(ShopManager manager) {
        this.manager = manager;
        String[] columns = {"Product ID", "Name", "Unit Price (PHP)", "Quantity", "Subtotal (PHP)"};
        cartModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        setTitle("My Cart & Checkout");
        setSize(800, 470);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        cartTable = new JTable(cartModel);
        cartScroll = new JScrollPane(cartTable);
        cartScroll.setBounds(10, 10, 760, 200);
        add(cartScroll);

        btnRemove = new JButton("Remove Selected");
        btnRemove.setBounds(10, 220, 150, 30);
        add(btnRemove);

        btnClear = new JButton("Clear Cart");
        btnClear.setBounds(170, 220, 120, 30);
        add(btnClear);

        btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(300, 220, 100, 30);
        add(btnRefresh);

        lbTotal = new JLabel("Total: PHP 0.00");
        lbTotal.setBounds(560, 220, 210, 30);
        add(lbTotal);

        lbMethod = new JLabel("Payment Method:");
        lbMethod.setBounds(10, 275, 120, 30);
        add(lbMethod);

        cmbMethod = new JComboBox<>(new String[]{"Cash on Delivery", "Credit Card", "E-Wallet"});
        cmbMethod.setBounds(130, 275, 200, 30);
        add(cmbMethod);

        lbDetails = new JLabel("Card / Wallet No.:");
        lbDetails.setBounds(10, 315, 120, 30);
        add(lbDetails);

        txtDetails = new JTextField();
        txtDetails.setBounds(130, 315, 200, 30);
        add(txtDetails);

        lbHelp = new JLabel("");
        lbHelp.setBounds(345, 295, 420, 30);
        add(lbHelp);

        btnPay = new JButton("Pay Now");
        btnPay.setBounds(130, 365, 200, 35);
        add(btnPay);

        btnRemove.addActionListener(this);
        btnClear.addActionListener(this);
        btnRefresh.addActionListener(this);
        btnPay.addActionListener(this);
        cmbMethod.addActionListener(this);

        updatePaymentFields();
        refreshCart();
    }

    private void refreshCart() {
        cartModel.setRowCount(0);
        for (OrderItem item : manager.getCart()) {
            cartModel.addRow(new Object[]{item.getProductId(), item.getProductName(),
                String.format("%,.2f", item.getUnitPrice()), item.getQuantity(), String.format("%,.2f", item.getSubtotal())});
        }
        lbTotal.setText("Total: PHP " + String.format("%,.2f", manager.getCartTotal()));
    }

    private void updatePaymentFields() {
        String method = cmbMethod.getSelectedItem().toString();
        if ("Credit Card".equals(method)) {
            txtDetails.setEnabled(true);
            lbHelp.setText("Enter the 16-digit card number (it is not saved).");
        } else if ("E-Wallet".equals(method)) {
            txtDetails.setEnabled(true);
            lbHelp.setText("Enter the 11-digit mobile number starting with 09 (not saved).");
        } else {
            txtDetails.setText("");
            txtDetails.setEnabled(false);
            lbHelp.setText("Pay in cash when the order is delivered.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cmbMethod) {
            updatePaymentFields();
        } else if (e.getSource() == btnRefresh) {
            refreshCart();
        } else if (e.getSource() == btnRemove) {
            int rowSelected = cartTable.getSelectedRow();
            if (rowSelected != -1) {
                int id = Integer.parseInt(cartModel.getValueAt(rowSelected, 0).toString());
                manager.removeFromCart(id);
                refreshCart();
            } else {
                JOptionPane.showMessageDialog(this, "Please select an item to remove first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnClear) {
            if (!manager.getCart().isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(this, "Remove all items from your cart?", "Confirm Clear", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    manager.clearCart();
                    refreshCart();
                }
            }
        } else if (e.getSource() == btnPay) {
            if (!manager.isCustomer()) return;
            if (manager.getCart().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your cart is empty", "ERROR Message", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int orderId = manager.checkout(cmbMethod.getSelectedItem().toString(), txtDetails.getText().trim());
                Order placed = manager.findOrder(orderId);
                refreshCart();
                txtDetails.setText("");
                JOptionPane.showMessageDialog(this, "Payment successful! Order #" + orderId + " has been placed.\nTotal paid: PHP "
                        + String.format("%,.2f", placed == null ? 0 : placed.getTotalPrice()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Payment Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
