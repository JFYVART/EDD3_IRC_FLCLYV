package com.cfranc.irc.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

import com.cfranc.irc.ClientServerProtocol;
import com.cfranc.irc.client.DefaultListSalonModel;
import com.cfranc.irc.client.DiscussionSalon;
import com.cfranc.irc.client.EventSalonADD;
import com.cfranc.irc.client.EventSalonNewMsg;
import com.cfranc.irc.client.EventSalonSUPPR;
import com.cfranc.irc.client.IfSenderModel;
import com.cfranc.irc.server.Salon;
import com.cfranc.irc.server.SalonLst;


public class SimpleChatFrameClient extends JFrame {

	private DefaultListSalonModel salonListModel;
	DiscussionSalon discussionDuSalonCree;
	IfSenderModel sender;
	private String senderName;
	private String nouveauNomSalonSaisi = "";
	private JPanel contentPane;

	private JPanel panelPiedPage;
	private JTextField textField;
	private JLabel lblSender;
	private JTabbedPane tabbedPaneSalon;

	private final ResourceAction sendAction = new SendAction();

	private final ResourceAction newSalondAction = new NewSalonAction();

	private final ResourceAction closeSalonAction = new CloseSalonAction();

	private final ResourceAction lockAction = new LockAction();

	private boolean isScrollLocked = true;

	private String salonName;
	private SalonLst listSalon = new SalonLst();
	private Document documentModel;
	private ListModel<String> listModel;


	// Refactoring. Inserted by : SCLAUDE  [19 Avr. 2017]
	private static final int SEND_MESSAGE = 0;
	private static final int CREATE_SALON = 1;
	private static final int CLOSE_SALON = 2;

	  Color defaultBackColor; // default background color of tab
	
	private String login;

	private  Color defaultForeColor= Color.GRAY ;
	private boolean isChangeColorNeeded = false;

	private AddNewSalonFrame newSalonFrame;

	JTextField txtCrerUnSalon;
	// private JTextField textField_NewSalon;

	private class CloseSalonAction extends ResourceAction {


		public CloseSalonAction() {
			this.putValue(NAME, Messages.getString("SimpleChatFrameClient.11")); //$NON-NLS-1$
			this.putValue(SHORT_DESCRIPTION, Messages.getString("SimpleChatFrameClient.15")); //$NON-NLS-1$
			this.putValue(SMALL_ICON, this.getIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			System.out.println("Fermeture salon invoqu�e");
			SimpleChatFrameClient.this.sendMessage(CLOSE_SALON);

		}

		private Icon getIcon() {
			return new ImageIcon(SimpleChatFrameClient.class.getResource("send_16_16.jpg")); //$NON-NLS-1$
		}
	}

	private class LockAction extends ResourceAction {
		public LockAction() {
			this.putValue(NAME, Messages.getString("SimpleChatFrameClient.1")); //$NON-NLS-1$
			this.putValue(SHORT_DESCRIPTION, Messages.getString("SimpleChatFrameClient.0")); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			SimpleChatFrameClient.this.isScrollLocked = (!SimpleChatFrameClient.this.isScrollLocked);
		}
	}

	private class NewSalonAction extends ResourceAction {


		public NewSalonAction() {
			this.putValue(NAME, Messages.getString("SimpleChatFrameClient.9")); //$NON-NLS-1$
			this.putValue(SHORT_DESCRIPTION, Messages.getString("SimpleChatFrameClient.14")); //$NON-NLS-1$
			this.putValue(SMALL_ICON, this.getIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			SimpleChatFrameClient.this.sendMessage(CREATE_SALON);
		}

		private Icon getIcon() {
			return new ImageIcon(SimpleChatFrameClient.class.getResource("send_16_16.jpg")); //$NON-NLS-1$
		}
	}

	private abstract class ResourceAction extends AbstractAction {
		public ResourceAction() {
		}
	}

	private class SendAction extends ResourceAction {


