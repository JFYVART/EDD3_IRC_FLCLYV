package com.cfranc.irc.client;

import javax.swing.DefaultListModel;
import javax.swing.text.StyledDocument;

public class DiscussionSalon {

	// Liste des utilisateur d'un salon donné
	private DefaultListModel<String> clientListModel;
	// Messages d'un salon donné
	private StyledDocument documentModel;

	public DiscussionSalon(DefaultListModel<String> clientListModel, StyledDocument documentModel) {
		this.clientListModel = clientListModel;
		this.documentModel = documentModel;
	}

	public DefaultListModel<String> getClientListModel() {
		return this.clientListModel;
	}

	public StyledDocument getDocumentModel() {
		return this.documentModel;
	}

	public void setClientListModel(DefaultListModel<String> clientListModel) {
		this.clientListModel = clientListModel;
	}

	public void setDocumentModel(StyledDocument documentModel) {
		this.documentModel = documentModel;
	}



}
