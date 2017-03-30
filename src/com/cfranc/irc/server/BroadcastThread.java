package com.cfranc.irc.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.cfranc.irc.ClientServerProtocol;

public class BroadcastThread extends Thread {

	// public static HashMap<User, ServerToClientThread> clientTreadsMap=new HashMap<User, ServerToClientThread>();
	public static HashMap<Integer, HashMap<User, ServerToClientThread>> salon_ClientTreadsMap = new HashMap<Integer, HashMap<User, ServerToClientThread>>();
	public static SalonLst mySalons;
	public static ArrayList<String> listConnextedUser = new ArrayList<String> ();

	static {
		Collections.synchronizedMap(salon_ClientTreadsMap);
		Collections.synchronizedList(listConnextedUser);
	}

	public static boolean addClient(int idSalon, User user, ServerToClientThread serverToClientThread) {
		boolean res = true;
		// Nouveau protocole :
		String line;
		String msg = "";
		String commande = "";
		String pwd = "";
		String nomSalon = mySalons.getSalonName(idSalon);
		String nomRecepteur = "";
		
		// On r�cup�re le clientTreadsMap li� au salon
		HashMap<User, ServerToClientThread> clientTreadsMap = createOrRetrieveClientTreadsMapByIdSalon(idSalon,user,serverToClientThread);
		
		if (clientTreadsMap.containsKey(user)) {
			res = false;
		} else {
			// On rajoute l'utilisateur � la liste des utilisateurs connect�s  
			if (!listConnextedUser.contains(user.getLogin())){
				listConnextedUser.add(user.getLogin());
			}
			
			// clientTreadsMap.put(user, serverToClientThread);
			// modifs du 03/11 : ajout de tous les users � la liste des users
			// des clients
			for (Entry<User, ServerToClientThread> entry : clientTreadsMap.entrySet()) {
				// Nouveau protocole : On signale � chaque Thread client
				// l'arriv�e d'un nouvel utilisateur
				commande = ClientServerProtocol.ADD;
				line = ClientServerProtocol.encodeProtocole_Ligne(user.getLogin(), pwd, msg, commande, idSalon,
						nomSalon, nomRecepteur);
				entry.getValue().post(line);
			}

			clientTreadsMap.put(user, serverToClientThread);

			for (Entry<User, ServerToClientThread> entry : clientTreadsMap.entrySet()) {
				// Nouveau protocole : On signale au nouvel arrivant les
				// utilisateurs existants
				line = ClientServerProtocol.encodeProtocole_Ligne(entry.getKey().getLogin(), pwd, msg,
						ClientServerProtocol.ADD, idSalon, nomSalon, nomRecepteur);
				serverToClientThread.post(line);
			}
		}
		return res;
	}

	public static void sendMessage(User sender, String pwd, String msg, String command, int idSalon, String nomSalon, String recepteur) {
		// Nouveau protocole :
		String line;
		// On r�cup�re le clientTreadsMap li� au salon
		HashMap<User, ServerToClientThread> clientTreadsMap = getClientTreadsMap(idSalon);
		Collection<ServerToClientThread> clientTreads = clientTreadsMap.values();
		Iterator<ServerToClientThread> receiverClientThreadIterator = clientTreads.iterator();
		while (receiverClientThreadIterator.hasNext()) {
			ServerToClientThread clientThread = (ServerToClientThread) receiverClientThreadIterator.next();
			// Nouveau protocole : On envoie le message en pr�cisant le login et
			// le msg
			line = ClientServerProtocol.encodeProtocole_Ligne(sender.getLogin(), pwd, msg, command, idSalon, nomSalon, recepteur);
			clientThread.post(line);
			System.out.println("sendMessage : " + "#" + sender.getLogin() + "#" + msg);
		}
	}

	/***
	 * Gestion d'une demande de cr�ation de salon publique (Tout le monde peut y acc�der)  
	 * @param user
	 * @param pwd
	 * @param msg
	 * @param commande
	 * @param idSalon
	 * @param nomSalon
	 * @param recepteur
	 */
	public static void createNewSalon(User user, String pwd, String msg, String commande, int idSalon, String nomSalon, String recepteur){
		// on cree le nouveau salon
		idSalon = mySalons.createOrRetrieveSalon(nomSalon, SalonLst.DEFAULT_SALON_NOT_PRIVACY);
		// on renvoie le message avec l'idSalon � tout le monde
		broadCastMessage(user,pwd,msg,commande,idSalon,nomSalon, recepteur);
	}
	
	
	/***
	 * Gestion d'une demande de cr�ation d'un salon priv� avec deux utilisateurs (pour envoyer des messages priv�s) 
	 * @param user
	 * @param pwd
	 * @param msg
	 * @param commande
	 * @param idSalon
	 * @param nomSalon
	 * @param recepteur
	 */
	public static void createMsgPrive(User user, String pwd, String msg, String commande, int idSalon, String nomSalon, String recepteur){
		// on cree le nouveau salon
		idSalon = mySalons.createOrRetrieveSalon(nomSalon, SalonLst.DEFAULT_SALON_PRIVACY);
		// on renvoie le message avec l'idSalon � l'exp�diteur du message priv� : Le nom du salon priv� est celui de l'exp�diteur 
		sendMessage(user,pwd,msg,commande,idSalon,recepteur, "");
		// on envoie le message avec l'idSalon au r�cepteur du message priv� : le nom du salon priv� est celui de l'exp�diteur
		User recepteurUser = new User(recepteur,"",idSalon);
		sendMessage(recepteurUser,pwd,msg,commande,idSalon,user.getLogin(),"");
	}
	
