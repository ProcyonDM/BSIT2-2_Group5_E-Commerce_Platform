/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ds.ecommerce;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Rudy Anonuevo
 */
public class UserFrame extends JFrame implements ActionListener {
    private final ShopManager manager;
    private DefaultTableModel userModel;
    private JTable userTable;
    private JScrollPane userScroll;
    private JTextField txtUsername, txtName, txtEmail;
    private JPasswordField txtPassword;
    private JLabel lbUsername, lbPassword, lbName, lbEmail, lbNote, lbSessions;
    private JButton btnAddCustomer, btnUpdatePass, btnDeleteCustomer, btnRefreshUser;

    UserFrame(ShopManager manager) {
        this.manager = manager;
        String[] columns = {"User ID", "Username", "Name", "Email", "Orders"};
        userModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        setTitle("Manage User Accounts [Admin Only]");
        setSize(790, 440);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        userTable = new JTable(userModel);
        userScroll = new JScrollPane(userTable);
        userScroll.setBounds(10, 10, 760, 200);
        add(userScroll);

        lbUsername = new JLabel("Username:");
        lbUsername.setBounds(10, 225, 80, 30);
        add(lbUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(90, 225, 160, 30);
        add(txtUsername);

        lbPassword = new JLabel("Password:");
        lbPassword.setBounds(270, 225, 70, 30);
        add(lbPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(340, 225, 160, 30);
        add(txtPassword);

        lbName = new JLabel("Name:");
        lbName.setBounds(10, 265, 80, 30);
        add(lbName);

        txtName = new JTextField();
        txtName.setBounds(90, 265, 160, 30);
        add(txtName);

        lbEmail = new JLabel("Email:");
        lbEmail.setBounds(270, 265, 70, 30);
        add(lbEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(340, 265, 160, 30);
        add(txtEmail);

        lbSessions = new JLabel("");
        lbSessions.setBounds(530, 225, 240, 30);
        add(lbSessions);

        btnAddCustomer = new JButton("Add Customer");
        btnAddCustomer.setBounds(10, 315, 130, 30);
        add(btnAddCustomer);

        btnUpdatePass = new JButton("Change Password");
        btnUpdatePass.setBounds(150, 315, 150, 30);
        add(btnUpdatePass);

        btnDeleteCustomer = new JButton("Delete Customer");
        btnDeleteCustomer.setBounds(310, 315, 140, 30);
        add(btnDeleteCustomer);

        btnRefreshUser = new JButton("Refresh");
        btnRefreshUser.setBounds(460, 315, 100, 30);
        add(btnRefreshUser);

        lbNote = new JLabel("Customers can also register themselves on the login screen. Select a row to change password / delete.");
        lbNote.setBounds(10, 365, 760, 20);
        add(lbNote);

        btnAddCustomer.addActionListener(this);
        btnUpdatePass.addActionListener(this);
        btnDeleteCustomer.addActionListener(this);
        btnRefreshUser.addActionListener(this);

        refreshUserTable();
    }

    private void refreshUserTable() {
        String error = manager.loadDataFromDatabase();
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        }
        userModel.setRowCount(0);
        for (User u : manager.getUsers()) {
            userModel.addRow(new Object[]{u.getId(), u.getUsername(), u.getName(), u.getEmail(), u.getOrderHistory().size()});
        }
        lbSessions.setText("Active sessions: " + SessionManager.activeCount());
    }

    private void clearUserFields() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtName.setText("");
        txtEmail.setText("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!manager.isAdmin()) return;

        if (e.getSource() == btnAddCustomer) {
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword()).trim();
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            if (!user.isEmpty() && !pass.isEmpty() && !name.isEmpty() && !email.isEmpty()) {
                try {
                    manager.addCustomer(user, pass, name, email);
                    refreshUserTable();
                    clearUserFields();
                    JOptionPane.showMessageDialog(this, "Customer account created successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter the username, password, name and email", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnUpdatePass) {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow != -1) {
                String pass = new String(txtPassword.getPassword()).trim();
                if (pass.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter the new password first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        int id = Integer.parseInt(userModel.getValueAt(selectedRow, 0).toString());
                        if (manager.changePassword(id, pass)) {
                            clearUserFields();
                            JOptionPane.showMessageDialog(this, "Password updated successfully!");
                        } else {
                            refreshUserTable();
                            JOptionPane.showMessageDialog(this, "Account no longer exists.", "Update Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a customer account from the table first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnDeleteCustomer) {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Delete the selected customer account?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        int id = Integer.parseInt(userModel.getValueAt(selectedRow, 0).toString());
                        manager.deleteCustomer(id);
                        refreshUserTable();
                        JOptionPane.showMessageDialog(this, "Customer account deleted from database");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot Delete", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a customer account from the table first", "ERROR Message", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnRefreshUser) {
            refreshUserTable();
        }
    }
}
