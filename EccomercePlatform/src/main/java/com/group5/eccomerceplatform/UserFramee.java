
package com.mycompany.userframe;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class UserFramee  extends JFrame implements ActionListener{
   // private final ShopManager manager;
    private DefaultTableModel userModel;
    private JTable userTable;
    private JScrollPane userScroll;
    private JTextField txtUsername, txtName, txtEmail;
    private JPasswordField txtPassword;
    private JLabel lbUsername, lbPassword, lbName, lbEmail, lbNote, lbSessions;
    private JButton btnAddCustomer, btnUpdatePass, btnDeleteCustomer, btnRefreshUser;

    
 UserFramee(){   
    
    setTitle("Manager User Accounts [Admin Only] ");
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

    
    
    {
    }
    
 }

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
}
    
    
    

