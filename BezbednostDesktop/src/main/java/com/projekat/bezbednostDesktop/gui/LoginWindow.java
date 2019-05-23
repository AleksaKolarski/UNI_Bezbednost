package com.projekat.bezbednostDesktop.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class LoginWindow extends JDialog {
	private static final long serialVersionUID = 1L;

	private JLabel labelEmail = new JLabel("Email:");
	private JTextField textEmail = new JTextField(20);
	private JLabel labelPassword = new JLabel("Password:");
	private JTextField textPassword = new JPasswordField(20);
	
	private JButton buttonOk = new JButton("Ok");
	
	
	public LoginWindow(MainWindow mainWindow) {
		super(mainWindow, "Login", true);
		guiInit();
		
		this.buttonOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainWindow.email = textEmail.getText();
				mainWindow.password = textPassword.getText();
				LoginWindow.this.dispose();
				LoginWindow.this.setVisible(false);
			}
		});
	}
	
	private void guiInit() {
		setSize(300,  150);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		
		setLayout(new MigLayout("wrap, al center center", "[][]", ""));
		
		this.add(labelEmail);
		this.add(textEmail);
		this.add(labelPassword);
		this.add(textPassword);
		this.add(buttonOk, "split 2, span, center");
		
		this.getRootPane().setDefaultButton(buttonOk);
	}
}
