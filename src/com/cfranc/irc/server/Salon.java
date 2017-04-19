package com.cfranc.irc.server;

public class Salon {
	private String nomSalon=null;
	private boolean bPrivate=false;
	private int idSalon = SalonLst.DEFAULT_SALON_ID;

	public Salon(String nomSalon, boolean bPrivate, int idSalon) {
		super();
		this.nomSalon = nomSalon;
		this.bPrivate = bPrivate;
		this.idSalon = idSalon;
	}
	public int getIdSalon() {
		return this.idSalon;
	}
	public String getNomSalon() {
		return this.nomSalon;
	}
	public boolean isbPrivate() {
		return this.bPrivate;
	}
	public void setbPrivate(boolean bPrivate) {
		this.bPrivate = bPrivate;
	}
	public void setIdSalon(int idSalon) {
		this.idSalon = idSalon;
	}
	public void setNomSalon(String nomSalon) {
		this.nomSalon = nomSalon;
	}
	
	//User userCreator = null; need it ?



}
