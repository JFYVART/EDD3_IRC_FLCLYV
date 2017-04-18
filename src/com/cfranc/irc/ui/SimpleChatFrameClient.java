package com.cfranc.irc.ui;

import java.awt.BorderLayout;
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

	// public void createOngletSalon(Document documentModel, String salonName)
	// {}

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
			SimpleChatFrameClient.this.sendMessage(2);
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
			SimpleChatFrameClient.this.sendMessage(1);
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
			SimpleChatFrameClient.this.sendMessage(0);
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
				new DefaultListSalonModel());
	}

	/**
	 * Create the frame with salonName
	 */

	public SimpleChatFrameClient(IfSenderModel sender, ListModel<String> clientListModel, Document documentModel,
			DefaultListSalonModel salonListModel) {
		this.sender = sender;
		this.documentModel = documentModel;
		this.listModel = clientListModel;
		this.salonListModel = salonListModel;
		this.salonListModel.addObserver(this);

		this.newSalonFrame = new AddNewSalonFrame();
		this.newSalonFrame.setVisible(false);

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
		this.creationMenuSalon(menuBar);
		/**
		 * Ajout Onglet pour Salon
		 */
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		this.setContentPane(this.contentPane);

		this.tabbedPaneSalon = new JTabbedPane(JTabbedPane.TOP);

		// tabbedPaneSalon.setToolTipText(Messages.getString("SimpleChatFrameClient.tabbedPane.toolTipText"));
		// //$NON-NLS-1$
		this.contentPane.add(this.tabbedPaneSalon, BorderLayout.CENTER);

		// On examine le contenu de salonListModel :
		if (salonListModel.getSize() > 0) { // S'il y a des salons
			for (int i = 0; i < salonListModel.getSize(); i++) {
				// On crée un onglet par salon
				Salon salon = salonListModel.getElementAt(i);
				this.createOngletSalon(documentModel, this.tabbedPaneSalon, salon, new DefaultListModel<String>());
			}
		} else { // Pas de salon au lancement : on en crée un onglet par défaut,
			// on l'ajoute à la liste et on ouvre un onglet
			Salon salon = new Salon(SalonLst.DEFAULT_SALON_NAME, SalonLst.DEFAULT_SALON_NOT_PRIVACY,
					SalonLst.DEFAULT_SALON_ID);
			salonListModel.addElement(salon);
			this.createOngletSalon(documentModel, this.tabbedPaneSalon, salon, clientListModel);
		}

		JToolBar toolBar = new JToolBar();
		this.contentPane.add(toolBar, BorderLayout.NORTH);

		JButton button = toolBar.add(this.sendAction);

		Label label = new Label(Messages.getString("SimpleChatFrameClient.label.text")); //$NON-NLS-1$
		label.setAlignment(Label.RIGHT);
		toolBar.add(label);

		this.txtCrerUnSalon = new JTextField();
		this.txtCrerUnSalon.setFont(
				this.txtCrerUnSalon.getFont().deriveFont(this.txtCrerUnSalon.getFont().getStyle() | Font.ITALIC, 9f));
		// txtCrerUnSalon.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
		// 0),
		// Messages.getString(salonName);

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
		// On met en place la Combo Box pour le choix du Salon
		// Choice choiceSalon = new Choice();
		// toolBar.add(choiceSalon);
		// doit afficher la liste des salons
		// SCLU
		// JComboBox choixSalon=new JComboBox();
		// toolBar.add(choixSalon);

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
				// On mets à jour le nom du salon actif quand on change d'onglet
				// => Libellé devant la zone de saisie
				SimpleChatFrameClient.this.lblSender.setText(tabbedPaneSalon.getSelectedComponent().getName());
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

		// On mets à jour le nom du salon dans l'onglet et dans le libellé
		// devant la zonbe de saisie (Création de l'onglet)
		this.salonName = salon.getNomSalon();
		panelSalon.setName(salon.getNomSalon());

		tabbedPaneSalon.addTab(this.salonName.toString(), null, panelSalon, null);

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

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(textArea, popupMenu);

		JCheckBoxMenuItem chckbxmntmLock = new JCheckBoxMenuItem(Messages.getString("SimpleChatFrameClient.10")); //$NON-NLS-1$
		chckbxmntmLock.setEnabled(this.isScrollLocked);
		popupMenu.add(chckbxmntmLock);

		JCheckBoxMenuItem chckbxmntmFermerSalon = new JCheckBoxMenuItem(
				Messages.getString("SimpleChatFrameClient.chckbxmntmNewCheckItem_1.text")); //$NON-NLS-1$
		popupMenu.add(chckbxmntmFermerSalon);
		chckbxmntmLock.addActionListener(this.lockAction);

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

		JSeparator separator = new JSeparator();
		mnOutils.add(separator);
		JCheckBoxMenuItem chckbxmntmNewCheckItem = new JCheckBoxMenuItem(this.lockAction);
		mnOutils.add(chckbxmntmNewCheckItem);
	}

	public void creationMenuSalon(JMenuBar menuBar) {
		JMenu mnSalon;
		mnSalon = new JMenu(Messages.getString("SimpleChatFrameClient.mnSalon.text")); //$NON-NLS-1$
		mnSalon.setMnemonic('O');
		menuBar.add(mnSalon);

		JList list_salon = new JList();
		mnSalon.add(list_salon);
		// alimenter list

		JSeparator separator_1 = new JSeparator();
		mnSalon.add(separator_1);

		/**
		 * Ouvrir fenêtre création salon
		 */
		JMenuItem mntmCreateSalon = new JMenuItem(Messages.getString("SimpleChatFrameClient.mntmCrerUnNouveau.text")); //$NON-NLS-1$

		// mntmCreateSalon.addFocusListener(new FocusAdapter() {
		// @Override
		// public void focusGained(FocusEvent e) {
		// }
		// });
		// mntmCreateSalon.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// newSalonFrame.setVisible(true);
		// nouveauNomSalonSaisi = newSalonFrame.newSalon;
		// System.out.println("Nom du nouveau salon demandé : " +
		// nouveauNomSalonSaisi);
		// }
		// });

		mntmCreateSalon.setAction(this.newSalondAction);
		mnSalon.add(mntmCreateSalon);

		JMenuItem mntmFermerSalon = new JMenuItem(Messages.getString("SimpleChatFrameClient.mntmNewMenuItem.text")); //$NON-NLS-1$
		mntmFermerSalon.setAction(this.closeSalonAction);
		mnSalon.add(mntmFermerSalon);
	}

	public JLabel getLblSender() {
		return this.lblSender;
	}

	public void sendMessage(int actionToPerform) {
		String nomSalonEncours = this.getLblSender().getText();
		int idSalonEncours = this.listSalon.retrieveIdSalon(nomSalonEncours);
		switch (actionToPerform) {
		case 0:// On envoie un message
			// TODO (inserted by : JFYVART / [7 avr. 2017, 08:47:51]
			/***
			 * Gérer le premier message de l'utilisateur dans un salon.
			 */
			this.sender.setMsgToSend(this.textField.getText(), idSalonEncours, "", "", "");
			break;
		case 1:// On veut un nouveau salon
			this.sender.setMsgToSend("Création d'un salon", idSalonEncours, this.nouveauNomSalonSaisi,
					ClientServerProtocol.NVSALON, "");
			//// Vider le champ de saisie du nouveau salon lorsque transmis au serveur (Peggy 18/04)
			this.txtCrerUnSalon.setText("");
			break;

		case 2:// On ferme le salon
			this.sender.setMsgToSend("Fermeture du salon", idSalonEncours, "", ClientServerProtocol.QUITSALON, "");
			break;
		default:
			break;
		}

	}

	/***
	 * On crée un nouvel Onglet à partir des données passées par le Thread la
	 * méthode est appelée par la classe DefaultListSalonModel (Design pattern
	 * Observer)
	 *
	 * @param event
	 */
	public void addSalon(EventSalonADD event) {
		System.out.println("Ajout du salon :" + event.getSalon().getNomSalon());
		// On positionne le DiscussionSalon sur celui que l'on nous passe (Thread ClientToServcer)
		this.discussionDuSalonCree = event.getDiscussionSalon();
		// On crée l'onglet en lui donnant une liste d'utilisateurs et un document model qui lui seront propres.
		this.createOngletSalon(this.discussionDuSalonCree.getDocumentModel(), this.tabbedPaneSalon, event.getSalon(),
				this.discussionDuSalonCree.getClientListModel());
		// On rajoute le nouveau salon à la liste des salons gérée par ce client
		this.listSalon.createOrRetrieveSalon(event.getSalon().getNomSalon(), SalonLst.DEFAULT_SALON_NOT_PRIVACY);
		
	}

	// TODO (inserted by : JFYVART / [9 avr. 2017, 14:52:08]
	/**
	 *  On supprime un onglet  : la
	 * méthode est appelée par la classe DefaultListSalonModel (Design pattern
	 * Observer)
	 * @param event
	 */

	public void supprSalon(EventSalonSUPPR event) {
		System.out.println("Suppression du salon :" + event.getSalon().getNomSalon());

	}

}