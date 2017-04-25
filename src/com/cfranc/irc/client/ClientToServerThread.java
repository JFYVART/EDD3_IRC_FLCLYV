package com.cfranc.irc.client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

import com.cfranc.irc.ClientServerProtocol;
import com.cfranc.irc.server.Salon;
import com.cfranc.irc.server.SalonLst;
import com.cfranc.irc.server.User;
import com.cfranc.irc.ui.SimpleChatClientApp;

public class ClientToServerThread extends Thread implements IfSenderModel {


	private Socket socket = null;
	private DataOutputStream streamOut = null;
	private DataInputStream streamIn = null;
	private BufferedReader console = null;
	String login, pwd;
	// Liste des utilisateurs du salon en cours (sert pour l'affichage dans un onglet donné)
	DefaultListModel<String> clientListModel;
	// Conversation des utilisateurs du salon (sert pour l'affichage dans un onglet donné)
	StyledDocument documentModel;
	// Liste des salons (la modification de la liste permet d'avertir la vue)
	DefaultListSalonModel salonListModel;
	// Conversation et liste des utilisateurs du salon en cours
	DiscussionSalon discussionSalonEncours;
	// Hashmap qui permet à parti de l'idSalon de retrouver son DiscussionSalon
	public HashMap<Integer, DiscussionSalon> salon_DiscussionSalonMap = new HashMap<Integer, DiscussionSalon>();

	String msgToSend = null;
	// Nouveau Protocole
	String nomRecepteur = "";
	String commande = "";
	int idSalon = SalonLst.DEFAULT_SALON_ID;
	String nomSalon = SalonLst.DEFAULT_SALON_NAME;
	int nouveauIdSalon  = SalonLst.DEFAULT_SALON_ID;

	boolean done;

	private EmoticonManager emoticonManager = new EmoticonManager();




	/**
	 * Recherche si il existe un DiscussionSalon
	 * correspondant à l'idSalon S'il existe, on le retourne tel quel Sinon on
	 * le crée
	 *
	 * @param idSalon
	 * @return DiscussionSalon
	 */
	public DiscussionSalon createOrRetrieveDiscussionSalonByIdSalon(int idSalon) {
		// Par défaut on crée un nouveau Discussionsalon
		DiscussionSalon discussionsalon = new DiscussionSalon(new DefaultListModel<String>(), new DefaultStyledDocument());

		// Si une discussionSalon correspond à l'idSalon, on
		// le retourne, sinon ce sera le nouveau discussionSalon qui sera retourné
		if (this.salon_DiscussionSalonMap.containsKey(idSalon)) {
			discussionsalon = this.salon_DiscussionSalonMap.get(idSalon);
		} else // On ajoute le idsalon et discussionSalon
		{
			this.salon_DiscussionSalonMap.put(new Integer(idSalon), discussionsalon);
		}
		return discussionsalon;
	}

	/**
	 * retourne le DefaultListModel<String> (liste des salons) de la discussion d'un salon donné
	 *
	 * @param idSalon
	 * @return DefaultListModel<String>
	 */
	public DefaultListModel<String> getClientListModelByIdSalon(int idSalon) {
		// On mets à jour le salon en cours
		this.discussionSalonEncours =  this.createOrRetrieveDiscussionSalonByIdSalon(idSalon);
		return this.discussionSalonEncours.getClientListModel();
	}


	/**
	 * retourne le StyledDocument (documentModel contenant les messages) d'un salon donné
	 *
	 * @param idSalon
	 * @return StyledDocument
	 */
	public StyledDocument getStyledDocumentByIdSalon(int idSalon) {
		// On mets à jour le salon en cours
		this.discussionSalonEncours = this.createOrRetrieveDiscussionSalonByIdSalon(idSalon);
		return this.discussionSalonEncours.getDocumentModel();
	}


	public ClientToServerThread(StyledDocument documentModel, DefaultListModel<String> clientListModel, Socket socket,
			String login, String pwd, DefaultListSalonModel salonListModel) {
		super();
		// création d'un discussionSalon
		this.documentModel = documentModel;
		this.clientListModel = clientListModel;
		this.salon_DiscussionSalonMap = new HashMap<Integer, DiscussionSalon>();
		this.discussionSalonEncours = new DiscussionSalon(this.clientListModel, this.documentModel);
		this.salon_DiscussionSalonMap.put(new Integer(SalonLst.DEFAULT_SALON_ID),this.discussionSalonEncours);

		this.socket = socket;
		this.login = login;
		this.pwd = pwd;
		this.salonListModel = salonListModel;
	}

