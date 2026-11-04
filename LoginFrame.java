/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ds.ecommerce;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

/**
 *
 * @author Rudy Anonuevo
 */
public class LoginFrame extends JFrame implements ActionListener {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;
    private JLabel lbUser, lbPass, lbTitle, lbNew;
    private RegisterFrame registerWindow;

    public LoginFrame() {
        setTitle("Login - E-commerce Platform");
        setSize(350, 310);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        lbTitle = new JLabel("E-commerce Platform Login");
        lbTitle.setBounds(95, 20, 220, 30);
        add(lbTitle);

        lbUser = new JLabel("Username:");
        lbUser.setBounds(30, 70, 80, 25);
        add(lbUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(110, 70, 180, 25);
        add(txtUsername);

        lbPass = new JLabel("Password:");
        lbPass.setBounds(30, 110, 80, 25);
        add(lbPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(110, 110, 180, 25);
        add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(120, 155, 100, 30);
        add(btnLogin);

        lbNew = new JLabel("New customer? Create an account:");
        lbNew.setBounds(80, 205, 220, 25);
        add(lbNew);

        btnRegister = new JButton("Register");
        btnRegister.setBounds(120, 235, 100, 30);
        add(btnRegister);

        btnLogin.addActionListener(this);
        btnRegister.addActionListener(this);
        txtPassword.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRegister) {
            if (registerWindow == null || !registerWindow.isDisplayable()) {
                registerWindow = new RegisterFrame();
                registerWindow.setVisible(true);
            } else {
                registerWindow.toFront();
            }
            return;
        }

        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT user_id, username, name, email, role FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user);
            pstmt.setString(2, pass);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User loggedIn = new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("name"),
                        rs.getString("email"), rs.getString("role"));
                String sessionId = SessionManager.login(loggedIn);
                if (registerWindow != null) registerWindow.dispose();
                this.dispose();
                new DashboardFrame(sessionId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
