package com.cfranc.irc.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import com.cfranc.irc.ClientServerProtocol;

public class ServerToClientThread extends Thread {
	private User user;
	private Socket socket = null;
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;

	private DefaultListModel<String> clientListModel;

	public ServerToClientThread(User user, Socket socket, DefaultListModel<String> clientListModel) {
		super();
		this.user = user;
		this.socket = socket;
		this.clientListModel = clientListModel;
	}

	List<String> msgToPost = new ArrayList<String>();

	public synchronized void post(String msg) {
		msgToPost.add(msg);
	}

	private synchronized void doPost() {
		try {
			for (String msg : msgToPost) {
				streamOut.writeUTF(msg);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			msgToPost.clear();
		}
	}

	public void open() throws IOException {
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

	@Override
	public void run() {
		try {
			open();
			boolean done = false;
			while (!done) {
				try {
					if (streamIn.available() > 0) {
						// Nouveau protocole : on décode le message du client
						String line = streamIn.readUTF();
						String login = ClientServerProtocol.decodeProtocole_Login(line);
						String msg = ClientServerProtocol.decodeProtocole_Message(line);
						String commande = ClientServerProtocol.decodeProtocole_Command(line);
						String pwd = ClientServerProtocol.decodeProtocole_PWD(line);
						String nomSalon = ClientServerProtocol.decodeProtocole_NomSalon(line);
						int idSalon = ClientServerProtocol.decodeProtocole_IdSalon(line);

						// Analyse et traitement de la ligne reçue : On se base
						// sur la nature de la commande pour déterminer le
						// travail à faire
						switch (commande) {
						case ClientServerProtocol.DEL: // L'utilisateur veut
														// quitter le chat.
							done = true;
							// On informes les IHM clients qu'un utilisateur
							// s'en va
							BroadcastThread.sendMessage(user, pwd, msg, commande, idSalon, nomSalon);
							// On supprime l'utilisateur de la liste des
							// Utilisateur du salon
							BroadcastThread.removeClient(user, idSalon);
							// Suppression de l'utilisateur de la liste des
							// utilisateurs connectés (IHM Serveur)
							clientListModel.removeElement(user.getLogin());
							break;

						case ClientServerProtocol.NVSALON: // L'utilisateur veut
															// créer un nouveau
															// salon
							BroadcastThread.createNewSalon(user, pwd, msg, commande, idSalon, nomSalon);
							break;

						default: // Défaut = diffusion de message texte
							if (login.equals(user)) {
								System.err.println("ServerToClientThread::run(), login!=user" + login);
							}
							BroadcastThread.sendMessage(user, pwd, msg, commande, idSalon, nomSalon);
							break;
						}

					} else {
						doPost();
					}
				} catch (IOException ioe) {
					done = true;
				}
			}
			close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