		public SendAction() {
			this.putValue(NAME, Messages.getString("SimpleChatFrameClient.3")); //$NON-NLS-1$
			this.putValue(SHORT_DESCRIPTION, Messages.getString("SimpleChatFrameClient.2")); //$NON-NLS-1$
			this.putValue(SMALL_ICON, this.getIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			SimpleChatFrameClient.this.sendMessage(SEND_MESSAGE);
		}

		private Icon getIcon() {
			return new ImageIcon(SimpleChatFrameClient.class.getResource("send_16_16.jpg")); //$NON-NLS-1$
		}
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					this.showMenu(e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					this.showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	/**
	 * Launch the application.
	 *
	 * @throws BadLocationException
	 */
	public static void main(String[] args) throws BadLocationException {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					SimpleChatFrameClient frame = new SimpleChatFrameClient();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		Scanner sc = new Scanner(System.in);
		String line = ""; //$NON-NLS-1$
		while (!line.equals(".bye")) { //$NON-NLS-1$
			line = sc.nextLine();
		} // voir si possible de rajouter fermeture frame client
	}

	public void sendMessage(String user, String line, Style styleBI, Style styleGP) {
		try {
			this.documentModel.insertString(this.documentModel.getLength(), user + " : ", styleBI); //$NON-NLS-1$
			this.documentModel.insertString(this.documentModel.getLength(), line + "\n", styleGP); //$NON-NLS-1$
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public SimpleChatFrameClient() {
		this(null, new DefaultListModel<String>(), SimpleChatClientApp.defaultDocumentModel(),
				new DefaultListSalonModel(), "");
	}

	/**
	 * Create the frame with salonName
	 */

	public SimpleChatFrameClient(IfSenderModel sender, ListModel<String> clientListModel, Document documentModel,
			DefaultListSalonModel salonListModel, String login) {
		this.sender = sender;
		this.documentModel = documentModel;
		this.listModel = clientListModel;
		this.salonListModel = salonListModel;
		this.salonListModel.addObserver(this);

		this.newSalonFrame = new AddNewSalonFrame();
		this.newSalonFrame.setVisible(false);
		this.login = login;

		this.setTitle(Messages.getString("SimpleChatFrameClient.4")); //$NON-NLS-1$
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 545, 300);
		/**
		 * Create menuBar
		 */
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		this.creationMenuFichier(menuBar);
		this.creationMenuOutils(menuBar);
		/**
		 * Ajout Onglet pour Salon
		 */
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		this.setContentPane(this.contentPane);

		this.tabbedPaneSalon = new JTabbedPane(JTabbedPane.TOP);

		// //$NON-NLS-1$
		this.contentPane.add(this.tabbedPaneSalon, BorderLayout.CENTER);

		// On examine le contenu de salonListModel :
		if (salonListModel.getSize() > 0) { // S'il y a des salons
			for (int i = 0; i < salonListModel.getSize(); i++) {
				// On cr�e un onglet par salon
				Salon salon = salonListModel.getElementAt(i);
				this.createOngletSalon(documentModel, this.tabbedPaneSalon, salon, new DefaultListModel<String>());
			}
		} else { // Pas de salon au lancement : on en cr�e un onglet par d�faut,
			// on l'ajoute � la liste et on ouvre un onglet
			Salon salon = new Salon(SalonLst.DEFAULT_SALON_NAME, SalonLst.DEFAULT_SALON_NOT_PRIVACY,
					SalonLst.DEFAULT_SALON_ID);
			salonListModel.addElement(salon);
			this.createOngletSalon(documentModel, this.tabbedPaneSalon, salon, clientListModel);
		}

		// on sauvegarde la couleur de fond
		//		SimpleChatFrameClient.this.defaultBackColor = this.tabbedPaneSalon.getBackgroundAt(SalonLst.DEFAULT_SALON_ID);
		//		SimpleChatFrameClient.this.defaultForeColor  =this.tabbedPaneSalon.getForegroundAt(SalonLst.DEFAULT_SALON_ID);
		JToolBar toolBar = new JToolBar();
		this.contentPane.add(toolBar, BorderLayout.NORTH);

		Label label = new Label(Messages.getString("SimpleChatFrameClient.label.text")); //$NON-NLS-1$
		label.setAlignment(Label.RIGHT);
		toolBar.add(label);

		this.txtCrerUnSalon = new JTextField();
		this.txtCrerUnSalon.setFont(
				this.txtCrerUnSalon.getFont().deriveFont(this.txtCrerUnSalon.getFont().getStyle() | Font.ITALIC, 9f));

		toolBar.add(this.txtCrerUnSalon);
		this.txtCrerUnSalon.setColumns(10);

		JButton btnNewButton = new JButton(this.newSalondAction);
		btnNewButton.setText("Nouveau salon"); //$NON-NLS-1$
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				SimpleChatFrameClient.this.nouveauNomSalonSaisi = SimpleChatFrameClient.this.txtCrerUnSalon.getText();
			}
		});
		toolBar.add(btnNewButton);

		this.panelPiedPage = new JPanel();
		this.contentPane.add(this.panelPiedPage, BorderLayout.SOUTH);
		this.panelPiedPage.setLayout(new BorderLayout(0, 0));
		JPanel panel = new JPanel();
		this.panelPiedPage.add(panel);

		// zone de gestion des messages
		this.lblSender = new JLabel(SalonLst.DEFAULT_SALON_NAME); // $NON-NLS-1$
		this.lblSender.setHorizontalAlignment(SwingConstants.RIGHT);
		this.lblSender.setHorizontalTextPosition(SwingConstants.CENTER);
		this.lblSender.setPreferredSize(new Dimension(100, 14));
		this.lblSender.setMinimumSize(new Dimension(100, 14));

		this.textField = new JTextField();
		this.textField.setHorizontalAlignment(SwingConstants.LEFT);
		this.textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				Messages.getString("SimpleChatFrameClient.12")); //$NON-NLS-1$
		this.textField.getActionMap().put(Messages.getString("SimpleChatFrameClient.13"), this.sendAction); //$NON-NLS-1$

