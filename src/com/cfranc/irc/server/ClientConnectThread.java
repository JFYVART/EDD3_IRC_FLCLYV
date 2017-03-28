package com.cfranc.irc.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;

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
	
	public ClientConnectThread(int port, StyledDocument model, DefaultListModel<String> clientListModel) {
		try {
			this.model=model;
			this.clientListModel=clientListModel;
			printMsg("Binding to port " + port + ", please wait  ...");
			server = new ServerSocket(port);
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
		String line = ClientServerProtocol.encodeProtocole_Ligne(loginUtilisateur, pwdUtilisateur, "", ClientServerProtocol.LOGIN_PWD, 0, "");
		dos.writeUTF(line);
		while(dis.available()<=0){
			Thread.sleep(100);
		}
		String reponse=dis.readUTF();
		
		// Nouveau protocole : on lit le login et pwd retourn�s par le client 
		loginUtilisateur = ClientServerProtocol.decodeProtocole_Login(reponse);
		pwdUtilisateur = ClientServerProtocol.decodeProtocole_PWD(reponse);
		int salonUser=0;
		// On cr�e un objet User � partir de ces 2 informations.
		User newUser=new User(loginUtilisateur, loginUtilisateur, salonUser);
		boolean isUserOK=authentication(newUser, salonUser);
		if(isUserOK){
			
			ServerToClientThread client=new ServerToClientThread(newUser, socket);
			// Nouveau protocole :  On accepte la connexion.
			line = ClientServerProtocol.encodeProtocole_Ligne(loginUtilisateur, pwdUtilisateur, "", ClientServerProtocol.OK, 0, "");
			dos.writeUTF(line);

			// Add user
			if(BroadcastThread.addClient(newUser, client)){
				client.start();			
				clientListModel.addElement(newUser.getLogin());
				// Nouveau protocole : On signale l'arriv�e de cet utilisateur 
				line = ClientServerProtocol.encodeProtocole_Ligne(loginUtilisateur, pwdUtilisateur, "", ClientServerProtocol.ADD, 0, "");
				dos.writeUTF(line);
			}
		}
		else{
			System.out.println("socket.close()");
			// Nouveau protocole :  On refuse la connexion et on transmet une erreur. 
			line = ClientServerProtocol.encodeProtocole_Ligne(loginUtilisateur, pwdUtilisateur, "", ClientServerProtocol.KO, 0, "");
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
