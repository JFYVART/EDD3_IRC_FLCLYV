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
import com.cfranc.irc.server.Salon;
import com.cfranc.irc.server.SalonLst;
import com.cfranc.irc.ui.SimpleChatClientApp;

public class ClientToServerThread extends Thread implements IfSenderModel {
	private Socket socket = null;
	private DataOutputStream streamOut = null;
	private DataInputStream streamIn = null;
	private BufferedReader console = null;
	String login, pwd;
	DefaultListModel<String> clientListModel;
	StyledDocument documentModel;
	DefaultListSalonModel salonListModel;
	String msgToSend = null;
	// Nouveau Protocole
	String nomRecepteur = "";
	String commande = "";
	int idSalon = SalonLst.DEFAULT_SALON_ID;
	String nomSalon = SalonLst.DEFAULT_SALON_NAME;
	int nouveauIdSalon  = SalonLst.DEFAULT_SALON_ID;

	boolean done;

	public ClientToServerThread(StyledDocument documentModel, DefaultListModel<String> clientListModel, Socket socket,
			String login, String pwd, DefaultListSalonModel salonListModel) {
		super();
		this.documentModel = documentModel;
		this.clientListModel = clientListModel;
		this.socket = socket;
		this.login = login;
		this.pwd = pwd;
		this.salonListModel = salonListModel;
	}

	/***
	 * Ajoute (si besoin) le nom du salon � la liste des salon
	 *
	 * @param nomSalon
	 */
	public void ajouteNomSalon(String nomSalon, int nouveauIdSalon) {
		if (!nomSalon.isEmpty()) {
			Salon salonLu = new Salon(nomSalon, SalonLst.DEFAULT_SALON_NOT_PRIVACY, nouveauIdSalon);
			if (!this.salonListModel.contains(salonLu)) {
				this.salonListModel.addElement(salonLu);
				EventSalonADD eventSalonAdd = new EventSalonADD(salonLu);
				this.salonListModel.notifyObservers(eventSalonAdd);
			}
		}
	}

