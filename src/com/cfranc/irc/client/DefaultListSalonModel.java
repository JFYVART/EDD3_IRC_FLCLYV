package com.cfranc.irc.client;

import java.util.ArrayList;

import javax.swing.DefaultListModel;

import com.cfranc.irc.server.Salon;
import com.cfranc.irc.ui.SimpleChatFrameClient;



public class DefaultListSalonModel extends DefaultListModel<Salon> {

	/**
	 * Impl�mentation du design pattern de l'observateur
	 * On h�rite de DefaultListModel pour permettre d'h�titer des m�canisme de cette classe (Mod�le MVC natif)
	 *
	 */
	private static final long serialVersionUID = 1L;

	public DefaultListSalonModel(){
		super();
	}

	// Liste des observateurs
	private ArrayList<SimpleChatFrameClient> collectionObservateur = new ArrayList<SimpleChatFrameClient>();

	// Notification des observateurs.
	public void notifyObservers(Object event) {
		for (SimpleChatFrameClient observer : this.collectionObservateur) {
			if (event instanceof EventSalonADD) {
				observer.addSalon((EventSalonADD)event);
			} else if (event instanceof EventSalonSUPPR) {
				observer.supprSalon((EventSalonSUPPR)event);
			}
		}
	}


	public void addObserver(SimpleChatFrameClient observer) {
		this.collectionObservateur.add(observer);
	}

	public void deleteObserver(SimpleChatFrameClient observer) {
		this.collectionObservateur.remove(observer);
	}

	public ArrayList<SimpleChatFrameClient> getCollection() {
		return this.collectionObservateur;
	}

	protected void setCollection(ArrayList<SimpleChatFrameClient> collection) {
		this.collectionObservateur = collection;
	}

}
