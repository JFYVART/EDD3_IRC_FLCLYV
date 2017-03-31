package com.cfranc.irc.client;

import java.util.ArrayList;

import javax.swing.DefaultListModel;

import com.cfranc.irc.server.Salon;
import com.cfranc.irc.ui.SimpleChatFrameClient;



public class DefaultListSalonModel extends DefaultListModel<Salon> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DefaultListSalonModel(){
		super();
	}
	
	private ArrayList<SimpleChatFrameClient> collectionObservateur = new ArrayList<SimpleChatFrameClient>();

	public void notifyObservers(Object event) {
		for (SimpleChatFrameClient observer : collectionObservateur) {
			if (event instanceof EventSalonADD) {
			observer.addSalon((EventSalonADD)event);
			} else if (event instanceof EventSalonSUPPR) {
				observer.supprSalon((EventSalonSUPPR)event);
				}
		}
	}

	public void addObserver(SimpleChatFrameClient observer) {
		collectionObservateur.add(observer);
	}

	public void deleteObserver(SimpleChatFrameClient observer) {
		collectionObservateur.remove(observer);
	}

	public ArrayList<SimpleChatFrameClient> getCollection() {
		return collectionObservateur;
	}

	protected void setCollection(ArrayList<SimpleChatFrameClient> collection) {
		this.collectionObservateur = collection;
	}

}
