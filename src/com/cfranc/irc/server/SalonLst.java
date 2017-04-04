package com.cfranc.irc.server;

import java.util.ArrayList;

public class SalonLst {

	public static final String DEFAULT_SALON_NAME = "Général";
	public static final int DEFAULT_SALON_ID = 0;
	public static final boolean DEFAULT_SALON_NOT_PRIVACY = false;
	public static final boolean DEFAULT_SALON_PRIVACY = true;

	private ArrayList<Salon> lstSalons; // list des salons

	public SalonLst() {
		this.lstSalons = new ArrayList<Salon>();
		// Création du salon "Général"
		this.lstSalons.add(new Salon(DEFAULT_SALON_NAME, false, DEFAULT_SALON_ID));
	}

	/***
	 * Cherche à partir de son nom si un salon existe déjà - S'il existe renvoie
	 * l'idSalon - S'il n'existe pas : renvoie le nouvel idSalon (après création
	 * du salon dans lstSalon)
	 *
	 * @param name
	 * @param isPrivate
	 * @return
	 */
	public int createOrRetrieveSalon(String name, boolean isPrivate) {
		int idSalon = this.retrieveIdSalon(name);
		if (idSalon == 0) {
			// on crée le salon
			Salon salon = new Salon(name, isPrivate, DEFAULT_SALON_ID);
			// On ajoute le salon
			this.set(salon);
			idSalon = this.lstSalons.size() - 1;
			salon.setIdSalon(idSalon);
		}

		return idSalon;
	}

	/***
	 * Supprime un salon de la liste à partir de son nom
	 *
	 * @param name
	 */
	public void deleteSalon(String name) {
		int idSalon = this.retrieveIdSalon(name);
		if (idSalon > 0) {
			this.lstSalons.remove(idSalon);
		}
	}

	public Salon get(int i) {
		return this.lstSalons.get(i);
	}

	/**
	 * Getteur Liste des salons
	 * @return
	 */
	public ArrayList<Salon> getLstSalons(){
		return this.lstSalons;
	}

	public String getSalonName(int i) {
		Salon salon = this.get(i);
		return salon.getNomSalon();
	}

	/**
	 * Liste des salons sous forme de tableau de String pour la JTree
	 * @return
	 */
	public String[] listeSalons(){
		String[] salons;
		int i=0;
		salons = new String[this.lstSalons.size()];
		for (Salon salon : this.lstSalons) {
			salons[i]=salon.getNomSalon();
			i++;
		}
		return salons;
	}

	/**
	 * Cherche la position du salon dans lstSalon
	 *
	 * @param name
	 * @return
	 */
	public int retrieveIdSalon(String name) {
		// init Salon
		int idSalon = 0;
		int position = 0;
		for (Salon salon : this.lstSalons) {
			if (salon.getNomSalon().equals(name)) {
				idSalon = position;
			} else {
				position++;
			}
		}
		return idSalon;
	}

	public void set(Salon salon) {
		this.lstSalons.add(salon);
	}

	public void setSalonName(int i, String name) {
		Salon salon = this.get(i);
		salon.setNomSalon(name);
	}
}
