package com.cfranc.irc.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.cfranc.irc.ClientServerProtocol;

public class BroadcastThread extends Thread {

	public static HashMap<User, ServerToClientThread> clientTreadsMap = new HashMap<User, ServerToClientThread>();
	static {
		Collections.synchronizedMap(clientTreadsMap);
	}

	public static boolean addClient(User user, ServerToClientThread serverToClientThread) {
		boolean res = true;
		// Nouveau protocole :
		String line;
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
				// Nouveau protocole : On signale au nouveal arrivant les
				// utilisateurs existants
				line = ClientServerProtocol.encodeProtocole_Ligne(entry.getKey().getLogin(), "", "",
						ClientServerProtocol.ADD, 0, "");
				serverToClientThread.post(line);
			}
		}
		return res;
	}

	public static void sendMessage(User sender, String msg, String commande) {
		// Nouveau protocole :
		String line;

		Collection<ServerToClientThread> clientTreads = clientTreadsMap.values();
		Iterator<ServerToClientThread> receiverClientThreadIterator = clientTreads.iterator();
		while (receiverClientThreadIterator.hasNext()) {
			ServerToClientThread clientThread = (ServerToClientThread) receiverClientThreadIterator.next();
			// Nouveau protocole : On envoie le message en précisant le login et
			// le msg
			line = ClientServerProtocol.encodeProtocole_Ligne(sender.getLogin(), "", msg, commande, 0, "");
			clientThread.post(line);
			System.out.println("sendMessage : " + "#" + sender.getLogin() + "#" + msg);
		}
	}

	public static void removeClient(User user) {
		clientTreadsMap.remove(user);
	}

	public static boolean accept(User user) {
		boolean res = true;
		if (clientTreadsMap.containsKey(user)) {
			res = false;
		}
		return res;
	}
}
