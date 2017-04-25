package com.cfranc.irc.client;

public class EventSalonNewMsg {
	private Boolean isSalonToColor;
	private String nameSalon;

	public EventSalonNewMsg(String nameSalon, Boolean isSalonToColor) {
		this.nameSalon = nameSalon;
		this.isSalonToColor = isSalonToColor;
	}

	public Boolean getIsSalonToColor() {
		return this.isSalonToColor;
	}

	public void setIsSalonToColor(Boolean isSalonToColor) {
		this.isSalonToColor = isSalonToColor;
	}

	public String getNameSalon() {
		return nameSalon;
	}

	public void setNameSalon(String nameSalon) {
		this.nameSalon = nameSalon;
	}



}
