package com.cfranc.irc.ui;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
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
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

import com.cfranc.irc.ClientServerProtocol;
import com.cfranc.irc.client.DefaultListSalonModel;
import com.cfranc.irc.client.EventSalonADD;
import com.cfranc.irc.client.EventSalonSUPPR;
import com.cfranc.irc.client.IfSenderModel;
import com.cfranc.irc.server.Salon;
import com.cfranc.irc.server.SalonLst;

public class SimpleChatFrameClient extends JFrame {

	private static Document documentModel;
	private static ListModel<String> listModel;
	private DefaultListSalonModel salonListModel;
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
	private AddNewSalonFrame newSalonFrame;
	JTextField txtCrerUnSalon;
	// private JTextField textField_NewSalon;

	/**
	 * Launch the application.
	 * 
	 * @throws BadLocationException
	 */
	public static void main(String[] args) throws BadLocationException {
		EventQueue.invokeLater(new Runnable() {
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

	public static void sendMessage(String user, String line, Style styleBI, Style styleGP) {
		try {
			documentModel.insertString(documentModel.getLength(), user + " : ", styleBI); //$NON-NLS-1$
			documentModel.insertString(documentModel.getLength(), line + "\n", styleGP); //$NON-NLS-1$
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void sendMessage(int actionToPerform) {
		switch (actionToPerform) {
		case 0:// On envoie un message
			sender.setMsgToSend(textField.getText(), 0,"", "","");
			break;
		case 1:// On veut un nouveau salon
			sender.setMsgToSend("Création d'un salon", 0,nouveauNomSalonSaisi,ClientServerProtocol.NVSALON,"");
			break;

		case 2:// On ferme le salon
			sender.setMsgToSend("Fermeture du salon",0,"",ClientServerProtocol.QUITSALON,"");
			break;	
		default:
			break;
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

		newSalonFrame= new AddNewSalonFrame();
		newSalonFrame.setVisible(false);
		
		setTitle(Messages.getString("SimpleChatFrameClient.4")); //$NON-NLS-1$
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 545, 300);
		/**
		 * Create menuBar
		 */
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		creationMenuFichier(menuBar);
		creationMenuOutils(menuBar);
		creationMenuSalon(menuBar);
		/**
		 * Ajout Onglet pour Salon
		 */
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		tabbedPaneSalon = new JTabbedPane(JTabbedPane.TOP);

		// tabbedPaneSalon.setToolTipText(Messages.getString("SimpleChatFrameClient.tabbedPane.toolTipText"));
		// //$NON-NLS-1$
		contentPane.add(tabbedPaneSalon, BorderLayout.CENTER);

		// On examine le contenu de salonListModel :
		if (salonListModel.getSize() > 0) { // S'il y a des salons
			for (int i = 0; i < salonListModel.getSize(); i++) {
				// On crée un onglet par salon
				Salon salon = salonListModel.getElementAt(i);
				createOngletSalon(documentModel, tabbedPaneSalon, salon);
			}
		} else { // Pas de salon au lancement : on en crée un onglet par défaut,
					// on l'ajoute à la liste et on ouvre un onglet
			Salon salon = new Salon(SalonLst.DEFAULT_SALON_NAME, SalonLst.DEFAULT_SALON_NOT_PRIVACY);
			salonListModel.addElement(salon);
			createOngletSalon(documentModel, tabbedPaneSalon, salon);
		}

		JToolBar toolBar = new JToolBar();
        contentPane.add(toolBar, BorderLayout.NORTH);

        JButton button = toolBar.add(sendAction);
        
                Label label = new Label(Messages.getString("SimpleChatFrameClient.label.text")); //$NON-NLS-1$
                label.setAlignment(Label.RIGHT);
                toolBar.add(label);
        
        txtCrerUnSalon = new JTextField();
        txtCrerUnSalon.setFont(txtCrerUnSalon.getFont().deriveFont(txtCrerUnSalon.getFont().getStyle() | Font.ITALIC, 9f));
        //txtCrerUnSalon.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
        //        Messages.getString(salonName);

        toolBar.add(txtCrerUnSalon);
        txtCrerUnSalon.setColumns(10);
        
        JButton btnNewButton = new JButton(newSalondAction);
        btnNewButton.setText("Nouveau salon"); //$NON-NLS-1$
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
    
            	nouveauNomSalonSaisi = txtCrerUnSalon.getText();
            }
        });
        toolBar.add(btnNewButton);
				// On met en place la Combo Box pour le choix du Salon
//		Choice choiceSalon = new Choice();
		//toolBar.add(choiceSalon);
		// doit afficher la liste des salons
		//SCLU
//		JComboBox choixSalon=new JComboBox();
//		toolBar.add(choixSalon);

		panelPiedPage = new JPanel();
		contentPane.add(panelPiedPage, BorderLayout.SOUTH);
		panelPiedPage.setLayout(new BorderLayout(0, 0));
		JPanel panel = new JPanel();
		panelPiedPage.add(panel);

		// zone de gestion des messages
		lblSender = new JLabel("?"); //$NON-NLS-1$
		lblSender.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSender.setHorizontalTextPosition(SwingConstants.CENTER);
		lblSender.setPreferredSize(new Dimension(100, 14));
		lblSender.setMinimumSize(new Dimension(100, 14));

		textField = new JTextField();
		textField.setHorizontalAlignment(SwingConstants.LEFT);
		textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				Messages.getString("SimpleChatFrameClient.12")); //$NON-NLS-1$
		textField.getActionMap().put(Messages.getString("SimpleChatFrameClient.13"), sendAction); //$NON-NLS-1$

		JButton btnSend = new JButton(sendAction);
		btnSend.setMnemonic(KeyEvent.VK_ENTER);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
						.addComponent(lblSender, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(textField, GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(btnSend)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel
				.createSequentialGroup().addGap(10)
				.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(textField, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
						.addComponent(lblSender, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
						.addComponent(btnSend, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))));
		panel.setLayout(gl_panel);

	}

	public void createOngletSalon(Document documentModel, JTabbedPane tabbedPaneSalon, Salon salon) {

		JPanel panelSalon = new JPanel();

		salonName = salon.getNomSalon();

		tabbedPaneSalon.addTab(salonName.toString(), null, panelSalon, null);

		panelSalon.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		panelSalon.add(splitPane, BorderLayout.CENTER);

		JList<String> list = new JList<String>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int iFirstSelectedElement = ((JList) e.getSource()).getSelectedIndex();
				if (iFirstSelectedElement >= 0 && iFirstSelectedElement < listModel.getSize()) {
					senderName = listModel.getElementAt(iFirstSelectedElement);
					getLblSender().setText(senderName);
				} else {
					getLblSender().setText("?"); //$NON-NLS-1$
				}
			}
		});
		list.setMinimumSize(new Dimension(100, 0));
		splitPane.setLeftComponent(list);

