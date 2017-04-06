package com.cfranc.irc.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.cfranc.irc.client.ClientToServerThread;
import com.cfranc.irc.client.DefaultListSalonModel;
import com.cfranc.irc.server.SalonLst;

public class SimpleChatClientApp implements Observer {
	static String[] ConnectOptionNames = { "Connect" };
	static String ConnectTitle = "Connection Information";
	public static final String BOLD_ITALIC = "BoldItalic";
	public static final String GRAY_PLAIN = "Gray";
	private static ClientToServerThread clientToServerThread;

	Socket socketClientServer;
	int serverPort;
	String serverName;
	String clientName;

	// HashMap pour chaque salon de ces éléments
	// le bouton envoyer devra récupérer le salon actif pour savoir sur lequel
	// envoyer

	String clientPwd;
	int idSalonActif = SalonLst.DEFAULT_SALON_ID;

	private SimpleChatFrameClient frame;

	public StyledDocument documentModel = new DefaultStyledDocument();

	DefaultListModel<String> clientListModel = new DefaultListModel<String>();

	DefaultListSalonModel salonListModel = new DefaultListSalonModel();

	public static DefaultStyledDocument defaultDocumentModel() {
		DefaultStyledDocument res = new DefaultStyledDocument();

		Style styleDefault = res.getStyle(StyleContext.DEFAULT_STYLE);

		res.addStyle(BOLD_ITALIC, styleDefault);
		Style styleBI = res.getStyle(BOLD_ITALIC);
		StyleConstants.setBold(styleBI, true);
		StyleConstants.setItalic(styleBI, true);
		StyleConstants.setForeground(styleBI, Color.black);

		res.addStyle(GRAY_PLAIN, styleDefault);
		Style styleGP = res.getStyle(GRAY_PLAIN);
		StyleConstants.setBold(styleGP, false);
		StyleConstants.setItalic(styleGP, false);
		StyleConstants.setForeground(styleGP, Color.lightGray);

		return res;
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		final SimpleChatClientApp app = new SimpleChatClientApp();
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					app.displayConnectionDialog();

					app.connectClient();

					app.displayClient();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		Scanner sc = new Scanner(System.in);
		String line = "";
		while (!line.equals(".bye")) {
			line = sc.nextLine();
		}
		sc.close();

		quitApp(app);

	}
	private static void quitApp(final SimpleChatClientApp app) {
		try {
			SimpleChatClientApp.clientToServerThread.quitServer();
			app.hideClient();
			System.out.println("SimpleChatClientApp : fermée");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public SimpleChatClientApp() {

	}

	private void connectClient() {
		System.out.println("Establishing connection. Please wait ...");
		try {
			this.socketClientServer = new Socket(this.serverName, this.serverPort);
			// Start connection services

			clientToServerThread = new ClientToServerThread(this.documentModel, this.clientListModel, this.socketClientServer,
					this.clientName, this.clientPwd, this.salonListModel);

			clientToServerThread.start();

			System.out.println("Connected: " + this.socketClientServer);
		} catch (UnknownHostException uhe) {
			System.out.println("Host unknown: " + uhe.getMessage());
		} catch (IOException ioe) {
			System.out.println("Unexpected exception: " + ioe.getMessage());
		}
	}

	public void displayClient() {

		// Init GUI
		this.frame = new SimpleChatFrameClient(clientToServerThread, this.clientListModel, this.documentModel, this.salonListModel);
		this.frame.setTitle(
				this.frame.getTitle() + " : " + this.clientName + " connected to " + this.serverName + ":" + this.serverPort);
		((JFrame) this.frame).setVisible(true);
		this.frame.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override

			public void windowClosing(WindowEvent e) {
				quitApp(SimpleChatClientApp.this);
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	void displayConnectionDialog() {
		ConnectionPanel connectionPanel = new ConnectionPanel();
		if (JOptionPane.showOptionDialog(null, connectionPanel, ConnectTitle, JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, ConnectOptionNames, ConnectOptionNames[0]) == 0) {
			this.serverPort = Integer.parseInt(connectionPanel.getServerPortField().getText());
			this.serverName = connectionPanel.getServerField().getText();
			this.clientName = connectionPanel.getUserNameField().getText();
			this.clientPwd = connectionPanel.getPasswordField().getText();
		}
	}

	public void hideClient() {

		// Init GUI
		((JFrame) this.frame).setVisible(false);
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}
}