		// Message priv�. Inserted by : SCLAUDE  [19 Avr. 2017]
		//if(this.lblSender.getText().equals(""))

		JButton btnSend = new JButton(this.sendAction);
		btnSend.setMnemonic(KeyEvent.VK_ENTER);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
						.addComponent(this.lblSender, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(this.textField, GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(btnSend)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel
				.createSequentialGroup().addGap(10)
				.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(this.textField, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
						.addComponent(this.lblSender, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
						.addComponent(btnSend, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))));
		panel.setLayout(gl_panel);

	}




	public void createOngletSalon(Document documentModelOnglet, JTabbedPane tabbedPaneSalon, Salon salon,
			ListModel<String> clientListModelOnglet) {

		JPanel panelSalon = new JPanel();
		tabbedPaneSalon.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				//valeur index � r�initialiser apr�s application couleur / Peggy
				int index = 0;
				// On mets � jour le nom du salon actif quand on change d'onglet
				// => Libell� devant la zone de saisie
				SimpleChatFrameClient.this.lblSender.setText(tabbedPaneSalon.getSelectedComponent().getName());
				// inserted by : SCLAUDE  [18 Avr. 2017]. On vide le contenu de la zone de saisie de message une fois ce dernier envoy�
				SimpleChatFrameClient.this.textField.setText("");
				index = SimpleChatFrameClient.this.tabbedPaneSalon.getSelectedIndex();
				//tabbedPaneSalon.setBackgroundAt(index,defaultBackColor );

				// Le clic sur l'onglet arr�te le chgt de couleur de l'onglet
				SimpleChatFrameClient.this.deColoreOnglet(index);
				
				index = 0;

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

		});

		// On mets � jour le nom du salon dans l'onglet et dans le libell�
		// devant la zonbe de saisie (Cr�ation de l'onglet)
		this.salonName = salon.getNomSalon();
		panelSalon.setName(salon.getNomSalon());

		tabbedPaneSalon.addTab(this.salonName.toString(), null, panelSalon, null);
		this.tabbedPaneSalon.setForegroundAt(tabbedPaneSalon.getSelectedIndex(), SimpleChatFrameClient.this.defaultForeColor);
		panelSalon.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		panelSalon.add(splitPane, BorderLayout.CENTER);

