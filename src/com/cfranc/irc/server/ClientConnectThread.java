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
	StyledDocument model=null;
	DefaultListModel<String> clientListModel;
	
	private boolean canStop=false;
	private ServerSocket server = null;
	
	private void printMsg(String msg){
		try {
			if(model!=null){
				model.insertString(model.getLength(), msg+"\n", null);
			}
			System.out.println(msg);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ClientConnectThread(int port, StyledDocument model, DefaultListModel<String> clientListModel, SalonLst mySalons) {
		try {
			this.model=model;
			this.clientListModel=clientListModel;
			printMsg("Binding to port " + port + ", please wait  ...");
			server = new ServerSocket(port);
			BroadcastThread.mySalons = mySalons;
			printMsg("Server started: " + server);
		} 
		catch (IOException ioe) {
			System.out.println(ioe);
		}
	}
	
	@Override
	public void run() {
		while(!canStop){
			printMsg("Waiting for a client ...");
			Socket socket;
			try {
				socket = server.accept();
				printMsg("Client accepted: " + socket);
				
				// Accept new client or close the socket
				acceptClient(socket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void acceptClient(Socket socket) throws IOException, InterruptedException {
		// Read user login and pwd
		DataInputStream dis=new DataInputStream(socket.getInputStream());
		DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
		
		// Nouveau protocole : On demande le login et pwd
		String loginUtilisateur = "";
		String pwdUtilisateur = "";
		// A la connexion, on logge l'utilisateur par défaut sur le salon général
		int salonId= BroadcastThread.mySalons.DEFAULT_SALON_ID;
		String salonName = BroadcastThread.mySalons.DEFAULT_SALON_NAME;
		String msg = "";
		String line = ClientServerProtocol.encodeProtocole_Ligne(loginUtilisateur, pwdUtilisateur, msg, ClientServerProtocol.LOGIN_PWD, salonId, salonName);
		dos.writeUTF(line);
		while(dis.available()<=0){
			Thread.sleep(100);
		}
		String reponse=dis.readUTF();
		
		// Nouveau protocole : on lit le login et pwd retournés par le client 
		loginUtilisateur = ClientServerProtocol.decodeProtocole_Login(reponse);
		pwdUtilisateur = ClientServerProtocol.decodeProtocole_PWD(reponse);
		
		// On crée un objet User à partir de ces 2 informations.
		User newUser=new User(loginUtilisateur, loginUtilisateur, salonId);
		boolean isUserOK=authentication(newUser, salonId);
		if(isUserOK){
			
			ServerToClientThread client=new ServerToClientThread(newUser, socket, clientListModel);
			// Nouveau protocole :  On accepte la connexion.
			line = ClientServerProtocol.encodeProtocole_Ligne(loginUtilisateur, pwdUtilisateur, msg, ClientServerProtocol.OK, salonId, salonName);
			dos.writeUTF(line);

			// Add user
			if(BroadcastThread.addClient(salonId, newUser, client)){
				client.start();			
				clientListModel.addElement(newUser.getLogin());
				// Nouveau protocole : On signale l'arrivée de cet utilisateur 
				line = ClientServerProtocol.encodeProtocole_Ligne(loginUtilisateur, pwdUtilisateur, msg, ClientServerProtocol.ADD, salonId, salonName);
				dos.writeUTF(line);
			}
		}
		else{
			System.out.println("socket.close()");
			// Nouveau protocole :  On refuse la connexion et on transmet une erreur. 
			line = ClientServerProtocol.encodeProtocole_Ligne(loginUtilisateur, pwdUtilisateur, msg, ClientServerProtocol.KO, salonId, salonName);
			dos.writeUTF(line);
			dos.close();
			socket.close();
		}
	}
	
	private boolean authentication(User newUser, int idsalon){
		return BroadcastThread.accept(newUser, idsalon);
	}

	
	public void open() throws IOException {
	}
	
	public void close() throws IOException {
		System.err.println("server:close()");
		if (server != null)
			server.close();
	}


}
