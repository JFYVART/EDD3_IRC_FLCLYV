package com.cfranc.irc.client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

import com.cfranc.irc.ClientServerProtocol;
import com.cfranc.irc.ui.SimpleChatClientApp;

public class ClientToServerThread extends Thread implements IfSenderModel {
	private Socket socket = null;
	private DataOutputStream streamOut = null;
	private DataInputStream streamIn = null;
	private BufferedReader console = null;
	String login, pwd;
	DefaultListModel<String> clientListModel;
	StyledDocument documentModel;

	public ClientToServerThread(StyledDocument documentModel, DefaultListModel<String> clientListModel, Socket socket,
			String login, String pwd) {
		super();
		this.documentModel = documentModel;
		this.clientListModel = clientListModel;
		this.socket = socket;
		this.login = login;
		this.pwd = pwd;
	}

	public void open() throws IOException {
		console = new BufferedReader(new InputStreamReader(System.in));
		streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		streamOut = new DataOutputStream(socket.getOutputStream());
	}

	public void close() throws IOException {
		if (socket != null)
			socket.close();
		if (streamIn != null)
			streamIn.close();
		if (streamOut != null)
			streamOut.close();
	}

	public void receiveMessage(String user, String line) {
		Style styleBI = ((StyledDocument) documentModel).getStyle(SimpleChatClientApp.BOLD_ITALIC);
		Style styleGP = ((StyledDocument) documentModel).getStyle(SimpleChatClientApp.GRAY_PLAIN);
		receiveMessage(user, line, styleBI, styleGP);
	}

	public void receiveMessage(String user, String line, Style styleBI, Style styleGP) {
		try {
			documentModel.insertString(documentModel.getLength(), user + " : ", styleBI);
			documentModel.insertString(documentModel.getLength(), line + "\n", styleGP);
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	void readMsg() throws IOException {
		String line = streamIn.readUTF();
		System.out.println(line);

		// Nouveau protocole : On décode la communication qui vient d'arriver 
		String loginUtilisateur = ClientServerProtocol.decodeProtocole_Login(line);
		String msg = ClientServerProtocol.decodeProtocole_Message(line);
		String commande = ClientServerProtocol.decodeProtocole_Command(line);

		if (commande.equals(ClientServerProtocol.ADD)) {
			if (!clientListModel.contains(loginUtilisateur)) {
				clientListModel.addElement(loginUtilisateur);
				receiveMessage(loginUtilisateur, " entre dans le salon...");
			}
		} else if (commande.equals(ClientServerProtocol.DEL)) {
			if (clientListModel.contains(loginUtilisateur)) {
				clientListModel.removeElement(loginUtilisateur);
				receiveMessage(loginUtilisateur, " quite le salon !");
			}
		} else {
			receiveMessage(loginUtilisateur, msg);
		}

	}

	String msgToSend = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfranc.irc.client.IfSenderModel#setMsgToSend(java.lang.String)
	 */
	@Override
	public void setMsgToSend(String msgToSend) {
		this.msgToSend = msgToSend;
	}

	private boolean sendMsg() throws IOException {
		boolean res = false;
		if (msgToSend != null) {
			// Nouveau protocole : on envoie le message de l'utilisateur courant
			String line = ClientServerProtocol.encodeProtocole_Ligne(login, "", msgToSend, "", 0, "");
			streamOut.writeUTF(line);
			msgToSend = null;
			streamOut.flush();
			res = true;
		}
		return res;
	}

	public void quitServer() throws IOException {
		// Nouveau protocole : On signale que l'utilisateur courant s'en va
		String line = ClientServerProtocol.encodeProtocole_Ligne(login, "", "", ClientServerProtocol.DEL, 0, "");
		streamOut.writeUTF(line);
		streamOut.flush();
		done = true;
	}

	boolean done;

	@Override
	public void run() {
		try {
			open();
			done = !authentification();
			while (!done) {
				try {
					if (streamIn.available() > 0) {
						readMsg();
					}

					if (!sendMsg()) {
						Thread.sleep(100);
					}
				} catch (IOException | InterruptedException ioe) {
					ioe.printStackTrace();
					done = true;
				}
			}
			close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean authentification() {
		boolean res = false;
		String loginPwdQ;

		// Nouveau protocole :
		String line = "";
		String commande = "";
		try{
			while (streamIn.available() <= 0) {
				Thread.sleep(100);
			}
			loginPwdQ = streamIn.readUTF();
			// Nouveau protocole : On décode la commande du serveur
			commande = ClientServerProtocol.decodeProtocole_Command(loginPwdQ);
			// Si la commande est une demande d'identification (Login et PWD)  
			if (commande.equals(ClientServerProtocol.LOGIN_PWD)) {
				// On renvoie le login et pwd de la fenetre de connexion.
				line = ClientServerProtocol.encodeProtocole_Ligne(this.login, this.pwd, "", "", 0, "");
				streamOut.writeUTF(line);
			}
			while (streamIn.available() <= 0) {
				Thread.sleep(100);
			}
			String acq = streamIn.readUTF();
			// Nouveau protocole : On décode la réponse du serveur
			commande = ClientServerProtocol.decodeProtocole_Command(acq);
			// Si la commande est une réponse positive
			if (commande.equals(ClientServerProtocol.OK)) {
				res = true;
			}
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res = false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

}
