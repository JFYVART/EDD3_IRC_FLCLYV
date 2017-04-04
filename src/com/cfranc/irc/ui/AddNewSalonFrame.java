package com.cfranc.irc.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.cfranc.irc.server.Salon;
import com.cfranc.irc.server.SalonLst;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

public class AddNewSalonFrame extends JFrame implements Observable{

	private JTextField textField_NewSalonName;
	//private MouseListener l ;
	public String newSalon;

	public AddNewSalonFrame() {
		super();
		this.initialize();
	}

	@Override
	public void addListener(InvalidationListener listener) {
		// TODO Auto-generated method stub

	}

	public void CreateSalon() {
		this.newSalon = this.textField_NewSalonName.getText();
		System.out.println(" : " + this.newSalon);
		new Salon(this.newSalon, true, SalonLst.DEFAULT_SALON_ID) ;
		this.setVisible(false);
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

		this.textField_NewSalonName = new JTextField();
		panel.add(this.textField_NewSalonName, BorderLayout.CENTER);
		this.textField_NewSalonName.setColumns(50);
		// inputStream
		JLabel lblNewLabel = new JLabel("Salon \u00E0 cr\u00E9er");
		panel.add(lblNewLabel, BorderLayout.WEST);

		JButton btnNewButton = new JButton("+");
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AddNewSalonFrame.this.CreateSalon();
			}
		});
		panel.add(btnNewButton, BorderLayout.EAST);

		this.setVisible(true);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		// TODO Auto-generated method stub

	}




}
