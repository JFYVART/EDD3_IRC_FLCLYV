package com.cfranc.irc.client;

import com.cfranc.irc.server.Salon;
import com.cfranc.irc.server.User;

public class EventSalonSUPPR {
	private Salon salon;
	private DiscussionSalon discussionSalon;
	private User user;

	public EventSalonSUPPR(Salon salon,  DiscussionSalon discussionSalon, User user) {
		this.salon = salon;
		this.discussionSalon = discussionSalon;
		this.user = user;
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

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}


}
