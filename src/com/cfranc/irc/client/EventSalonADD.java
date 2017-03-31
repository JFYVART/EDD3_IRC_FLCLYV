package com.cfranc.irc.client;

import com.cfranc.irc.server.Salon;

public class EventSalonADD {
	private Salon salon;

	public EventSalonADD(Salon salon) {
		this.salon = salon;
	}

	public Salon getSalon() {
		return salon;
	}

	public void setSalon(Salon salon) {
		this.salon = salon;
	}

}