	private boolean authentification() {
		boolean res = false;
		String loginPwdQ;

		// Nouveau protocole :
		String line = "";
		String commande = "";
		try {
			while (this.streamIn.available() <= 0) {
				Thread.sleep(100);
			}
			loginPwdQ = this.streamIn.readUTF();
			// Nouveau protocole : On d�code la commande du serveur
			commande = ClientServerProtocol.decodeProtocole_Command(loginPwdQ);
			// Si la commande est une demande d'identification (Login et PWD)
			if (commande.equals(ClientServerProtocol.LOGIN_PWD)) {
				// On renvoie le login et pwd de la fenetre de connexion.
				this.commande = "";
				this.msgToSend = "";
				line = ClientServerProtocol.encodeProtocole_Ligne(this.login, this.pwd, this.msgToSend, this.commande,
						this.idSalon, this.nomSalon, this.nomRecepteur, this.nouveauIdSalon);
				this.streamOut.writeUTF(line);
			}
			while (this.streamIn.available() <= 0) {
				Thread.sleep(100);
			}
			String acq = this.streamIn.readUTF();
			// Nouveau protocole : On d�code la r�ponse du serveur
			commande = ClientServerProtocol.decodeProtocole_Command(acq);
			String nomSalon = ClientServerProtocol.decodeProtocole_NomSalon(acq);
			// Si la commande est une r�ponse positive
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

	public void close() throws IOException {
		if (this.socket != null) {
			this.socket.close();
		}
		if (this.streamIn != null) {
			this.streamIn.close();
		}
		if (this.streamOut != null) {
			this.streamOut.close();
		}
		System.out.println("Socket ferm�e");
	}

	public void open() throws IOException {
		this.console = new BufferedReader(new InputStreamReader(System.in));
		this.streamIn = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
		this.streamOut = new DataOutputStream(this.socket.getOutputStream());
	}

	@Override
	public void quitServer() throws IOException {
		// Nouveau protocole : On signale que l'utilisateur courant s'en va
		this.commande = ClientServerProtocol.DEL;
		this.pwd = "";
		this.msgToSend = "";
		String line = ClientServerProtocol.encodeProtocole_Ligne(this.login, this.pwd, this.msgToSend, this.commande, this.idSalon, this.nomSalon,
				this.nomRecepteur, this.nouveauIdSalon);
		this.streamOut.writeUTF(line);
		this.streamOut.flush();
		this.done = true;
	}

	void readMsg() throws IOException {
		String line = this.streamIn.readUTF();
		System.out.println(line);

		// Nouveau protocole : On d�code la communication qui vient d'arriver
		String loginUtilisateur = ClientServerProtocol.decodeProtocole_Login(line);
		String msg = ClientServerProtocol.decodeProtocole_Message(line);
		String commande = ClientServerProtocol.decodeProtocole_Command(line);
		String nomSalon = ClientServerProtocol.decodeProtocole_NomSalon(line);
		int idSalon = ClientServerProtocol.decodeProtocole_IdSalon(line);
		int nouveauIdSalon = ClientServerProtocol.decodeProtocole_Nouveausalon(line);

		switch (commande) {
		case ClientServerProtocol.ADD: // Un user arrive dans le salon
			if (!this.clientListModel.contains(loginUtilisateur)) {
				this.clientListModel.addElement(loginUtilisateur);
				this.receiveMessage(loginUtilisateur, " entre dans le salon...");
			}
			break;

		case ClientServerProtocol.DEL: // Un user quitte le salon
			if (this.clientListModel.contains(loginUtilisateur)) {
				this.clientListModel.removeElement(loginUtilisateur);
				this.receiveMessage(loginUtilisateur, " quitte le salon !");
			}
			break;

		case ClientServerProtocol.NVSALON: // Le serveur informe de la cr�ation
			// d'un nouveau salon
			this.ajouteNomSalon(nomSalon, nouveauIdSalon);
			break;

		case ClientServerProtocol.NVMSGPRIVE: // le serveur informe de la
			// cr�ation d'un salon priv�
			// (msg priv�)
			this.ajouteNomSalon(nomSalon, nouveauIdSalon);
			break;

		case ClientServerProtocol.QUITSALON: // le serveur informe de la
			// suppression d'un salon
			// (public ou priv�)
			this.supprimeNomSalon(nomSalon, nouveauIdSalon);
			break;

		default: // R�ception d'un message de tchat (cas g�n�ral)
			this.receiveMessage(loginUtilisateur, msg);
			break;
		}
	}

	public void receiveMessage(String user, String line) {
		Style styleBI = this.documentModel.getStyle(SimpleChatClientApp.BOLD_ITALIC);
		Style styleGP = this.documentModel.getStyle(SimpleChatClientApp.GRAY_PLAIN);
		this.receiveMessage(user, line, styleBI, styleGP);
	}

	public void receiveMessage(String user, String line, Style styleBI, Style styleGP) {
		try {
			if (!line.isEmpty()) {
				this.documentModel.insertString(this.documentModel.getLength(), user + " : ", styleBI);
				this.documentModel.insertString(this.documentModel.getLength(), line + "\n", styleGP);
			}
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			this.open();
			this.done = !this.authentification();
			while (!this.done) {
				try {
					if (this.streamIn.available() > 0) {
						this.readMsg();
					}

					if (!this.sendMsg()) {
						Thread.sleep(100);
					}
				} catch (IOException | InterruptedException ioe) {
					ioe.printStackTrace();
					this.done = true;
				}
			}

			this.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean sendMsg() throws IOException {
		boolean res = false;
		if (this.msgToSend != null) {
			// Nouveau protocole : on envoie le message de l'utilisateur courant
			this.pwd = "";
			String line = ClientServerProtocol.encodeProtocole_Ligne(this.login, this.pwd, this.msgToSend,
					this.commande, this.idSalon, this.nomSalon, this.nomRecepteur, this.nouveauIdSalon);
			this.streamOut.writeUTF(line);
			this.msgToSend = null;
			this.streamOut.flush();
			res = true;
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.cfranc.irc.client.IfSenderModel#setMsgToSend(java.lang.String)
	 */
	@Override
	public void setMsgToSend(String msgToSend, int idsalon, String nomSalon, String commande, String recepteur) {
		this.msgToSend = msgToSend;
		this.idSalon = idsalon;
		this.nomSalon = nomSalon;
		this.commande = commande;
		this.nomRecepteur = recepteur;
	}

	/***
	 * supprime (si besoin) le nom du salon � la liste des salon
	 *
	 * @param nomSalon
	 */
	public void supprimeNomSalon(String nomSalon, int nouveauIdSalon) {
		if (!nomSalon.isEmpty()) {
			Salon salonLu = new Salon(nomSalon, SalonLst.DEFAULT_SALON_NOT_PRIVACY, nouveauIdSalon);
			if (!this.salonListModel.contains(salonLu)) {
				this.salonListModel.addElement(salonLu);
				EventSalonSUPPR eventSalonAdd = new EventSalonSUPPR(salonLu);
				this.salonListModel.notifyObservers(eventSalonAdd);
			}
		}
	}

}
