/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecomerceplatformmain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class ShopManager {

    public static final int SORT_ID = 1;
    public static final int SORT_PRICE_ASC = 2;
    public static final int SORT_PRICE_DESC = 3;
    public static final int SORT_RATING_DESC = 4;
    public static final int SORT_NAME = 5;
    public static final int SORT_STOCK_ASC = 6;
    public static final int LOW_STOCK_LEVEL = 5;

    private final String sessionId;
    private ArrayList<Product> productList;      
    private HashMap<Integer, User> userMap;      
    private ArrayList<Order> orderList;
    private ArrayList<OrderItem> cart;

    public ShopManager(String sessionId) {
        this.sessionId = sessionId;
        productList = new ArrayList<>();
        userMap = new LinkedHashMap<>(); 
        orderList = new ArrayList<>();
        cart = new ArrayList<>();
    }

    public User getCurrentUser() {
        return SessionManager.getUser(sessionId); 
    }

    public boolean isAdmin() {
        User u = getCurrentUser();
        return u != null && "Admin".equalsIgnoreCase(u.getRole());
    }

    public boolean isCustomer() {
        User u = getCurrentUser();
        return u != null && "Customer".equalsIgnoreCase(u.getRole());
    }

    private void requireAdmin() {
        if (!isAdmin()) throw new IllegalStateException("Admin access is required for this action.");
    }

    private void requireCustomer() {
        if (!isCustomer()) throw new IllegalStateException("Only a logged-in customer can do this.");
    }

    public void logout() {
        SessionManager.logout(sessionId);
        cart.clear();
    }

    public ArrayList<Product> getProducts() { return productList; }
    public ArrayList<Order> getOrders() { return orderList; }
    public ArrayList<OrderItem> getCart() { return cart; }
    public Collection<User> getUsers() { return userMap.values(); }
    public User getUser(int id) { return userMap.get(id); }

    public Order findOrder(int orderId) {
        for (Order o : orderList) {
            if (o.getOrderId() == orderId) return o;
        }
        return null;
    }

    private String trimDate(String date) {
        if (date != null && date.length() > 19) return date.substring(0, 19);
        return date;
    }

    public String loadDataFromDatabase() {
        productList.clear();
        userMap.clear();
        orderList.clear();

        User me = getCurrentUser();
        if (me == null) return "Session expired. Please log in again.";
        boolean admin = isAdmin();

        try (Connection conn = DBConnection.getConnection()) {
            String prodSql = "SELECT p.product_id, p.name, p.price, p.category, p.stock, "
                    + "IFNULL(AVG(r.rating), 0) AS avg_rating, COUNT(r.rating) AS rating_count "
                    + "FROM products p LEFT JOIN product_ratings r ON p.product_id = r.product_id "
                    + "GROUP BY p.product_id, p.name, p.price, p.category, p.stock ORDER BY p.product_id";
            try (PreparedStatement pstmt = conn.prepareStatement(prodSql); ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    productList.add(new Product(rs.getInt("product_id"), rs.getString("name"), rs.getDouble("price"),
                            rs.getString("category"), rs.getInt("stock"), rs.getDouble("avg_rating"), rs.getInt("rating_count")));
                }
            }

            String userSql = admin ? "SELECT * FROM users WHERE role = 'Customer' ORDER BY user_id"
                                   : "SELECT * FROM users WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(userSql)) {
                if (!admin) pstmt.setInt(1, me.getId());
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("user_id");
                        userMap.put(id, new User(id, rs.getString("username"), rs.getString("name"),
                                rs.getString("email"), rs.getString("role")));
                    }
                }
            }

            HashMap<Integer, Order> orderById = new HashMap<>();
            String orderSql = admin ? "SELECT * FROM orders ORDER BY order_id"
                                    : "SELECT * FROM orders WHERE user_id = ? ORDER BY order_id";
            try (PreparedStatement pstmt = conn.prepareStatement(orderSql)) {
                if (!admin) pstmt.setInt(1, me.getId());
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Order o = new Order(rs.getInt("order_id"), rs.getInt("user_id"), rs.getDouble("total_price"),
                                rs.getString("payment_method"), trimDate(rs.getString("order_date")));
                        orderList.add(o);
                        orderById.put(o.getOrderId(), o);
                        User owner = userMap.get(o.getUserId());
                        if (owner != null) owner.addOrder(o);
                    }
                }
            }

            String itemSql = admin ? "SELECT * FROM order_items ORDER BY item_id"
                                   : "SELECT oi.* FROM order_items oi JOIN orders o ON oi.order_id = o.order_id "
                                     + "WHERE o.user_id = ? ORDER BY oi.item_id";
            try (PreparedStatement pstmt = conn.prepareStatement(itemSql)) {
                if (!admin) pstmt.setInt(1, me.getId());
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Order o = orderById.get(rs.getInt("order_id"));
                        if (o != null) {
                            o.addItem(new OrderItem(rs.getInt("product_id"), rs.getString("product_name"),
                                    rs.getDouble("unit_price"), rs.getInt("quantity")));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            return "Database Load Error: " + e.getMessage();
        }
        return null;
    }

    private void validateProduct(int id, String name, double price, String category, int stock) {
        if (id <= 0) {
            throw new IllegalArgumentException("Product ID must be greater than zero.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }
        if (Double.isNaN(price) || Double.isInfinite(price) || price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
    }

    public void addProduct(int id, String name, double price, String category, int stock) {
        requireAdmin();
        validateProduct(id, name, price, category, stock);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO products (product_id, name, price, category, stock) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setDouble(3, price);
            pstmt.setString(4, category);
            pstmt.setInt(5, stock);
            pstmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Product ID " + id + " already exists.");
        } catch (SQLException e) {
            throw new RuntimeException("Database error during insert: " + e.getMessage());
        }
        loadDataFromDatabase();
    }

    public boolean updateProduct(int id, String name, double price, String category, int stock) {
        requireAdmin();
        validateProduct(id, name, price, category, stock);
        int affected;

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE products SET name = ?, price = ?, category = ?, stock = ? WHERE product_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setString(3, category);
            pstmt.setInt(4, stock);
            pstmt.setInt(5, id);
            affected = pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database update error: " + e.getMessage());
        }
        loadDataFromDatabase();
        return affected > 0;
    }

    public boolean deleteProduct(int id) {
        requireAdmin();
        int affected;

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM order_items WHERE product_id = ?");
            check.setInt(1, id);
            ResultSet rs = check.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                throw new IllegalArgumentException("Cannot delete this product because it appears in customer orders.");
            }

            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM products WHERE product_id = ?");
            pstmt.setInt(1, id);
            affected = pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database delete error: " + e.getMessage());
        }
        loadDataFromDatabase();
        return affected > 0;
    }

    public Product searchById(int id) {
        int low = 0;
        int high = productList.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            Product p = productList.get(mid);
            if (p.getId() == id) return p;
            if (p.getId() < id) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    public ArrayList<Product> searchByName(String text) {
        ArrayList<Product> sorted = mergeSort(new ArrayList<>(productList), SORT_NAME);
        ArrayList<Product> result = new ArrayList<>();
        String key = text.trim().toLowerCase();

        int low = 0;
        int high = sorted.size() - 1;
        int first = sorted.size();
        while (low <= high) {
            int mid = (low + high) / 2;
            if (sorted.get(mid).getName().compareToIgnoreCase(key) >= 0) {
                first = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        for (int i = first; i < sorted.size() && sorted.get(i).getName().toLowerCase().startsWith(key); i++) {
            result.add(sorted.get(i));
        }
        return result;
    }

    public Collection<String> getCategories() {
        TreeSet<String> categories = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Product p : productList) {
            categories.add(p.getCategory().trim());
        }
        return categories;
    }

    public ArrayList<Product> browse(int searchBy, String text, String category, int sortMode) {
        ArrayList<Product> base;
        if (text == null || text.trim().isEmpty()) {
            base = new ArrayList<>(productList);
        } else if (searchBy == 0) {
            base = searchByName(text);
        } else {
            base = new ArrayList<>();
            Product p = searchById(Integer.parseInt(text.trim()));
            if (p != null) base.add(p);
        }

        ArrayList<Product> filtered = new ArrayList<>();
        for (Product p : base) {
            if (category == null || "All".equals(category) || p.getCategory().equalsIgnoreCase(category)) {
                filtered.add(p);
            }
        }
        return mergeSort(filtered, sortMode);
    }

    private OrderItem findCartItem(int productId) {
        for (OrderItem item : cart) {
            if (item.getProductId() == productId) return item;
        }
        return null;
    }

    public void addToCart(Product p, int qty) {
        requireCustomer();
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        OrderItem existing = findCartItem(p.getId());
        int already = (existing == null) ? 0 : existing.getQuantity();
        if (already + qty > p.getStock()) {
            throw new IllegalArgumentException("Not enough stock. Available: " + p.getStock()
                    + (already > 0 ? " (you already have " + already + " in your cart)" : ""));
        }
        if (existing == null) {
            cart.add(new OrderItem(p.getId(), p.getName(), p.getPrice(), qty));
        } else {
            existing.setQuantity(already + qty);
        }
    }

    public void removeFromCart(int productId) {
        OrderItem item = findCartItem(productId);
        if (item != null) cart.remove(item);
    }

    public void clearCart() {
        cart.clear();
    }

    public double getCartTotal() {
        double total = 0;
        for (OrderItem item : cart) {
            total += item.getSubtotal();
        }
        return total;
    }

    private void validatePayment(String method, String details) {
        String d = (details == null) ? "" : details.replace(" ", "");
        if ("Credit Card".equals(method)) {
            if (!d.matches("\\d{16}")) {
                throw new IllegalArgumentException("Credit card number must be exactly 16 digits.");
            }
        } else if ("E-Wallet".equals(method)) {
            if (!d.matches("09\\d{9}")) {
                throw new IllegalArgumentException("E-Wallet number must be an 11-digit mobile number starting with 09.");
            }
        } else if (!"Cash on Delivery".equals(method)) {
            throw new IllegalArgumentException("Please choose a payment method.");
        }
    }

    public int checkout(String method, String details) {
        requireCustomer();
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Your cart is empty.");
        }
        validatePayment(method, details);
        int orderId = 0;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                double total = 0;
                ArrayList<OrderItem> finalItems = new ArrayList<>();
                for (OrderItem item : cart) {
                    PreparedStatement ps = conn.prepareStatement("SELECT name, price, stock FROM products WHERE product_id = ? FOR UPDATE");
                    ps.setInt(1, item.getProductId());
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        throw new IllegalArgumentException("'" + item.getProductName() + "' is no longer available. Remove it from your cart.");
                    }
                    if (rs.getInt("stock") < item.getQuantity()) {
                        throw new IllegalArgumentException("Not enough stock for '" + rs.getString("name") + "'. Available: " + rs.getInt("stock"));
                    }
                    OrderItem fin = new OrderItem(item.getProductId(), rs.getString("name"), rs.getDouble("price"), item.getQuantity());
                    total += fin.getSubtotal();
                    finalItems.add(fin);
                }

                PreparedStatement po = conn.prepareStatement("INSERT INTO orders (user_id, total_price, payment_method) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                po.setInt(1, getCurrentUser().getId());
                po.setDouble(2, total);
                po.setString(3, method);
                po.executeUpdate();
                ResultSet keys = po.getGeneratedKeys();
                keys.next();
                orderId = keys.getInt(1);

                for (OrderItem fin : finalItems) {
                    PreparedStatement pi = conn.prepareStatement("INSERT INTO order_items (order_id, product_id, product_name, unit_price, quantity) VALUES (?, ?, ?, ?, ?)");
                    pi.setInt(1, orderId);
                    pi.setInt(2, fin.getProductId());
                    pi.setString(3, fin.getProductName());
                    pi.setDouble(4, fin.getUnitPrice());
                    pi.setInt(5, fin.getQuantity());
                    pi.executeUpdate();

                    PreparedStatement pu = conn.prepareStatement("UPDATE products SET stock = stock - ? WHERE product_id = ? AND stock >= ?");
                    pu.setInt(1, fin.getQuantity());
                    pu.setInt(2, fin.getProductId());
                    pu.setInt(3, fin.getQuantity());
                    if (pu.executeUpdate() == 0) {
                        throw new IllegalArgumentException("Not enough stock for '" + fin.getProductName() + "'.");
                    }
                }
                conn.commit();
            } catch (SQLException | RuntimeException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during checkout: " + e.getMessage());
        }
        cart.clear();
        loadDataFromDatabase();
        return orderId;
    }

    public void rateProduct(int productId, int rating) {
        requireCustomer();
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be from 1 to 5.");
        }
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM order_items oi JOIN orders o ON oi.order_id = o.order_id "
                    + "WHERE o.user_id = ? AND oi.product_id = ?");
            check.setInt(1, getCurrentUser().getId());
            check.setInt(2, productId);
            ResultSet rs = check.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                throw new IllegalArgumentException("You can only rate products you have purchased.");
            }

            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO product_ratings (user_id, product_id, rating) VALUES (?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE rating = ?");
            pstmt.setInt(1, getCurrentUser().getId());
            pstmt.setInt(2, productId);
            pstmt.setInt(3, rating);
            pstmt.setInt(4, rating);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database error saving rating: " + e.getMessage());
        }
        loadDataFromDatabase();
    }

    public static void registerCustomer(String username, String password, String name, String email) {
        if (username == null || !username.matches("[A-Za-z0-9_]{3,30}")) {
            throw new IllegalArgumentException("Username must be 3-30 letters, numbers or underscores.");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (email == null || !email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (username, password, name, email, role) VALUES (?, ?, ?, ?, 'Customer')");
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, name.trim());
            pstmt.setString(4, email.trim());
            pstmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Username '" + username + "' already exists.");
        } catch (SQLException e) {
            throw new RuntimeException("Database error creating account: " + e.getMessage());
        }
    }

    public void addCustomer(String username, String password, String name, String email) {
        requireAdmin();
        registerCustomer(username, password, name, email);
        loadDataFromDatabase();
    }

    public boolean changePassword(int userId, String newPassword) {
        requireAdmin();
        if (newPassword == null || newPassword.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters.");
        }
        int affected;
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET password = ? WHERE user_id = ? AND role = 'Customer'");
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            affected = pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database error updating password: " + e.getMessage());
        }
        return affected > 0;
    }

    public boolean deleteCustomer(int userId) {
        requireAdmin();
        int affected;
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM orders WHERE user_id = ?");
            check.setInt(1, userId);
            ResultSet rs = check.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                throw new IllegalArgumentException("Cannot delete this customer because they have order history.");
            }

            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE user_id = ? AND role = 'Customer'");
            pstmt.setInt(1, userId);
            affected = pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database error deleting customer: " + e.getMessage());
        }
        loadDataFromDatabase();
        return affected > 0;
    }

    private int compare(Product a, Product b, int mode) {
        switch (mode) {
            case SORT_PRICE_ASC:
                return Double.compare(a.getPrice(), b.getPrice());
            case SORT_PRICE_DESC:
                return Double.compare(b.getPrice(), a.getPrice());
            case SORT_RATING_DESC:
                return Double.compare(b.getRating(), a.getRating());
            case SORT_NAME:
                return a.getName().compareToIgnoreCase(b.getName());
            case SORT_STOCK_ASC:
                return Integer.compare(a.getStock(), b.getStock());
            default:
                return Integer.compare(a.getId(), b.getId());
        }
    }

    private ArrayList<Product> mergeSort(ArrayList<Product> list, int mode) {
        if (list.size() <= 1) return list;

        int mid = list.size() / 2;
        ArrayList<Product> left = mergeSort(new ArrayList<>(list.subList(0, mid)), mode);
        ArrayList<Product> right = mergeSort(new ArrayList<>(list.subList(mid, list.size())), mode);

        ArrayList<Product> merged = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (i < left.size() && j < right.size()) {
            if (compare(left.get(i), right.get(j), mode) <= 0) {
                merged.add(left.get(i++));
            } else {
                merged.add(right.get(j++));
            }
        }
        while (i < left.size()) merged.add(left.get(i++));
        while (j < right.size()) merged.add(right.get(j++));
        return merged;
    }

    private ArrayList<Order> insertionSortOrders(ArrayList<Order> source) {
        ArrayList<Order> list = new ArrayList<>(source);
        for (int i = 1; i < list.size(); i++) {
            Order key = list.get(i);
            int j = i - 1;
            while (j >= 0 && list.get(j).getTotalPrice() < key.getTotalPrice()) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
        return list;
    }

    private ArrayList<double[]> insertionSortByUnits(ArrayList<double[]> source) {
        ArrayList<double[]> list = new ArrayList<>(source);
        for (int i = 1; i < list.size(); i++) {
            double[] key = list.get(i);
            int j = i - 1;
            while (j >= 0 && list.get(j)[1] < key[1]) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
        return list;
    }

    private String fit(String text, int width) {
        if (text == null) return "";
        return text.length() <= width ? text : text.substring(0, width - 1) + "~";
    }

    public String generateSalesReport() {
        requireAdmin();
        if (orderList.isEmpty()) {
            return "No orders have been placed yet.";
        }

        double revenue = 0;
        int units = 0;
        TreeMap<String, double[]> byCategory = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        HashMap<Integer, double[]> byProduct = new HashMap<>();

        for (Order o : orderList) {
            revenue += o.getTotalPrice();
            for (OrderItem it : o.getProductList()) {
                units += it.getQuantity();

                double[] ps = byProduct.get(it.getProductId());
                if (ps == null) {
                    ps = new double[2];
                    byProduct.put(it.getProductId(), ps);
                }
                ps[0] += it.getQuantity();
                ps[1] += it.getSubtotal();

                Product p = searchById(it.getProductId());
                String cat = (p == null) ? "(deleted)" : p.getCategory().trim();
                double[] cs = byCategory.get(cat);
                if (cs == null) {
                    cs = new double[2];
                    byCategory.put(cat, cs);
                }
                cs[0] += it.getQuantity();
                cs[1] += it.getSubtotal();
            }
        }

        StringBuilder report = new StringBuilder("=== SALES REPORT ===\n\n");
        report.append("-- Summary --\n");
        report.append("Total Orders  : ").append(orderList.size()).append("\n");
        report.append("Items Sold    : ").append(units).append("\n");
        report.append("Total Revenue : PHP ").append(String.format("%,.2f", revenue)).append("\n");
        report.append("Average Order : PHP ").append(String.format("%,.2f", revenue / orderList.size())).append("\n\n");

        report.append("-- Sales by Category --\n");
        for (Map.Entry<String, double[]> entry : byCategory.entrySet()) {
            report.append(String.format("%-24s %5d unit(s)   PHP %,14.2f\n",
                    fit(entry.getKey(), 24), (int) entry.getValue()[0], entry.getValue()[1]));
        }

        ArrayList<double[]> entries = new ArrayList<>();
        for (Map.Entry<Integer, double[]> entry : byProduct.entrySet()) {
            entries.add(new double[]{entry.getKey(), entry.getValue()[0], entry.getValue()[1]});
        }
        ArrayList<double[]> top = insertionSortByUnits(entries);
        report.append("\n-- Top 5 Best-Selling Products (by units) --\n");
        for (int i = 0; i < top.size() && i < 5; i++) {
            Product p = searchById((int) top.get(i)[0]);
            String name = (p == null) ? "Product #" + (int) top.get(i)[0] : p.getName();
            report.append(String.format("%d. %-28s %5d unit(s)   PHP %,14.2f\n",
                    i + 1, fit(name, 28), (int) top.get(i)[1], top.get(i)[2]));
        }

        report.append("\n-- All Orders (highest total first) --\n");
        report.append(String.format("%-7s %-20s %-6s %15s  %-17s %s\n", "Order", "Customer", "Items", "Total (PHP)", "Payment", "Date"));
        for (Order o : insertionSortOrders(orderList)) {
            User u = userMap.get(o.getUserId());
            report.append(String.format("%-7d %-20s %-6d %,15.2f  %-17s %s\n", o.getOrderId(),
                    fit(u == null ? "N/A" : u.getName(), 20), o.getItemCount(), o.getTotalPrice(),
                    o.getPaymentMethod(), o.getDate()));
        }
        return report.toString();
    }

    public String generateInventoryReport() {
        requireAdmin();
        if (productList.isEmpty()) {
            return "No products to report yet. Add products first.";
        }

        int totalUnits = 0;
        int outOfStock = 0;
        int lowStock = 0;
        double value = 0;
        for (Product p : productList) {
            totalUnits += p.getStock();
            value += p.getPrice() * p.getStock();
            if (p.getStock() == 0) {
                outOfStock++;
            } else if (p.getStock() <= LOW_STOCK_LEVEL) {
                lowStock++;
            }
        }

        StringBuilder report = new StringBuilder("=== INVENTORY REPORT (lowest stock first) ===\n\n");
        report.append("-- Summary --\n");
        report.append("Total Products    : ").append(productList.size()).append("\n");
        report.append("Units in Stock    : ").append(totalUnits).append("\n");
        report.append("Inventory Value   : PHP ").append(String.format("%,.2f", value)).append("\n");
        report.append("Out of Stock      : ").append(outOfStock).append("\n");
        report.append("Low Stock (<= ").append(LOW_STOCK_LEVEL).append(")  : ").append(lowStock).append("\n\n");

        report.append("-- Stock Listing --\n");
        report.append(String.format("%-6s %-26s %-16s %6s %14s  %s\n", "ID", "Product", "Category", "Stock", "Price (PHP)", "Status"));
        for (Product p : mergeSort(new ArrayList<>(productList), SORT_STOCK_ASC)) {
            String status = (p.getStock() == 0) ? "OUT OF STOCK" : (p.getStock() <= LOW_STOCK_LEVEL ? "LOW" : "OK");
            report.append(String.format("%-6d %-26s %-16s %6d %,14.2f  %s\n", p.getId(), fit(p.getName(), 26),
                    fit(p.getCategory(), 16), p.getStock(), p.getPrice(), status));
        }
        return report.toString();
    }
}
