/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ds.ecommerce;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * @author Rudy Anonuevo
 */
public class ReportFrame extends JFrame implements ActionListener {
    private final ShopManager manager;
    private JTextArea txtReport;
    private JScrollPane reportScroll;
    private JButton btnSales, btnInventory;

    ReportFrame(ShopManager manager) {
        this.manager = manager;
        setTitle("Reports [Admin Only]");
        setSize(760, 480);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        btnSales = new JButton("Sales Report");
        btnSales.setBounds(10, 10, 150, 30);
        add(btnSales);

        btnInventory = new JButton("Inventory Report");
        btnInventory.setBounds(170, 10, 150, 30);
        add(btnInventory);

        txtReport = new JTextArea();
        txtReport.setEditable(false);
        txtReport.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportScroll = new JScrollPane(txtReport);
        reportScroll.setBounds(10, 50, 725, 380);
        add(reportScroll);

        btnSales.addActionListener(this);
        btnInventory.addActionListener(this);

        showReport(true);
    }

    private void showReport(boolean sales) {
        if (!manager.isAdmin()) return;
        String error = manager.loadDataFromDatabase();
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            txtReport.setText(sales ? manager.generateSalesReport() : manager.generateInventoryReport());
            txtReport.setCaretPosition(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSales) {
            showReport(true);
        } else if (e.getSource() == btnInventory) {
            showReport(false);
        }
    }
}
