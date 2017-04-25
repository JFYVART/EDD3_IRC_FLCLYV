package com.cfranc.irc.client;

import java.util.ArrayList;

import javax.swing.DefaultListModel;

import com.cfranc.irc.server.Salon;
import com.cfranc.irc.ui.SimpleChatFrameClient;



public class DefaultListSalonModel extends DefaultListModel<Salon> {

	/**
	 * Implémentation du design pattern de l'observateur
	 * On hérite de DefaultListModel pour permettre d'hétiter des mécanisme de cette classe (Modèle MVC natif)
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
			} else if (event instanceof EventSalonNewMsg) {
				observer.colorSalon((EventSalonNewMsg)event);
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

	public boolean isDefaultListSalonModelContainsNomSalon(String nomSalon){
		boolean result = false;
		for (int i = 0; i <this.size(); i++) {
			Salon salonLu = this.getElementAt(i);
			if (salonLu.getNomSalon().equals(nomSalon)){
				result = true;
			}
		}

		return result;
	}
	
	public String getNomSalonById(int salonId){
		String result = "";
		for (int i = 0; i <this.size(); i++) {
			Salon salonLu = this.getElementAt(i);
			if (salonLu.getIdSalon()==salonId){
				result = salonLu.getNomSalon();
			}
		}

		return result;
	}
	

}