		JTextPane textArea = new JTextPane((StyledDocument) documentModel);
		textArea.setEnabled(false);
		JScrollPane scrollPaneText = new JScrollPane(textArea);

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(textArea, popupMenu);

		JCheckBoxMenuItem chckbxmntmLock = new JCheckBoxMenuItem(Messages.getString("SimpleChatFrameClient.10")); //$NON-NLS-1$
		chckbxmntmLock.setEnabled(isScrollLocked);
		popupMenu.add(chckbxmntmLock);

		JCheckBoxMenuItem chckbxmntmFermerSalon = new JCheckBoxMenuItem(
				Messages.getString("SimpleChatFrameClient.chckbxmntmNewCheckItem_1.text")); //$NON-NLS-1$
		popupMenu.add(chckbxmntmFermerSalon);
		chckbxmntmLock.addActionListener(lockAction);

		scrollPaneText.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (isScrollLocked) {
					e.getAdjustable().setValue(e.getAdjustable().getMaximum());
				}
			}
		});

		splitPane.setRightComponent(scrollPaneText);
	}

	// public void createOngletSalon(Document documentModel, String salonName)
	// {}

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
		
//		mntmCreateSalon.addFocusListener(new FocusAdapter() {
//			@Override
//			public void focusGained(FocusEvent e) {
//			}
//		});
//		mntmCreateSalon.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				newSalonFrame.setVisible(true);
//				nouveauNomSalonSaisi = newSalonFrame.newSalon;
//				System.out.println("Nom du nouveau salon demandé : " + nouveauNomSalonSaisi);
//			}
//		});

		mntmCreateSalon.setAction(newSalondAction);
		mnSalon.add(mntmCreateSalon);
		
		JMenuItem mntmFermerSalon = new JMenuItem(Messages.getString("SimpleChatFrameClient.mntmNewMenuItem.text")); //$NON-NLS-1$
		mntmFermerSalon.setAction(closeSalonAction);
		mnSalon.add(mntmFermerSalon);
	}

	public void creationMenuOutils(JMenuBar menuBar) {
		JMenu mnOutils = new JMenu(Messages.getString("SimpleChatFrameClient.7")); //$NON-NLS-1$
		mnOutils.setMnemonic('O');
		menuBar.add(mnOutils);

		JMenuItem mntmEnvoyer = new JMenuItem(Messages.getString("SimpleChatFrameClient.8")); //$NON-NLS-1$
		mntmEnvoyer.setAction(sendAction);
		mnOutils.add(mntmEnvoyer);

		JSeparator separator = new JSeparator();
		mnOutils.add(separator);
		JCheckBoxMenuItem chckbxmntmNewCheckItem = new JCheckBoxMenuItem(lockAction);
		mnOutils.add(chckbxmntmNewCheckItem);
	}

	public void creationMenuFichier(JMenuBar menuBar) {
		JMenu mnFile = new JMenu(Messages.getString("SimpleChatFrameClient.5")); //$NON-NLS-1$
		mnFile.setMnemonic('F');
		menuBar.add(mnFile);

		JMenuItem mntmEnregistrerSous = new JMenuItem(Messages.getString("SimpleChatFrameClient.6")); //$NON-NLS-1$
		mnFile.add(mntmEnregistrerSous);
	}

	public JLabel getLblSender() {
		return lblSender;
	}

	private abstract class ResourceAction extends AbstractAction {
		public ResourceAction() {
		}
	}

	private class SendAction extends ResourceAction {
		private Icon getIcon() {
			return new ImageIcon(SimpleChatFrameClient.class.getResource("send_16_16.jpg")); //$NON-NLS-1$
		}

		public SendAction() {
			putValue(NAME, Messages.getString("SimpleChatFrameClient.3")); //$NON-NLS-1$
			putValue(SHORT_DESCRIPTION, Messages.getString("SimpleChatFrameClient.2")); //$NON-NLS-1$
			putValue(SMALL_ICON, getIcon());
		}

		public void actionPerformed(ActionEvent e) {
			sendMessage(0);
		}
	}

	private class NewSalonAction extends ResourceAction {
		private Icon getIcon() {
			return new ImageIcon(SimpleChatFrameClient.class.getResource("send_16_16.jpg")); //$NON-NLS-1$
		}

		public NewSalonAction() {
			putValue(NAME, Messages.getString("SimpleChatFrameClient.9")); //$NON-NLS-1$
			putValue(SHORT_DESCRIPTION, Messages.getString("SimpleChatFrameClient.14")); //$NON-NLS-1$
			putValue(SMALL_ICON, getIcon());
		}

		public void actionPerformed(ActionEvent e) {
			sendMessage(1);
		}
	}
	private class CloseSalonAction extends ResourceAction {
		private Icon getIcon() {
			return new ImageIcon(SimpleChatFrameClient.class.getResource("send_16_16.jpg")); //$NON-NLS-1$
		}

		public CloseSalonAction() {
			putValue(NAME, Messages.getString("SimpleChatFrameClient.11")); //$NON-NLS-1$
			putValue(SHORT_DESCRIPTION, Messages.getString("SimpleChatFrameClient.15")); //$NON-NLS-1$
			putValue(SMALL_ICON, getIcon());
		}

		public void actionPerformed(ActionEvent e) {
			sendMessage(2);
		}
	}
	
	
	private class LockAction extends ResourceAction {
		public LockAction() {
			putValue(NAME, Messages.getString("SimpleChatFrameClient.1")); //$NON-NLS-1$
			putValue(SHORT_DESCRIPTION, Messages.getString("SimpleChatFrameClient.0")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			isScrollLocked = (!isScrollLocked);
		}
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	
	public void addSalon(EventSalonADD event){
		System.out.println("Ajout du salon :" + event.getSalon().getNomSalon());
		createOngletSalon(new DefaultStyledDocument(), tabbedPaneSalon, event.getSalon());
	}
	
	public void supprSalon(EventSalonSUPPR event){
		System.out.println("Suppression du salon :" + event.getSalon().getNomSalon());
		
	}
	
}