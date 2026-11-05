/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.eccomerceplatform;

import javax.swing.JOptionPane;

public class ECommerce_Platform_Main {

    public static void main(String[] args) {
        String error = DBConnection.initializeDatabase();
        if (error != null) {
            JOptionPane.showMessageDialog(null, error, "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        java.awt.EventQueue.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
