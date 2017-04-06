package com.cfranc.irc.client;

import com.cfranc.irc.server.Salon;

public class EventSalonADD {
	private Salon salon;
	private DiscussionSalon discussionSalon;

	public EventSalonADD(Salon salon,  DiscussionSalon discussionSalon) {
		this.salon = salon;
		this.discussionSalon = discussionSalon;
	}

	public Salon getSalon() {
		return this.salon;
	}

	public void setSalon(Salon salon) {
		this.salon = salon;
	}

	public DiscussionSalon getDiscussionSalon() {
		return this.discussionSalon;
	}

	public void setDiscussionSalon(DiscussionSalon discussionSalon) {
		this.discussionSalon = discussionSalon;
	}

}
