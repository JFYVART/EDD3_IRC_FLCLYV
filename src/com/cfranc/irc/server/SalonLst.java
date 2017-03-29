package com.cfranc.irc.server;

import java.util.ArrayList;

public class SalonLst  {

	public static final String DEFAULT_SALON_NAME = "Général";
	public static final int DEFAULT_SALON_ID = 0;
	public static final boolean DEFAULT_SALON_NOT_PRIVACY = false;
	
	private ArrayList <Salon> lstSalons; // list des salons
	
	public SalonLst(){
		this.lstSalons = new ArrayList<Salon>();
		// Création du salon "Général"
		this.lstSalons.add(new Salon(DEFAULT_SALON_NAME,false));
	}

	public Salon get(int i) {
		return (Salon) lstSalons.get(i);
	}
	
	public void set(Salon salon) {
		lstSalons.add(salon);
	}

	public String getSalonName(int i){
		Salon salon = get(i);
		return salon.getNomSalon();
	}
	
	public void setSalonName(int i, String name){
		Salon salon = get(i);
		salon.setNomSalon(name);
	}
	
	/**
	 *  Cherche la position du salon dans lstSalon 
	 * @param name
	 * @return
	 */
	public int retrieveIdSalon(String name){
		// init Salon
		int idSalon = 0;
		int position = 0;
		for (Salon salon : lstSalons) {
			if (salon.equals(name)){
				idSalon = position;
			} else position++;
		}
		return idSalon;
	}

	
	/***
	 *  Cherche à partir de son nom si un salon existe déjà
	 *  - S'il existe renvoie l'idSalon
	 *  - S'il n'existe pas : renvoie le nouvel idSalon (après création du salon dans lstSalon)
	 * @param name
	 * @param isPrivate
	 * @return
	 */
	public int createOrRetrieveSalon(String name, boolean isPrivate){
		int idSalon = retrieveIdSalon(name);
		if (idSalon == 0){
			// on crée le salon 
			Salon salon = new Salon(name,isPrivate);
			// On ajoute le salon 
			set(salon);
			idSalon = lstSalons.size();
		}
		
		return idSalon;
	}
}
