package com.cfranc.irc.client;

import java.io.IOException;

public interface IfSenderModel {

	public abstract void setMsgToSend(String msgToSend, int idSalon, String NomSalon, String commande, String recepteur);
	
	public abstract void quitServer() throws IOException;

}