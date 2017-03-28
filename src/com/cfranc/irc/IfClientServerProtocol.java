package com.cfranc.irc;

public class IfClientServerProtocol {
	public static final String LOGIN_PWD = "#Login?#Pwd?";
	public static final String SEPARATOR = "#";
	public static final String KO = "#KO";
	public static final String OK = "#OK";
	public static final String ADD = "#+#";
	public static final String DEL = "#-#";
	public static final String NVSALON = "#+SAL#";
	public static final String NVMSG = "#+MSG#";

	/**
	 * Régle de communication : Tout msg doit avoir cette syntaxe
	 * 
	 * "#" +Nom utilisateur + "#" + Msg utilisateur +"#" + commande utilisateur
	 * +"#" + IdSalon + "#" + Nom Salon
	 * 
	 * Exemples :
	 * 
	 * #Toto#Hello tout le monde##0#Salon général (Toto envoit le message
	 * "Hello tout le monde" au salon n° 0 (Salon général))
	 * 
	 * #Toto##-#0#Salon général (Toto quitte le salon n° 0 (Salon général))
	 * 
	 * #Toto#Donald#+MSG##Salon privé (Toto demande la création d'un nouveau
	 * salon fermé pour envoyer un message privé à Donald)
	 * 
	 * #Toto##+SAL## Salon Java (Toto demande la création d'un nouveau
	 * salon public nommé "Salon Java")
	 * 
	 * 
	 **/

	public static String decodeProtocoleLogin(String line) {
		String[] userMsg = line.split(IfClientServerProtocol.SEPARATOR);
		String login = userMsg[1];
		return login;
	}

	public static String decodeProtocoleMessage(String line) {
		String[] userMsg = line.split(IfClientServerProtocol.SEPARATOR);
		String msg = userMsg[2];
		return msg;
	}

	public static String decodeProtocoleCommand(String line) {
		String[] userMsg = line.split(IfClientServerProtocol.SEPARATOR);
		String command = userMsg[4];
		return command;
	}

	public static int decodeProtocoleIdSalon(String line) {
		String[] userMsg = line.split(IfClientServerProtocol.SEPARATOR);
		String idSalonString = userMsg[6];
		return Integer.parseInt(idSalonString);
	}

	public static String decodeProtocoleNomSalon(String line) {
		String[] userMsg = line.split(IfClientServerProtocol.SEPARATOR);
		String salonName = userMsg[7];
		return salonName;
	}

	
	public static String encodeLigne(String login, String msg, String command, int salonId, String salonName){
		return SEPARATOR + login + SEPARATOR + msg + SEPARATOR + command + SEPARATOR +  salonId + SEPARATOR + salonName;
	}
	
}