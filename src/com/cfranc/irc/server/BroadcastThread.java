package com.cfranc.irc.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.cfranc.irc.ClientServerProtocol;

public class BroadcastThread extends Thread {

	// public static HashMap<User, ServerToClientThread> clientTreadsMap=new
	// HashMap<User, ServerToClientThread>();
	public static HashMap<Integer, HashMap<User, ServerToClientThread>> salon_ClientTreadsMap = new HashMap<Integer, HashMap<User, ServerToClientThread>>();
	public static SalonLst mySalons;

	static {
		Collections.synchronizedMap(salon_ClientTreadsMap);
	}

	public static boolean addClient(int idSalon, User user, ServerToClientThread serverToClientThread) {
		boolean res = true;
		// Nouveau protocole :
		String line;

		// On récupère le clientTreadsMap lié au salon
		HashMap<User, ServerToClientThread> clientTreadsMap = createOrRetrieveClientTreadsMapByIdSalon(idSalon, user,
				serverToClientThread);

		// On voit si c'est un nouveau salon

		if (clientTreadsMap.containsKey(user)) {
			res = false;
		} else {
			// clientTreadsMap.put(user, serverToClientThread);
			// modifs du 03/11 : ajout de tous les users à la liste des users
			// des clients
			for (Entry<User, ServerToClientThread> entry : clientTreadsMap.entrySet()) {
				// Nouveau protocole : On signale à chaque Thread client
				// l'arrivée d'un nouvel utilisateur
				line = ClientServerProtocol.encodeProtocole_Ligne(user.getLogin(), "", "", ClientServerProtocol.ADD, 0,
						"");
				entry.getValue().post(line);
			}

			clientTreadsMap.put(user, serverToClientThread);

			for (Entry<User, ServerToClientThread> entry : clientTreadsMap.entrySet()) {

				// Nouveau protocole : On signale au nouvel arrivant les

				// utilisateurs existants
				line = ClientServerProtocol.encodeProtocole_Ligne(entry.getKey().getLogin(), "", "",
						ClientServerProtocol.ADD, 0, "");
				serverToClientThread.post(line);
			}
		}
		return res;
	}

	public static void sendMessage(User sender, String pwd, String msg, String command, int idSalon, String nomSalon) {
		// Nouveau protocole :
		String line;
		// On récupère le clientTreadsMap lié au salon
		HashMap<User, ServerToClientThread> clientTreadsMap = getClientTreadsMap(idSalon);

		Collection<ServerToClientThread> clientTreads = clientTreadsMap.values();
		Iterator<ServerToClientThread> receiverClientThreadIterator = clientTreads.iterator();
		while (receiverClientThreadIterator.hasNext()) {
			ServerToClientThread clientThread = (ServerToClientThread) receiverClientThreadIterator.next();
			// Nouveau protocole : On envoie le message en précisant le login et
			// le msg

			line = ClientServerProtocol.encodeProtocole_Ligne(sender.getLogin(), pwd, msg, command, idSalon, nomSalon);

			clientThread.post(line);
			System.out.println("sendMessage : " + "#" + sender.getLogin() + "#" + msg);
		}
	}

	public static void createNewSalon(User user, String pwd, String msg, String commande, int idSalon,
			String nomSalon) {
		// on cree le nouveau salon
		idSalon = mySalons.createOrRetrieveSalon(nomSalon, SalonLst.DEFAULT_SALON_NOT_PRIVACY);
		// on renvoie le message avec l'idSalon
		sendMessage(user, pwd, msg, commande, idSalon, nomSalon);
	}

	public static void removeClient(User user, int idSalon) {
		// On récupère le clientTreadsMap lié au salon
		HashMap<User, ServerToClientThread> clientTreadsMap = getClientTreadsMap(idSalon);
		clientTreadsMap.remove(user);

	}

	public static boolean accept(User user, int idSalon) {
		boolean res = true;
		// On récupère le clientTreadsMap lié au salon
		HashMap<User, ServerToClientThread> clientTreadsMap = getClientTreadsMap(idSalon);
		if (clientTreadsMap.containsKey(user)) {
			res = false;
		}
		return res;
	}

	/**
	 * Recherche si il existe un HashMap<User, ServerToClientThread>
	 * correspondant à l'idSalon S'il existe, on le retourne tel quel Sinon on
	 * le crée en rajoutant le user et son serverToClientThread
	 * 
	 * @param idSalon
	 * @param user
	 * @param serverToClientThread
	 * @return
	 */
	public static HashMap<User, ServerToClientThread> createOrRetrieveClientTreadsMapByIdSalon(int idSalon, User user,
			ServerToClientThread serverToClientThread) {
		// Par défaut on crée un nouveau HashMap<User, ServerToClientThread>
		HashMap<User, ServerToClientThread> clientTreadsMap = new HashMap<User, ServerToClientThread>();

		// Si un HashMap<User, ServerToClientThread> correspond à l'idSalon, on
		// le retourne, sinon ce sera le nouveau HashMap<User,
		// ServerToClientThread> qui sera retourné
		if (salon_ClientTreadsMap.containsKey(idSalon)) {
			clientTreadsMap = salon_ClientTreadsMap.get(idSalon);
		} else // On ajoute le idsalon et le Thread au nouveau HashMap<User,
				// ServerToClientThread>
		{
			salon_ClientTreadsMap.put(new Integer(idSalon), clientTreadsMap);
		}

		return clientTreadsMap;
	}

	/**
	 * Retourne le HashMap<User, ServerToClientThread> correspondant au salon On
	 * part du principe que ce HashMap existe !!!
	 * 
	 * @param idSalon
	 * @return
	 */
	public static HashMap<User, ServerToClientThread> getClientTreadsMap(int idSalon) {
		// Par défaut on crée un nouveau HashMap<User, ServerToClientThread>
		HashMap<User, ServerToClientThread> clientTreadsMap = new HashMap<User, ServerToClientThread>();

		// Si un HashMap<User, ServerToClientThread> correspond à l'idSalon, on
		// le retourne

		// sinon ce sera le nouveau HashMap<User, ServerToClientThread> qui sera
		// retourné
		if (salon_ClientTreadsMap.containsKey(idSalon)) {
			clientTreadsMap = salon_ClientTreadsMap.get(idSalon);
		}
		return clientTreadsMap;
	}
}
