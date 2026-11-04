/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ds.ecommerce;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * @author Rudy Anonuevo
 */
public class DashboardFrame extends JFrame implements ActionListener {
    private JButton btnShop, btnCart, btnProducts, btnUsers, btnOrders, btnReports, btnLogout;
    private JLabel lblWelcome, lblInfo;
    private final ShopManager manager;
    private ShopFrame shopWindow;
    private CartFrame cartWindow;
    private ProductFrame productWindow;
    private UserFrame userWindow;
    private OrderHistoryFrame orderWindow;
    private ReportFrame reportWindow;

    public DashboardFrame(String sessionId) {
        manager = new ShopManager(sessionId);
        boolean admin = manager.isAdmin();
        String name = manager.getCurrentUser().getName();

        setTitle(admin ? "Admin Dashboard" : "Customer Dashboard");
        setSize(400, admin ? 370 : 320);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        lblWelcome = new JLabel(admin ? "Welcome, Administrator " + name + "!" : "Welcome, " + name + "!");
        lblWelcome.setBounds(90, 20, 320, 25);
        add(lblWelcome);

        lblInfo = new JLabel(admin ? "Products, users, orders and reports"
                                   : "Browse products, add to cart, pay and track orders");
        lblInfo.setBounds(100, 45, 320, 25);
        add(lblInfo);

        btnShop = new JButton("Browse Products");
        btnShop.setBounds(100, 85, 200, 35);
        add(btnShop);

        btnCart = new JButton("My Cart & Checkout");
        btnCart.setBounds(100, 130, 200, 35);
        add(btnCart);

        btnProducts = new JButton("Manage Products");
        btnProducts.setBounds(100, 85, 200, 35);
        add(btnProducts);

        btnUsers = new JButton("Manage Users");
        btnUsers.setBounds(100, 130, 200, 35);
        add(btnUsers);

        btnOrders = new JButton(admin ? "All Orders" : "My Order History");
        btnOrders.setBounds(100, 175, 200, 35);
        add(btnOrders);

        btnReports = new JButton("Reports");
        btnReports.setBounds(100, 220, 200, 35);
        add(btnReports);

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(100, admin ? 265 : 220, 200, 35);
        add(btnLogout);

        if (admin) {
            btnShop.setVisible(false);
            btnCart.setVisible(false);
        } else {
            btnProducts.setVisible(false);
            btnUsers.setVisible(false);
            btnReports.setVisible(false);
        }

        btnShop.addActionListener(this);
        btnCart.addActionListener(this);
        btnProducts.addActionListener(this);
        btnUsers.addActionListener(this);
        btnOrders.addActionListener(this);
        btnReports.addActionListener(this);
        btnLogout.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnShop) {
            if (!manager.isCustomer()) return;
            if (shopWindow == null || !shopWindow.isDisplayable()) {
                shopWindow = new ShopFrame(manager);
                shopWindow.setVisible(true);
            } else {
                shopWindow.toFront();
            }
        } else if (e.getSource() == btnCart) {
            if (!manager.isCustomer()) return;
            if (cartWindow == null || !cartWindow.isDisplayable()) {
                cartWindow = new CartFrame(manager);
                cartWindow.setVisible(true);
            } else {
                cartWindow.toFront();
            }
        } else if (e.getSource() == btnProducts) {
            if (!manager.isAdmin()) return;
            if (productWindow == null || !productWindow.isDisplayable()) {
                productWindow = new ProductFrame(manager);
                productWindow.setVisible(true);
            } else {
                productWindow.toFront();
            }
        } else if (e.getSource() == btnUsers) {
            if (!manager.isAdmin()) return;
            if (userWindow == null || !userWindow.isDisplayable()) {
                userWindow = new UserFrame(manager);
                userWindow.setVisible(true);
            } else {
                userWindow.toFront();
            }
        } else if (e.getSource() == btnOrders) {
            if (orderWindow == null || !orderWindow.isDisplayable()) {
                orderWindow = new OrderHistoryFrame(manager);
                orderWindow.setVisible(true);
            } else {
                orderWindow.toFront();
            }
        } else if (e.getSource() == btnReports) {
            if (!manager.isAdmin()) return;
            if (reportWindow == null || !reportWindow.isDisplayable()) {
                reportWindow = new ReportFrame(manager);
                reportWindow.setVisible(true);
            } else {
                reportWindow.toFront();
            }
        } else if (e.getSource() == btnLogout) {
            manager.logout();
            JFrame[] windows = {shopWindow, cartWindow, productWindow, userWindow, orderWindow, reportWindow};
            for (JFrame w : windows) {
                if (w != null) w.dispose();
            }
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