	/***
	 * Envoie � tous les utilisateurs de tous les salons un message 
	 * @param user
	 * @param pwd
	 * @param msg
	 * @param commande
	 * @param idSalon
	 * @param nomSalon
	 * @param recepteur
	 */
	private static void broadCastMessage(User user, String pwd, String msg, String commande, int idSalon, String nomSalon, String recepteur){
		// On parcoure la liste des utilisateurs connect�s
		for (String connectedUsername : listConnextedUser) {
			User connectedUser = new User(connectedUsername, "", idSalon);
			sendMessage(connectedUser,pwd,msg,commande,idSalon,recepteur,recepteur);
		}
	}
	
	
	public static void removeClientFromSalon(User user, String pwd, String msg, String commande, int idSalon, String nomSalon, String recepteur){
		// On r�cup�re le clientTreadsMap li� au salon
		HashMap<User, ServerToClientThread> clientTreadsMap = getClientTreadsMap(idSalon);
		// on supprime le User du clientTreadsMap
		clientTreadsMap.remove(user);
		// On envoie le message � tout le monde pour que chaun mette � jour son IHM
		broadCastMessage(user,pwd,msg,commande,idSalon,nomSalon, recepteur);
		
	}
	
	public static void removeClient(User user) {
		// On parcoure tous les clientTreadsMap (salon) pour supprimer le USER  
		
		// On r�cup�re le clientTreadsMap li� au salon
		//HashMap<User, ServerToClientThread> clientTreadsMap = getClientTreadsMap(idSalon);
		//clientTreadsMap.remove(user);
	}

	public static boolean accept(User user, int idSalon) {
		boolean res = true;
		// On r�cup�re le clientTreadsMap li� au salon
		HashMap<User, ServerToClientThread> clientTreadsMap = getClientTreadsMap(idSalon);
			if (clientTreadsMap.containsKey(user)) {
				res = false;
			}
		return res;
	}

	/**
	 * Recherche si il existe un HashMap<User, ServerToClientThread> correspondant � l'idSalon
	 * S'il existe, on le retourne tel quel
	 * Sinon on le cr�e en rajoutant le user et son  serverToClientThread
	 * 
	 * @param idSalon
	 * @param user
	 * @param serverToClientThread
	 * @return
	 */
	public static HashMap<User, ServerToClientThread> createOrRetrieveClientTreadsMapByIdSalon(int idSalon, User user, 
			ServerToClientThread serverToClientThread) {
		// Par d�faut on cr�e un nouveau HashMap<User, ServerToClientThread>
		HashMap<User, ServerToClientThread> clientTreadsMap = new HashMap<User, ServerToClientThread>();

		// Si un HashMap<User, ServerToClientThread> correspond � l'idSalon, on
		// le retourne, sinon ce sera le nouveau HashMap<User,
		// ServerToClientThread> qui sera retourn�
		if (salon_ClientTreadsMap.containsKey(idSalon)) {
			clientTreadsMap = salon_ClientTreadsMap.get(idSalon);
		} 
		else // On ajoute le idsalon et le Thread au nouveau HashMap<User, ServerToClientThread>
		{
			salon_ClientTreadsMap.put(new Integer(idSalon), clientTreadsMap);
		}

		return clientTreadsMap;
	}

	
	/**
	 *  Retourne le HashMap<User, ServerToClientThread> correspondant au salon
	 *  On part du principe que ce HashMap existe !!!
	 * @param idSalon
	 * @return
	 */
	public static HashMap<User, ServerToClientThread> getClientTreadsMap(int idSalon) {
		// Par d�faut on cr�e un nouveau HashMap<User, ServerToClientThread>
		HashMap<User, ServerToClientThread> clientTreadsMap = new HashMap<User, ServerToClientThread>();

		// Si un HashMap<User, ServerToClientThread> correspond � l'idSalon, on
		// le retourne

		// sinon ce sera le nouveau HashMap<User, ServerToClientThread> qui sera
		// retourn�
		if (salon_ClientTreadsMap.containsKey(idSalon)) {
			clientTreadsMap = salon_ClientTreadsMap.get(idSalon);
		}
		return clientTreadsMap;
	}
}
