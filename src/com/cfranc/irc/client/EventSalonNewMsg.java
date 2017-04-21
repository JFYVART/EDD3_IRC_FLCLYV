package com.cfranc.irc.client;

public class EventSalonNewMsg {
	private Boolean isSalonToColor;

	public EventSalonNewMsg(Boolean isSalonToColor) {
		this.isSalonToColor = isSalonToColor;
	}

	public Boolean getIsSalonToColor() {
		return this.isSalonToColor;
	}

	public void setIsSalonToColor(Boolean isSalonToColor) {
		this.isSalonToColor = isSalonToColor;
	}



}