		JList<String> list = new JList<String>(clientListModelOnglet);

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int iFirstSelectedElement = ((JList) e.getSource()).getSelectedIndex();
				if ((iFirstSelectedElement >= 0) && (iFirstSelectedElement < clientListModelOnglet.getSize())) {
					SimpleChatFrameClient.this.senderName = clientListModelOnglet.getElementAt(iFirstSelectedElement);
					SimpleChatFrameClient.this.getLblSender().setText(SimpleChatFrameClient.this.senderName);
				} else {
					SimpleChatFrameClient.this.getLblSender().setText("?"); //$NON-NLS-1$
				}
			}
		});
		list.setMinimumSize(new Dimension(100, 0));
		splitPane.setLeftComponent(list);

		JTextPane textArea = new JTextPane((StyledDocument) documentModelOnglet);
		textArea.setEnabled(false);
		JScrollPane scrollPaneText = new JScrollPane(textArea);
		
		// inserted by : STEPHANE/PEGGY  [20 Avr. 2017] : ajout listener sur discussion salon
				//Changement couleur onglet
		documentModelOnglet.addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				System.out.println("???");
				
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {			
				System.out.println("G�re color sur update");
				
				
				
//				int indiceOnglet = 0;
//				for (int i = 0; i < tabbedPaneSalon.getTabCount(); i++) {
//					if (salonName.equals(tabbedPaneSalon.getTitleAt(i))){
//						indiceOnglet = i;
//					}
//				}
//				defaultBackColor = tabbedPaneSalon.getBackgroundAt(indiceOnglet); //On sauvegarde la couleur de fond
//				tabbedPaneSalon.setBackgroundAt(indiceOnglet,Color.green);
//				tabbedPaneSalon.requestFocusInWindow();
			
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				System.out.println("Changement ???");
			}
		});
			
		
				

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(textArea, popupMenu);

		JCheckBoxMenuItem chckbxmntmLock = new JCheckBoxMenuItem(Messages.getString("SimpleChatFrameClient.10")); //$NON-NLS-1$
		chckbxmntmLock.setEnabled(this.isScrollLocked);
		popupMenu.add(chckbxmntmLock);


		scrollPaneText.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (SimpleChatFrameClient.this.isScrollLocked) {
					e.getAdjustable().setValue(e.getAdjustable().getMaximum());
				}
			}
		});

		splitPane.setRightComponent(scrollPaneText);
	}


	protected void gereColorisationOnglet(String salonToColor) {
		int idPositionOnglet = 0;
		System.out.println("salon to color : " + salonToColor);
		// Recherche du salon concern�.
		for (int i = 0; i < this.tabbedPaneSalon.getTabCount(); i++) {
			//if(this.tabbedPaneSalon.getTitleAt(i).equals(SimpleChatFrameClient.this.salonName)){
				// Peg--> salonName doit �tre celui de l'eventSalonNewMsg transmis � l'appel de la fonction
			if(this.tabbedPaneSalon.getTitleAt(i).equals(salonToColor)){
				idPositionOnglet = i;
			}
		}
		// Couleur en cours de l'onglet
		Color colorOnglet = this.tabbedPaneSalon.getForegroundAt(idPositionOnglet);
		// On change la couleur de l'onglet si on a l'autorisation et si c'est utile
		if (SimpleChatFrameClient.this.isChangeColorNeeded) {
			if(colorOnglet != Color.RED){	
				System.out.println("Chgt Couleur de fond  de l'onglet n� " +idPositionOnglet );
				this.tabbedPaneSalon.setForegroundAt(idPositionOnglet, Color.RED);
				//reinit
				//SimpleChatFrameClient.this.isChangeColorNeeded = false;
			}
		} else {
			
			if (colorOnglet != SimpleChatFrameClient.this.defaultForeColor){
//				//tests : peggy	
				//si rouge, si s�lection alors decolorise
				idPositionOnglet = this.tabbedPaneSalon.getSelectedIndex();
				this.deColoreOnglet	(idPositionOnglet);
				System.out.println("Pas en d�faut - Chgt Couleur de fond n� " +idPositionOnglet );
				//this.tabbedPaneSalon.setForegroundAt(idPositionOnglet, SimpleChatFrameClient.this.defaultForeColor);
			}
		
		}
		this.tabbedPaneSalon.requestFocusInWindow();
		//r�init des compteurs d'onglets
		idPositionOnglet = 0;
		
	}

	protected void deColoreOnglet(int idPositionOnglet) {
		//Color colorOnglet = this.tabbedPaneSalon.getForegroundAt(idPositionOnglet);
		int id = this.tabbedPaneSalon.getSelectedIndex();
		if(idPositionOnglet==id){
		
		//if (colorOnglet != SimpleChatFrameClient.this.defaultForeColor){
			System.out.println("On d�colore l'onglet en cours n� " +id );
			this.tabbedPaneSalon.setForegroundAt(id, SimpleChatFrameClient.this.defaultForeColor);
	//	}
		this.tabbedPaneSalon.requestFocusInWindow();
		id=0;
	}
	}


	public void creationMenuFichier(JMenuBar menuBar) {
		JMenu mnFile = new JMenu(Messages.getString("SimpleChatFrameClient.5")); //$NON-NLS-1$
		mnFile.setMnemonic('F');
		menuBar.add(mnFile);

		JMenuItem mntmEnregistrerSous = new JMenuItem(Messages.getString("SimpleChatFrameClient.6")); //$NON-NLS-1$
		mnFile.add(mntmEnregistrerSous);
	}

	public void creationMenuOutils(JMenuBar menuBar) {
		JMenu mnOutils = new JMenu(Messages.getString("SimpleChatFrameClient.7")); //$NON-NLS-1$
		mnOutils.setMnemonic('O');
		menuBar.add(mnOutils);

		JMenuItem mntmEnvoyer = new JMenuItem(Messages.getString("SimpleChatFrameClient.8")); //$NON-NLS-1$
		mntmEnvoyer.setAction(this.sendAction);
		mnOutils.add(mntmEnvoyer);

		//inserted by : PEGGY  [18 Avr. 2017] : Ajout sous_menu de suppression du salon actif
		JMenuItem mntmSupprimer = new JMenuItem(Messages.getString("SimpleChatFrameClient.15")); //$NON-NLS-1$

		mntmSupprimer.setAction(this.closeSalonAction);
		mnOutils.add(mntmSupprimer);

		JSeparator separator = new JSeparator();
		mnOutils.add(separator);
		JCheckBoxMenuItem chckbxmntmNewCheckItem = new JCheckBoxMenuItem(this.lockAction);
		mnOutils.add(chckbxmntmNewCheckItem);

	}


	public JLabel getLblSender() {
		return this.lblSender;
	}

	public void sendMessage(int actionToPerform) {
		String nomSalonEncours = this.getLblSender().getText();
		int idSalonEncours = this.listSalon.retrieveIdSalon(nomSalonEncours);
		switch (actionToPerform) {
		case SEND_MESSAGE:// On envoie un message
			// TODO (inserted by : JFYVART / [7 avr. 2017, 08:47:51]
			// Si le salon existe : c'est une conversation normale
			if (!this.validateCreationNomSalon(nomSalonEncours)){
				this.sender.setMsgToSend(this.textField.getText(), idSalonEncours, "", "", "");
			} else // C'est une conversation priv�e aver un utilisateur donn�.
			{
				this.sender.setMsgToSend(this.textField.getText(), idSalonEncours, "", ClientServerProtocol.NVMSGPRIVE, nomSalonEncours);

			}
			this.textField.setText("");
			break;

		case CREATE_SALON:// On veut un nouveau salon

			if (this.validateCreationNomSalon(this.nouveauNomSalonSaisi)){

				this.sender.setMsgToSend("Cr�ation d'un salon", idSalonEncours, this.nouveauNomSalonSaisi,
						ClientServerProtocol.NVSALON, "");
			}
			// inserted by : PEGGY  [18 Avr. 2017] : Vider le champ de saisie du nouveau salon lorsque transmis au serveur
			this.txtCrerUnSalon.setText("");
			break;

		case CLOSE_SALON:// On ferme le salon
			// inserted by : PEGGY  [18 Avr. 2017] : on passe ici si clic sur "Fermeture salon"
			System.out.println("salon concern� :" + nomSalonEncours +" " + idSalonEncours);
			// On avertit le Thread qu'on supprime un salon
			this.sender.setMsgToSend("Fermeture du salon", idSalonEncours, nomSalonEncours, ClientServerProtocol.QUITSALON, "");
			this.supprSalon(nomSalonEncours);

			break;

		default:
			break;
		}

	}

	/***
	 *  V�rifie si un salon ne porte pas d�j� le m�me nom
	 *  - Le nom du salon ne doit pas �tre celui d'un salon existant
	 *  - le nom du salon ne doit pas �tre celui de l'utilisateur (msg vers lui m�me)
	 *  -
	 * @return
	 */
	public boolean validateCreationNomSalon(String nomSalon){
		boolean result = true;

		// Si le salon existe on interdit la cr�ation du salon (autre que celui g�n�ral).
		if(this.listSalon.isSalonNameUsed(nomSalon)) {
			result = false;
		}
		if (nomSalon.equals(this.login)){
			result = false;
		}
		return result;

	}

	/***
	 * On cr�e un nouvel Onglet � partir des donn�es pass�es par le Thread la
	 * m�thode est appel�e par la classe DefaultListSalonModel (Design pattern
	 * Observer)
	 *
	 * @param event
	 */
	public void addSalon(EventSalonADD event) {
		System.out.println("Ajout du salon :" + event.getSalon().getNomSalon());
		// On positionne le DiscussionSalon sur celui que l'on nous passe (Thread ClientToServcer)
		this.discussionDuSalonCree = event.getDiscussionSalon();
		// On cr�e l'onglet en lui donnant une liste d'utilisateurs et un document model qui lui seront propres.
		this.createOngletSalon(this.discussionDuSalonCree.getDocumentModel(), this.tabbedPaneSalon, event.getSalon(),
				this.discussionDuSalonCree.getClientListModel());
		// On rajoute le nouveau salon � la liste des salons g�r�e par ce client
		this.listSalon.createOrRetrieveSalon(event.getSalon().getNomSalon(), SalonLst.DEFAULT_SALON_NOT_PRIVACY);

	}
	
	// TODO (inserted by : JFYVART / [9 avr. 2017, 14:52:08]
	/**
	 *  On supprime un onglet  : la
	 * m�thode est appel�e par la classe DefaultListSalonModel (Design pattern
	 * Observer)
	 * @param event
	 */

	public void supprSalon(EventSalonSUPPR event) {
		System.out.println("Fermeture du salon :" + event.getSalon().getNomSalon());
	}

	public void colorSalon(EventSalonNewMsg event) {
		if(event.getIsSalonToColor()){
			System.out.println("Autorisation colorisation");
			this.isChangeColorNeeded = event.getIsSalonToColor();
			//Peg : transmettre le nom du salon � la fonction de colorisation
			System.out.println("salon transmis � colorisation : " + event.getNameSalon());
			this.gereColorisationOnglet(event.getNameSalon());
		} else {
			System.out.println("Interdiction colorisation");
//			//tests : peggy
//			event.setIsSalonToColor(false);
//			this.deColoreOnglet(this.tabbedPaneSalon.getSelectedIndex());
		}
//		this.isChangeColorNeeded = event.getIsSalonToColor();
//		this.gereColorisationOnglet();
	}

	// inserted by : PEGGY  [18 Avr. 2017] : suppression onglet salon actif
	public void supprSalon(String salonASupprimer) {

		int indexSuppr = this.tabbedPaneSalon.getSelectedIndex();
		if (indexSuppr==SalonLst.DEFAULT_SALON_ID) {
			System.out.println("suppression Salon G�n�ral impossible");
		}
		else {
			System.out.println("Fermeture salon :" + salonASupprimer + " - Index Tab : " + indexSuppr);
			this.tabbedPaneSalon.remove(indexSuppr);
			this.listSalon.deleteSalon(salonASupprimer);
			// Focus onglet
			this.tabbedPaneSalon.setSelectedIndex(SalonLst.DEFAULT_SALON_ID);
			this.getLblSender().setText(SalonLst.DEFAULT_SALON_NAME);

		}

	}

}