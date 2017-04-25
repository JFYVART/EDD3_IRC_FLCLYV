package com.cfranc.irc.client;

import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.cfranc.irc.ui.SimpleChatFrameClient;

public class EmoticonManager {

	private ArrayList<Emoticon> listEmoticon;

	public static final String EMOTICON_SMILE = "EMOTICON_SMILE";
	public static final String EMOTICON_PLEURER = "EMOTICON_PLEURER";
	private static final String EMOTICON_AIMER = "EMOTICON_AIMER";
	private static final String EMOTICON_BISOU = "EMOTICON_BISOU";
	private static final String EMOTICON_Evil = "EMOTICON_Evil";

	public class Emoticon{
		private String chaine;
		private String nom;
		private String iconeFileName;

		public Emoticon(String chaine, String nom, String iconeFileName) {
			this.chaine = chaine;
			this.nom= nom;
			this.iconeFileName = iconeFileName;
		}

		public String getChaine() {
			return this.chaine;
		}

		public void setChaine(String chaine) {
			this.chaine = chaine;
		}

		public String getNom() {
			return this.nom;
		}

		public void setNom(String nom) {
			this.nom = nom;
		}

		public  String getIconeFileName() {
			return this.iconeFileName;
		}

		public void setIconeFileName(String iconeFileName) {
			this.iconeFileName = iconeFileName;
		}

	}

	public EmoticonManager() {
		// TODO Auto-generated constructor stub
	}



	public ArrayList<Emoticon> getListEmoticon(){

		if (this.listEmoticon== null){
			this.listEmoticon = new ArrayList<Emoticon>();
			this.listEmoticon.add(this.getEmoticonHappy());
			this.listEmoticon.add(this.getEmoticonPleure());
			this.listEmoticon.add(this.getEmoticonAime());
			this.listEmoticon.add(this.getEmoticonBisou());
			this.listEmoticon.add(this.getEmoticonEvil());
		}

		return this.listEmoticon;
	}

	public boolean isEmoticonInChain(String str){
		boolean result = false;
		for (Emoticon emoticon : this.getListEmoticon()) {
			if(this.findPositionEmoticonInChain(emoticon, str) >=0){
				result = true;
			}
		}

		return result;
	}



	public int findPositionEmoticonInChain(Emoticon emoticon, String str){
		return str.indexOf(emoticon.getChaine());
	}


	public void insertIcon(StyledDocument documentModelOnglet) {
		// TODO Auto-generated method stubStyledDocument doc = (StyledDocument) p.getDocument();
		try {
			String text = documentModelOnglet.getText(0, documentModelOnglet.getLength());
			System.out.println("text reçu par InsertIcon :" + text);

			// boucle de traitement des émoticones (la liste est créée dans le getListEmoticon)
			for (Emoticon emoticon : this.getListEmoticon()) {
				int index = this.findPositionEmoticonInChain(emoticon, text);
				int start = 0;
				if (index >=0) {
					while (index > -1) {
						Element el = documentModelOnglet.getCharacterElement(index);
						if (StyleConstants.getIcon(el.getAttributes()) == null) {
							documentModelOnglet.remove(index, 2);
							SimpleAttributeSet attrs = new SimpleAttributeSet();
							StyleConstants.setIcon(attrs, this.insererIconeImg(emoticon.getIconeFileName()));
							documentModelOnglet.insertString(index, emoticon.getChaine(), attrs);
						}
						start = index + 2;
						index = text.indexOf(emoticon.getChaine(), start);
					}
				}
			}

		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public Emoticon getEmoticonHappy(){
		return new Emoticon(":)",EMOTICON_SMILE,"happy.jpg");
	}

	public Emoticon getEmoticonPleure(){
		return new Emoticon(":(",EMOTICON_PLEURER,"pleure.jpg");
	}

	public Emoticon getEmoticonAime(){
		return new Emoticon(":L",EMOTICON_AIMER,"aime.jpg");
	}
	
	public Emoticon getEmoticonBisou(){
		return new Emoticon(":K",EMOTICON_BISOU,"bisou.jpg");
	}
	
	public Emoticon getEmoticonEvil(){
		return new Emoticon(":E",EMOTICON_Evil,"diable.jpg");
	}
	
	public Icon insererIconeImg(String cheminImage){
		Icon imageIcon = (new ImageIcon(SimpleChatFrameClient.class.getResource(cheminImage)));
		return imageIcon;
	}


}
