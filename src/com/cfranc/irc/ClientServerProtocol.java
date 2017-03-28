package com.cfranc.irc;

public class ClientServerProtocol {
	public static final String LOGIN_PWD = "LoginPwd";
	public static final String SEPARATOR = "#";
	
	/**
	 *  Liste des commandes utilisables
	 */
	// Pb technique ou refus d'une action demandée
	public static final String KO = "KO";
	// Acceptation d'une action demandée
	public static final String OK = "OK";
	// Un nouvel utilisateur arrive dans un salon
	public static final String ADD = "+";
	// Un utilisateur quitte un salon 
	public static final String DEL = "-";
	// Un utilisateur demande la création d'un nouveau salon
	public static final String NVSALON = "NVS";
	// Un utilisateur demande la création d'un msg privé
	public static final String NVMSGPRIVE = "MSGPRIVE";

	/**
	 * Régle de communication : Tout msg doit avoir cette syntaxe
	 * 
	 * "#" + Login utilisateur + "#" + Password utilisateur + "#" + Msg utilisateur +"#" + commande utilisateur +"#" + IdSalon + "#" + Nom Salon
	 * 
	 * Exemples :
	 * 
	 * #Toto##Hello tout le monde##0#Salon général (Toto envoit le message
	 * "Hello tout le monde" au salon n° 0 (Salon général))
	 * 
	 * #Toto###-#0#Salon général (Toto quitte le salon n° 0 (Salon général))
	 * 
	 * #Toto##Donald#+MSG##Salon privé (Toto demande la création d'un nouveau
	 * salon fermé pour envoyer un message privé à Donald)
	 * 
	 * #Toto###+SAL## Salon Java (Toto demande la création d'un nouveau salon
	 * public nommé "Salon Java")
	 * 
	 * 
	 **/

	public static String decodeProtocole_Login(String line) {
		String[] userMsg = line.split(ClientServerProtocol.SEPARATOR);
		String login = userMsg[1];
		if (login == null)
			login = "";
		return login;
	}

	public static String decodeProtocole_PWD(String line) {
		String[] userMsg = line.split(ClientServerProtocol.SEPARATOR);
		String pwd = userMsg[2];
		if (pwd == null)
			pwd = "";
		return pwd;
	}

	public static String decodeProtocole_Message(String line) {
		String[] userMsg = line.split(ClientServerProtocol.SEPARATOR);
		String msg = userMsg[3];
		if (msg == null)
			msg = "";
		return msg;
	}

	public static String decodeProtocole_Command(String line) {
		String[] userMsg = line.split(ClientServerProtocol.SEPARATOR);
		String command = userMsg[4];
		if (command == null)
			command = "";
		return command;
	}

	public static int decodeProtocole_IdSalon(String line) {
		String[] userMsg = line.split(ClientServerProtocol.SEPARATOR);
		String idSalonString = userMsg[5];
		if (idSalonString == null)
			idSalonString = "0";
		return Integer.parseInt(idSalonString);
	}

	public static String decodeProtocole_NomSalon(String line) {
		String[] userMsg = line.split(ClientServerProtocol.SEPARATOR);
		String salonName = userMsg[6];
		if (salonName == null)
			salonName = "";
		return salonName;
	}

	public static String encodeProtocole_Ligne(String login, String pwd, String msg, String command, int salonId,
			String salonName) {
		// On remplace les NULL par des chaines vides
		if (login == null)
			login = "";
		if (pwd == null)
			pwd = "";
		if (msg == null)
			msg = "";
		if (command == null)
			command = "";
		if (salonName == null)
			salonName = "";
		// Construction de la ligne
		return SEPARATOR + login + SEPARATOR + pwd + SEPARATOR + msg + SEPARATOR + command + SEPARATOR + salonId
				+ SEPARATOR + salonName;
	}
}