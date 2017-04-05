package com.cfranc.irc.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import com.cfranc.irc.ClientServerProtocol;

public class ClientConnectThread extends Thread {
	StyledDocument model = null;
	DefaultListModel<String> clientListModel;

	private boolean canStop = false;

	private ServerSocket server = null;

	public ClientConnectThread(int port, StyledDocument model, DefaultListModel<String> clientListModel,
			SalonLst mySalons) {
		try {
			this.model = model;
			this.clientListModel = clientListModel;
			this.printMsg("Binding to port " + port + ", please wait  ...");
			this.server = new ServerSocket(port);
			BroadcastThread.mySalons = mySalons;
			this.printMsg("Server started: " + this.server);
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}

	private void acceptClient(Socket socket) throws IOException, InterruptedException {
		// Read user login and pwd
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

		// Nouveau protocole : On demande le login et pwd
		String loginUtilisateur = "";
		String pwdUtilisateur = "";

		// A la connexion, on logge l'utilisateur par défaut sur le salon
		// général
		int salonId = BroadcastThread.mySalons.DEFAULT_SALON_ID;
		String salonName = BroadcastThread.mySalons.DEFAULT_SALON_NAME;
		String msg = "";
		String recepteur = "";
		int nouveauIdSalon = BroadcastThread.mySalons.DEFAULT_SALON_ID;
		String line = ClientServerProtocol.encodeProtocole_Ligne(loginUtilisateur, pwdUtilisateur, msg,
				ClientServerProtocol.LOGIN_PWD, salonId, salonName, recepteur, nouveauIdSalon);

		dos.writeUTF(line);
		while (dis.available() <= 0) {
			Thread.sleep(100);
		}
		String reponse = dis.readUTF();

		// Nouveau protocole : on lit le login et pwd retournés par le client
		loginUtilisateur = ClientServerProtocol.decodeProtocole_Login(reponse);
		pwdUtilisateur = ClientServerProtocol.decodeProtocole_PWD(reponse);
		// On crée un objet User à partir de ces 2 informations.
		User newUser = new User(loginUtilisateur, loginUtilisateur, salonId);
		boolean isUserOK = this.authentication(newUser, salonId);
		if (isUserOK) {

			ServerToClientThread client = new ServerToClientThread(newUser, socket, this.clientListModel);
			// Nouveau protocole : On accepte la connexion.
			line = ClientServerProtocol.encodeProtocole_Ligne(loginUtilisateur, pwdUtilisateur, msg,
					ClientServerProtocol.OK, salonId, salonName, recepteur, nouveauIdSalon);

			dos.writeUTF(line);

			// Add user
			if (BroadcastThread.addClient(salonId, newUser, client, true)) {
				client.start();
				this.clientListModel.addElement(newUser.getLogin());

				// Nouveau protocole : On signale l'arrivée de cet utilisateur
				line = ClientServerProtocol.encodeProtocole_Ligne(loginUtilisateur, pwdUtilisateur, msg,
						ClientServerProtocol.ADD, salonId, salonName, recepteur, nouveauIdSalon);

				dos.writeUTF(line);
			}
		} else {
			System.out.println("socket.close()");

			// Nouveau protocole : On refuse la connexion et on transmet une
			// erreur.
			line = ClientServerProtocol.encodeProtocole_Ligne(loginUtilisateur, pwdUtilisateur, msg,
					ClientServerProtocol.KO, salonId, salonName, recepteur, nouveauIdSalon);

			dos.writeUTF(line);
			dos.close();
			socket.close();
		}
	}

	private boolean authentication(User newUser, int idsalon) {
		return BroadcastThread.accept(newUser, idsalon);

	}

	public void close() throws IOException {
		System.err.println("server:close()");
		if (this.server != null) {
			this.server.close();
		}
	}

	public void open() throws IOException {
	}

	private void printMsg(String msg) {
		try {
			if (this.model != null) {
				this.model.insertString(this.model.getLength(), msg + "\n", null);
			}
			System.out.println(msg);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (!this.canStop) {
			this.printMsg("Waiting for a client ...");
			Socket socket;
			try {
				socket = this.server.accept();
				this.printMsg("Client accepted: " + socket);

				// Accept new client or close the socket
				this.acceptClient(socket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
