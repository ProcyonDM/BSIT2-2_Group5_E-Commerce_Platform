/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecomerceplatformmain;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class RegisterFrame extends JFrame implements ActionListener {
    private JTextField txtUsername, txtName, txtEmail;
    private JPasswordField txtPassword, txtConfirm;
    private JLabel lbUsername, lbPassword, lbConfirm, lbName, lbEmail;
    private JButton btnRegister, btnClose;

    RegisterFrame() {
        setTitle("Register - New Customer");
        setSize(380, 330);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        lbUsername = new JLabel("Username:");
        lbUsername.setBounds(20, 20, 110, 30);
        add(lbUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(140, 20, 200, 30);
        add(txtUsername);

        lbPassword = new JLabel("Password:");
        lbPassword.setBounds(20, 60, 110, 30);
        add(lbPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(140, 60, 200, 30);
        add(txtPassword);

        lbConfirm = new JLabel("Confirm Password:");
        lbConfirm.setBounds(20, 100, 110, 30);
        add(lbConfirm);

        txtConfirm = new JPasswordField();
        txtConfirm.setBounds(140, 100, 200, 30);
        add(txtConfirm);

        lbName = new JLabel("Full Name:");
        lbName.setBounds(20, 140, 110, 30);
        add(lbName);

        txtName = new JTextField();
        txtName.setBounds(140, 140, 200, 30);
        add(txtName);

        lbEmail = new JLabel("Email:");
        lbEmail.setBounds(20, 180, 110, 30);
        add(lbEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(140, 180, 200, 30);
        add(txtEmail);

        btnRegister = new JButton("Register");
        btnRegister.setBounds(60, 235, 110, 30);
        add(btnRegister);

        btnClose = new JButton("Close");
        btnClose.setBounds(190, 235, 110, 30);
        add(btnClose);

        btnRegister.addActionListener(this);
        btnClose.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnClose) {
            this.dispose();
        } else if (e.getSource() == btnRegister) {
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword()).trim();
            String confirm = new String(txtConfirm.getPassword()).trim();
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();

            if (user.isEmpty() || pass.isEmpty() || name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            } else if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    ShopManager.registerCustomer(user, pass, name, email);
                    JOptionPane.showMessageDialog(this, "Account created! You can now log in.");
                    this.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
