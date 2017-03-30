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
	private DefaultListModel<String> clientListModel = null;

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
						// Si la commande = DEL => on arrete
						done = commande.equals(ClientServerProtocol.DEL);
						if (!done) {
							if (login.equals(user)) {
								System.err.println(commande + "ServerToClientThread::run(), login!=user" + login);
							}
							BroadcastThread.sendMessage(user, msg, commande);
						} else {
							// Message de départ de l'utilisateur
							BroadcastThread.sendMessage(user, msg, commande);

							// Envoi du message au client
							// line =
							// ClientServerProtocol.encodeProtocole_Ligne(user.getLogin(),
							// "", "",
							// ClientServerProtocol.OK, 0, "");
							// streamOut.writeUTF(line);

							// Suppression de l'utilisateur
							BroadcastThread.removeClient(user);

							clientListModel.removeElement(user.getLogin());
							// SimpleChatServerApp.getClientListModel().removeElement(user.getLogin());
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
