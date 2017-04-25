package com.cfranc.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.cfranc.irc.client.EmoticonManager;
import com.cfranc.irc.client.EmoticonManager.Emoticon;

public class TestEmoticon {

	@Test
	public void test_isEmoticonInChain() {
		String nomUser = "Test";
		String msg = "Mon tests comportant l'émoticone Happy :) mais pas que ça !!!";
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
