package com.cfranc.irc.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.cfranc.irc.server.BroadcastThread;
import com.cfranc.irc.server.Salon;
import com.cfranc.irc.server.SalonLst;
import com.cfranc.irc.server.ServerToClientThread;
import com.cfranc.irc.server.User;

public class SimpleChatFrameServer extends JFrame {

	public StyledDocument model = null;
	public DefaultListModel<String> clientListModel = null;
	public SalonLst serverSalon = null;
	private JTree tree;

	public SimpleChatFrameServer(int port, StyledDocument model, DefaultListModel<String> clientListModel) {
		super("ISM - IRC Server Manager");
		this.model = model;
		this.clientListModel = clientListModel;
		serverSalon = new SalonLst();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.setBounds(100, 100, 702, 339);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		JTextPane textPane = new JTextPane(model);
		scrollPane.setViewportView(textPane);
		scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				e.getAdjustable().setValue(e.getAdjustable().getMaximum());

			}
		});

		JScrollPane scrollPaneList = new JScrollPane();
		getContentPane().add(scrollPaneList, BorderLayout.WEST);

		final JLabel statusBar = new JLabel("");
		getContentPane().add(statusBar, BorderLayout.SOUTH);

		// Affichage d'un arbre des Salons et utilisateurs connectés

		
		  final JList<String> list = new JList<String>(clientListModel);
		  list.addListSelectionListener(new ListSelectionListener() { public
		  void valueChanged(ListSelectionEvent e) { String clientSelected =
		  list.getSelectedValue().toString();
		  statusBar.setText(clientSelected); } }); list.setMinimumSize(new
		  Dimension(200, 0)); 
		  scrollPaneList.setViewportView(list);
		 

//		  DefaultMutableTreeNode top = new DefaultMutableTreeNode("Liste des salons");
//			tree = new JTree(top);
//			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
//			tree.addTreeSelectionListener(new TreeSelectionListener() {
//				public void valueChanged(TreeSelectionEvent e) {
//					for (int i = 0; i < BroadcastThread.mySalons.getLstSalons().size(); i++) {
//						// Récupération du nom du salon
//						Salon salonEncours = BroadcastThread.mySalons.getLstSalons().get(i);
//						DefaultMutableTreeNode salonNode = new DefaultMutableTreeNode(salonEncours.getNomSalon());
//						// Ajout du nom du salon à l'arbre
//						top.add(salonNode);
//						// A partir de l'id du salon en cours, on récupère le hashmap (User + Thread Server to client) 
//						HashMap<User, ServerToClientThread> ListThread = BroadcastThread.getClientTreadsMap(BroadcastThread.mySalons.retrieveIdSalon(salonEncours.getNomSalon()));
//						// On en extrait chaque utilisateur
//						for (Entry<User, ServerToClientThread> entry : ListThread.entrySet()) {
//
//							DefaultMutableTreeNode utilisateurNode = new DefaultMutableTreeNode(entry.getKey().getLogin());
//							salonNode.add(utilisateurNode);
//						}
//						// On ajoute le nom de chaque utilisateur au Salon Node
//						// 
//						
//					}
//				}
//			});
//			
//			
//			// Create the scroll pane and add the tree to it.
//			JScrollPane treeView = new JScrollPane(tree);
//			// Mise du treeView à gauche
//			getContentPane().add(treeView, BorderLayout.WEST);
//			// splitPane.setTopComponent(treeView);
	}
}