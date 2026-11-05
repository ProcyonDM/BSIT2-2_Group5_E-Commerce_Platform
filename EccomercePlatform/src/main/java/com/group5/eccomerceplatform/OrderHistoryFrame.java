/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eccomerceplatform;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class OrderHistoryFrame extends JFrame implements ActionListener {
    private final ShopManager manager;
    private DefaultTableModel orderModel;
    private JTable orderTable;
    private JScrollPane orderScroll, detailScroll;
    private JTextArea txtDetails;
    private JButton btnView, btnRefresh;

    OrderHistoryFrame(ShopManager manager) {
        this.manager = manager;
        String[] columns = {"Order ID", "Customer", "Items", "Total (PHP)", "Payment", "Date"};
        orderModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        setTitle(manager.isAdmin() ? "All Orders [Admin]" : "My Order History");
        setSize(790, 470);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        orderTable = new JTable(orderModel);
        orderScroll = new JScrollPane(orderTable);
        orderScroll.setBounds(10, 10, 760, 200);
        add(orderScroll);

        btnView = new JButton("View Items");
        btnView.setBounds(10, 220, 150, 30);
        add(btnView);

        btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(170, 220, 120, 30);
        add(btnRefresh);

        txtDetails = new JTextArea();
        txtDetails.setEditable(false);
        txtDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailScroll = new JScrollPane(txtDetails);
        detailScroll.setBounds(10, 260, 760, 160);
        add(detailScroll);

        btnView.addActionListener(this);
        btnRefresh.addActionListener(this);

        refreshAll();
    }

    private void refreshAll() {
        String error = manager.loadDataFromDatabase();
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        }
        orderModel.setRowCount(0);
        ArrayList<Order> list = manager.getOrders();
        for (int i = list.size() - 1; i >= 0; i--) {
            Order o = list.get(i);
            User u = manager.getUser(o.getUserId());
            orderModel.addRow(new Object[]{o.getOrderId(), u == null ? "N/A" : u.getName(), o.getItemCount(),
                String.format("%,.2f", o.getTotalPrice()), o.getPaymentMethod(), o.getDate()});
        }
        txtDetails.setText("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRefresh) {
            refreshAll();
        } else if (e.getSource() == btnView) {
            int rowSelected = orderTable.getSelectedRow();
            if (rowSelected != -1) {
                int id = Integer.parseInt(orderModel.getValueAt(rowSelected, 0).toString());
                Order o = manager.findOrder(id);
                if (o != null) {
                    User u = manager.getUser(o.getUserId());
                    StringBuilder sb = new StringBuilder();
                    sb.append("Order #").append(o.getOrderId())
                      .append("  |  Customer: ").append(u == null ? "N/A" : u.getName())
                      .append("  |  ").append(o.getDate())
                      .append("  |  Payment: ").append(o.getPaymentMethod()).append("\n");
                    sb.append("------------------------------------------------------------------\n");
                    for (OrderItem item : o.getProductList()) {
                        sb.append(String.format("%3d x %-28s @ %,12.2f = %,14.2f\n", item.getQuantity(),
                                item.getProductName(), item.getUnitPrice(), item.getSubtotal()));
                    }
                    sb.append("------------------------------------------------------------------\n");
                    sb.append(String.format("TOTAL: PHP %,.2f\n", o.getTotalPrice()));
                    txtDetails.setText(sb.toString());
                    txtDetails.setCaretPosition(0);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an order first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
