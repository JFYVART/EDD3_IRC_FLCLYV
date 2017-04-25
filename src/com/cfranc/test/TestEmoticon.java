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
		String msg = "Mon test comportant l'émoticone Happy :) mais pas que ça !!!";
		EmoticonManager eManager = new EmoticonManager();
		assertTrue(eManager.isEmoticonInChain(msg));
	}


	@Test
	public void test_findPositionEmoticonInChain() {
		String nomUser = "Test";
		String msg = "Mon tests comportant l'émoticone Happy :) mais pas que ça !!!";
		EmoticonManager eManager = new EmoticonManager();
		Emoticon happy = eManager.getEmoticonHappy();
		assertTrue(39 ==eManager.findPositionEmoticonInChain(happy,msg));
	}



	



}
