package com.cfranc.irc.ui;

import org.junit.BeforeClass;
import org.junit.Test;

import com.cfranc.irc.server.Salon;
import com.cfranc.irc.server.SalonLst;
import com.cfranc.irc.ui.SimpleChatServerApp;

public class TstCreateOriginal {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void test() {

					SimpleChatServerApp app = new SimpleChatServerApp(4567);	
					
					SalonLst salon0 = app.mySalons;
					Salon objO = (Salon) salon0.get(0);
					String val0=objO.getNomSalon();
					
					System.out.println("Nom salon: "+val0);
					
					// recupe id salon
//					DefaultListModel<String> listClientConnect = new DefaultListModel();
//					
//					listClientConnect = app.clientListModel;
//					
//					User objUser = listClientConnect.get(0);
//					
//					System.out.println(objUser.getIdSalon());
					
					
		
	}

}