	/***
	 * Ajoute (si besoin) le nom du salon à la liste des salon
	 *
	 * @param nomSalon
	 */
	public void ajouteNomSalon(String nomSalon, int nouveauIdSalon) {
		if ((!nomSalon.isEmpty()) &&(!this.salonListModel.isDefaultListSalonModelContainsNomSalon(nomSalon))){
			Salon salonLu = new Salon(nomSalon, SalonLst.DEFAULT_SALON_NOT_PRIVACY, nouveauIdSalon);
			if (!this.salonListModel.contains(salonLu)) {
				// On rajoute le salon à la liste des salons
				this.salonListModel.addElement(salonLu);
				// On crée une discussionSalon pour ce nouveau salon
				this.discussionSalonEncours = this.createOrRetrieveDiscussionSalonByIdSalon(nouveauIdSalon);
				this.salon_DiscussionSalonMap.put(new Integer(nouveauIdSalon),this.discussionSalonEncours);
				// On prévient la vue de se raffraichir
				EventSalonADD eventSalonAdd = new EventSalonADD(salonLu,this.discussionSalonEncours );
				this.salonListModel.notifyObservers(eventSalonAdd);
			}
		}
	}

	/***
	 * Ajoute (si besoin) le nom du salon à la liste des salon
	 *
	 * @param nomSalon
	 */
	public void ajouteNomSalonPrive(String nomSalon, int nouveauIdSalon, String nomUser, String msg) {
		if ((!nomSalon.isEmpty()) &&(nomSalon.contains(this.login))&&(!this.salonListModel.isDefaultListSalonModelContainsNomSalon(nomSalon))){
			Salon salonLu = new Salon(nomSalon, SalonLst.DEFAULT_SALON_NOT_PRIVACY, nouveauIdSalon);
			if (!this.salonListModel.contains(salonLu)) {
				// On rajoute le salon à la liste des salons
				this.salonListModel.addElement(salonLu);
				// On crée une discussionSalon pour ce nouveau salon
				this.discussionSalonEncours = this.createOrRetrieveDiscussionSalonByIdSalon(nouveauIdSalon);
				this.documentModel = this.discussionSalonEncours.getDocumentModel();

				if (msg != null) {
					Style styleBI = this.documentModel.getStyle(SimpleChatClientApp.BOLD_ITALIC);
					Style styleGP = this.documentModel.getStyle(SimpleChatClientApp.GRAY_PLAIN);
					try {
						this.documentModel.insertString(this.documentModel.getLength(), nomUser + " : ", styleBI);
						this.documentModel.insertString(this.documentModel.getLength(), msg + "\n", styleGP);
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				this.salon_DiscussionSalonMap.put(new Integer(nouveauIdSalon),this.discussionSalonEncours);
				// On prévient la vue de se raffraichir
				EventSalonADD eventSalonAdd = new EventSalonADD(salonLu,this.discussionSalonEncours );
				this.salonListModel.notifyObservers(eventSalonAdd);
			}
		}
	}


	private boolean authentification() {
		boolean res = false;
		String loginPwdQ;

		// Nouveau protocole :
		String line = "";
		String commande = "";
		try {
			while (this.streamIn.available() <= 0) {
				Thread.sleep(100);
			}
			loginPwdQ = this.streamIn.readUTF();
			// Nouveau protocole : On décode la commande du serveur
			commande = ClientServerProtocol.decodeProtocole_Command(loginPwdQ);
			// Si la commande est une demande d'identification (Login et PWD)
			if (commande.equals(ClientServerProtocol.LOGIN_PWD)) {
				// On renvoie le login et pwd de la fenetre de connexion.
				this.commande = "";
				this.msgToSend = "";
				line = ClientServerProtocol.encodeProtocole_Ligne(this.login, this.pwd, this.msgToSend, this.commande,
						this.idSalon, this.nomSalon, this.nomRecepteur, this.nouveauIdSalon);
				this.streamOut.writeUTF(line);
			}
			while (this.streamIn.available() <= 0) {
				Thread.sleep(100);
			}
			String acq = this.streamIn.readUTF();
			// Nouveau protocole : On décode la réponse du serveur
			commande = ClientServerProtocol.decodeProtocole_Command(acq);
			String nomSalon = ClientServerProtocol.decodeProtocole_NomSalon(acq);
			// Si la commande est une réponse positive
			if (commande.equals(ClientServerProtocol.OK)) {
				res = true;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res = false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public void close() throws IOException {
		if (this.socket != null) {
			this.socket.close();
		}
		if (this.streamIn != null) {
			this.streamIn.close();
		}
		if (this.streamOut != null) {
			this.streamOut.close();
		}
		System.out.println("Socket fermée");
	}



	public void open() throws IOException {
		this.console = new BufferedReader(new InputStreamReader(System.in));
		this.streamIn = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
		this.streamOut = new DataOutputStream(this.socket.getOutputStream());
	}

	@Override
	public void quitServer() throws IOException {
		// Nouveau protocole : On signale que l'utilisateur courant s'en va
		this.commande = ClientServerProtocol.DEL;
		this.pwd = "";
		this.msgToSend = "";
		String line = ClientServerProtocol.encodeProtocole_Ligne(this.login, this.pwd, this.msgToSend, this.commande, this.idSalon, this.nomSalon,
				this.nomRecepteur, this.nouveauIdSalon);
		this.streamOut.writeUTF(line);
		this.streamOut.flush();
		this.done = true;
	}

	void readMsg() throws IOException {
		String line = this.streamIn.readUTF();
		System.out.println(line);

		// Nouveau protocole : On décode la communication qui vient d'arriver
		String loginUtilisateur = ClientServerProtocol.decodeProtocole_Login(line);
		String msg = ClientServerProtocol.decodeProtocole_Message(line);
		String commande = ClientServerProtocol.decodeProtocole_Command(line);
		String nomSalon = ClientServerProtocol.decodeProtocole_NomSalon(line);
		int idSalon = ClientServerProtocol.decodeProtocole_IdSalon(line);
		int nouveauIdSalon = ClientServerProtocol.decodeProtocole_Nouveausalon(line);
		EventSalonNewMsg eventNewMsg;
		// On se positionne sur le clientListModel concerné
		this.clientListModel = this.getClientListModelByIdSalon(idSalon);
		// On se positionne sur le documentModel concerné
		this.documentModel = this.getStyledDocumentByIdSalon(idSalon);

		switch (commande) {
		case ClientServerProtocol.ADD: // Un user arrive dans le salon
			if (!this.clientListModel.contains(loginUtilisateur)) {
				this.clientListModel.addElement(loginUtilisateur);
				this.receiveMessage(loginUtilisateur, " entre dans le salon...");
			} else if(!this.salonListModel.isDefaultListSalonModelContainsNomSalon(nomSalon)){
				this.receiveMessage(loginUtilisateur, " entre dans le salon...");
			}
			break;

		case ClientServerProtocol.DEL: // Un user quitte le salon
			if (this.clientListModel.contains(loginUtilisateur)) {
				this.clientListModel.removeElement(loginUtilisateur);
				this.receiveMessage(loginUtilisateur, " quitte le salon !");
			}
			break;

		case ClientServerProtocol.NVSALON: // Le serveur informe de la création
			// d'un nouveau salon
			this.ajouteNomSalon(nomSalon, nouveauIdSalon);
			break;

		case ClientServerProtocol.NVMSGPRIVE: // le serveur informe de la
			// création d'un salon privé
			// (msg privé)
			this.ajouteNomSalonPrive(nomSalon, nouveauIdSalon, loginUtilisateur, msg);
			break;

		case ClientServerProtocol.QUITSALON: // le serveur informe de la
			// suppression d'un salon
			// (public ou privé)
			this.supprimeNomSalon(nomSalon, idSalon, loginUtilisateur);
			break;

		default: // Réception d'un message de tchat (cas général)
			this.receiveMessage(loginUtilisateur, msg);
			//			eventNewMsg = new EventSalonNewMsg(Boolean.TRUE);
			//			this.salonListModel.notifyObservers(eventNewMsg);
			break;
		}
	}

	public void receiveMessage(String user, String line) {
		Style styleBI = this.documentModel.getStyle(SimpleChatClientApp.BOLD_ITALIC);
		Style styleGP = this.documentModel.getStyle(SimpleChatClientApp.GRAY_PLAIN);
		this.receiveMessage(user, line, styleBI, styleGP);
	}

	public void receiveMessage(String user, String line, Style styleBI, Style styleGP) {
		try {
			if (!line.isEmpty()) {
				this.documentModel.insertString(this.documentModel.getLength(), user + " : ", styleBI);
				this.documentModel.insertString(this.documentModel.getLength(), line + "\n", styleGP);
				if (this.emoticonManager.isEmoticonInChain(line)) {
					this.emoticonManager.insertIcon(this.documentModel);
				}
			}
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			this.open();
			this.done = !this.authentification();
			while (!this.done) {
				try {
					if (this.streamIn.available() > 0) {
						this.readMsg();
					}

					if (!this.sendMsg()) {
						Thread.sleep(100);
					}
				} catch (IOException | InterruptedException ioe) {
					ioe.printStackTrace();
					this.done = true;
				}
			}

			this.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean sendMsg() throws IOException {
		boolean res = false;
		if (this.msgToSend != null) {
			// Nouveau protocole : on envoie le message de l'utilisateur courant
			this.pwd = "";
			if(this.commande.equals(ClientServerProtocol.QUITSALON)){
				this.supprimeNomSalonThread(this.nomSalon, this.idSalon);

			}
			String line = ClientServerProtocol.encodeProtocole_Ligne(this.login, this.pwd, this.msgToSend,
					this.commande, this.idSalon, this.nomSalon, this.nomRecepteur, this.nouveauIdSalon);
			this.streamOut.writeUTF(line);
			this.msgToSend = null;
			this.streamOut.flush();

			res = true;
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.cfranc.irc.client.IfSenderModel#setMsgToSend(java.lang.String)
	 */
	@Override
	public void setMsgToSend(String msgToSend, int idsalon, String nomSalon, String commande, String recepteur) {
		this.msgToSend = msgToSend;
		this.idSalon = idsalon;
		this.nomSalon = nomSalon;
		this.commande = commande;
		this.nomRecepteur = recepteur;
	}

	/***
	 * supprime (si besoin) le nom du salon à la liste des salon
	 *
	 * @param nomSalon
	 */
	public void supprimeNomSalon(String nomSalon, int nouveauIdSalon, String nomUser) {
		if (!nomSalon.isEmpty()) {
			// on retrouve le salon correpondant au nom et à l'identifiant que l'on nous donne
			for (int i = 0; i < this.salonListModel.size(); i++) {
				Salon salonEnCours = this.salonListModel.elementAt(i);
				if((salonEnCours.getIdSalon()==nouveauIdSalon) &&(salonEnCours.getNomSalon().equals(nomSalon))){
					// Si le salon est trouvé : On retrouve la discussionSalon liée
					this.discussionSalonEncours = this.createOrRetrieveDiscussionSalonByIdSalon(nouveauIdSalon);
					// On crée un EventSalonSUPPR
					User userPartant = new User(nomUser, "", nouveauIdSalon);
					// On retrouve le
					EventSalonSUPPR eventSalonAdd = new EventSalonSUPPR(salonEnCours, this.discussionSalonEncours, userPartant);
					// On avertit la vue que l'on supprime le salon.
					this.salonListModel.notifyObservers(eventSalonAdd);
					// On supprime l'utilisateur du salon
					this.clientListModel.removeElement(nomUser);
				}
			}
		}
	}


	/***
	 * supprime (si besoin) le nom du salon à la liste des salon
	 *
	 * @param nomSalon
	 */
	public void supprimeNomSalonThread(String nomSalon, int idSalon) {
		for (int i = 0; i < this.salonListModel.size(); i++) {
			Salon salonEnCours = this.salonListModel.elementAt(i);
			if((salonEnCours.getIdSalon()==idSalon) &&(salonEnCours.getNomSalon().equals(nomSalon))){
				this.salonListModel.removeElement(salonEnCours);
			}
		}
	}

}
