package com.cfranc.irc.ui;

import java.awt.BorderLayout;




import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.cfranc.irc.client.ClientToServerThread;
import com.cfranc.irc.server.Salon;
import com.cfranc.irc.server.User;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableMap;

import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddNewSalonFrame extends JFrame implements Observable{

	private JTextField textField_NewSalonName;
	//private MouseListener l ;
	public String newSalon;
	
	public AddNewSalonFrame() {
		super();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		this.setTitle("Ajout Salon");
		this.setBounds(100, 60, 300, 70);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		this.setContentPane(panel);

		textField_NewSalonName = new JTextField();
		panel.add(textField_NewSalonName, BorderLayout.CENTER);
		textField_NewSalonName.setColumns(50);
		// inputStream
		JLabel lblNewLabel = new JLabel("Salon \u00E0 cr\u00E9er");
		panel.add(lblNewLabel, BorderLayout.WEST);
		
		JButton btnNewButton = new JButton("+");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CreateSalon();
			}
		});
		panel.add(btnNewButton, BorderLayout.EAST);
		
		this.setVisible(true);
	}

	public void CreateSalon() {
		newSalon = textField_NewSalonName.getText();		
		System.out.println(" : " + newSalon);
		new Salon(newSalon, true) ;
		this.setVisible(false);
	}

	@Override
	public void addListener(InvalidationListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		// TODO Auto-generated method stub
		
	}




}
