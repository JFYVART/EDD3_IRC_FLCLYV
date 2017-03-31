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
		System.out.println("Socket fermée");
	}

	public void receiveMessage(String user, String line) {
		Style styleBI = ((StyledDocument) documentModel).getStyle(SimpleChatClientApp.BOLD_ITALIC);
		Style styleGP = ((StyledDocument) documentModel).getStyle(SimpleChatClientApp.GRAY_PLAIN);
		receiveMessage(user, line, styleBI, styleGP);
	}

	public void receiveMessage(String user, String line, Style styleBI, Style styleGP) {
		try {
			if (!line.isEmpty()) {
				documentModel.insertString(documentModel.getLength(), user + " : ", styleBI);
				documentModel.insertString(documentModel.getLength(), line + "\n", styleGP);
			}
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/***
	 * Ajoute (si besoin) le nom du salon à la liste des salon
	 * 
	 * @param nomSalon
	 */
	public void ajouteNomSalon(String nomSalon) {
		if (!nomSalon.isEmpty()) {
			Salon salonLu = new Salon(nomSalon, SalonLst.DEFAULT_SALON_NOT_PRIVACY);
			if (!salonListModel.contains(salonLu)) {
				salonListModel.addElement(salonLu);
				EventSalonADD eventSalonAdd = new EventSalonADD(salonLu);
				salonListModel.notifyObservers(eventSalonAdd);
			}
		}
	}

	/***
	 * supprime (si besoin) le nom du salon à la liste des salon
	 * 
	 * @param nomSalon
	 */
	public void supprimeNomSalon(String nomSalon) {
		if (!nomSalon.isEmpty()) {
			Salon salonLu = new Salon(nomSalon, SalonLst.DEFAULT_SALON_NOT_PRIVACY);
			if (!salonListModel.contains(salonLu)) {
				salonListModel.addElement(salonLu);
				EventSalonSUPPR eventSalonAdd = new EventSalonSUPPR(salonLu);
				salonListModel.notifyObservers(eventSalonAdd);
			}
		}
	}

	void readMsg() throws IOException {
		String line = streamIn.readUTF();
		System.out.println(line);

		// Nouveau protocole : On décode la communication qui vient d'arriver
		String loginUtilisateur = ClientServerProtocol.decodeProtocole_Login(line);
		String msg = ClientServerProtocol.decodeProtocole_Message(line);
		String commande = ClientServerProtocol.decodeProtocole_Command(line);
		String nomSalon = ClientServerProtocol.decodeProtocole_NomSalon(line);
		int idSalon = ClientServerProtocol.decodeProtocole_IdSalon(line);
		int nouveauIdSalon = ClientServerProtocol.decodeProtocole_Nouveausalon(line);

		switch (commande) {
		case ClientServerProtocol.ADD: // Un user arrive dans le salon
			if (!clientListModel.contains(loginUtilisateur)) {
				clientListModel.addElement(loginUtilisateur);
				receiveMessage(loginUtilisateur, " entre dans le salon...");
			}
			break;

		case ClientServerProtocol.DEL: // Un user quitte le salon
			if (clientListModel.contains(loginUtilisateur)) {
				clientListModel.removeElement(loginUtilisateur);
				receiveMessage(loginUtilisateur, " quitte le salon !");
			}
			break;

		case ClientServerProtocol.NVSALON: // Le serveur informe de la création
											// d'un nouveau salon
			ajouteNomSalon(nomSalon);
			break;

		case ClientServerProtocol.NVMSGPRIVE: // le serveur informe de la
												// création d'un salon privé
												// (msg privé)
			ajouteNomSalon(nomSalon);
			break;

		case ClientServerProtocol.QUITSALON: // le serveur informe de la
												// suppression d'un salon
												// (public ou privé)
			supprimeNomSalon(nomSalon);
			break;

		default: // Réception d'un message de tchat (cas général)
			receiveMessage(loginUtilisateur, msg);
			break;
		}
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

	private boolean sendMsg() throws IOException {
		boolean res = false;
		if (msgToSend != null) {
			// Nouveau protocole : on envoie le message de l'utilisateur courant
			pwd = "";
			String line = ClientServerProtocol.encodeProtocole_Ligne(this.login, this.pwd, this.msgToSend,
					this.commande, this.idSalon, this.nomSalon, this.nomRecepteur, this.nouveauIdSalon);
			streamOut.writeUTF(line);
			msgToSend = null;
			streamOut.flush();
			res = true;
		}
		return res;
	}

	public void quitServer() throws IOException {
		// Nouveau protocole : On signale que l'utilisateur courant s'en va
		commande = ClientServerProtocol.DEL;
		pwd = "";
		msgToSend = "";
		String line = ClientServerProtocol.encodeProtocole_Ligne(login, pwd, msgToSend, commande, idSalon, nomSalon,
				nomRecepteur, nouveauIdSalon);
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
		try {
			while (streamIn.available() <= 0) {
				Thread.sleep(100);
			}
			loginPwdQ = streamIn.readUTF();
			// Nouveau protocole : On décode la commande du serveur
			commande = ClientServerProtocol.decodeProtocole_Command(loginPwdQ);
			// Si la commande est une demande d'identification (Login et PWD)
			if (commande.equals(ClientServerProtocol.LOGIN_PWD)) {
				// On renvoie le login et pwd de la fenetre de connexion.
				this.commande = "";
				msgToSend = "";
				line = ClientServerProtocol.encodeProtocole_Ligne(this.login, this.pwd, msgToSend, this.commande,
						idSalon, nomSalon, nomRecepteur, nouveauIdSalon);
				streamOut.writeUTF(line);
			}
			while (streamIn.available() <= 0) {
				Thread.sleep(100);
			}
			String acq = streamIn.readUTF();
			// Nouveau protocole : On décode la réponse du serveur
			commande = ClientServerProtocol.decodeProtocole_Command(acq);
			String nomSalon = ClientServerProtocol.decodeProtocole_NomSalon(acq);
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
