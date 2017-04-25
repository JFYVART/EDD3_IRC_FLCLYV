package com.cfranc.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

import org.junit.Test;

import com.cfranc.irc.client.EmoticonManager;
import com.cfranc.irc.client.EmoticonManager.Emoticon;
import com.cfranc.irc.ui.SimpleChatClientApp;

public class TestEmoticon {

	@Test
	public void test_isEmoticonInChain() {
		String nomUser = "Test";
		String msg = "Mon test comportant l'�moticone Happy :) mais pas que �a !!!";
		EmoticonManager eManager = new EmoticonManager();
		assertTrue(eManager.isEmoticonInChain(msg));
	}


	@Test
	public void test_findPositionEmoticonInChain() {
		String nomUser = "Test";
		String msg = "Mon tests comportant l'�moticone Happy :) mais pas que �a !!!";
		EmoticonManager eManager = new EmoticonManager();
		Emoticon happy = eManager.getEmoticonHappy();
		assertTrue(39 ==eManager.findPositionEmoticonInChain(happy,msg));
	}



	@Test
	public void test_insertIcon() {
		String nomUser = "Test";
		String msg = "Mon test comportant l'�moticone Happy :) mais pas que �a !!! car il ya aussi l'�moticone Pleure :( ....";
		String  msgTraite = "";
		EmoticonManager eManager = new EmoticonManager();
		StyledDocument documentModel = new DefaultStyledDocument();;
		Style styleBI = documentModel.getStyle(SimpleChatClientApp.BOLD_ITALIC);
		Style styleGP = documentModel.getStyle(SimpleChatClientApp.GRAY_PLAIN);
		try {
			documentModel.insertString(documentModel.getLength(), nomUser + " : ", styleBI);
			documentModel.insertString(documentModel.getLength(), msg + "\n", styleGP);
			eManager.insertIcon(documentModel);
			msgTraite = documentModel.getText(0, documentModel.getLength());
			System.out.println("Msg trait� :" + msgTraite);
			assertTrue(!eManager.isEmoticonInChain(msgTraite));
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertFalse(Boolean.TRUE);
		}


	}



}
