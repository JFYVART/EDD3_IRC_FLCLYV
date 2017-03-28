package com.cfranc.irc;

import static org.junit.Assert.*;

import org.junit.Test;

public class TstClientServerProtocole {

	@Test
	public void test_encodeProtocole_Ligne_Login() {
		String line = "";
		String login ="Toto";
		String pwd = "";  
		String msg = "";
		String command = "";
		int salonId = 0;
		String salonName = "";
		
		line = IfClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName);
		assertEquals(login,IfClientServerProtocol.decodeProtocole_Login(line));
	}
	
	@Test
	public void test_encodeProtocole_Ligne_pwd() {
		String line = "";
		String login ="";
		String pwd = "pvd12345";  
		String msg = "";
		String command = "";
		int salonId = 0;
		String salonName = "";
		
		line = IfClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName);
		assertEquals(pwd,IfClientServerProtocol.decodeProtocole_PWD(line));
	}	
	
	@Test
	public void test_encodeProtocole_Ligne_Msg() {
		String line = "";
		String login ="";
		String pwd = "";  
		String msg = "Hello World !!!";
		String command = "";
		int salonId = 0;
		String salonName = "";
		
		line = IfClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName);
		assertEquals(msg,IfClientServerProtocol.decodeProtocole_Message(line));
	}
	
	@Test
	public void test_encodeProtocole_Ligne_Command() {
		String line = "";
		String login ="";
		String pwd = "";  
		String msg = "";
		String command = "-";
		int salonId = 0;
		String salonName = "";
		
		line = IfClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName);
		assertEquals(command,IfClientServerProtocol.decodeProtocole_Command(line));
	}
	
	@Test
	public void test_encodeProtocole_Ligne_SalonId() {
		String line = "";
		String login ="";
		String pwd = "";  
		String msg = "";
		String command = "";
		int salonId = 6;
		String salonName = "";
		
		line = IfClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName);
		assertEquals(salonId,IfClientServerProtocol.decodeProtocole_IdSalon(line));
	}	
	
	@Test
	public void test_encodeProtocole_Ligne_SalonName() {
		String line = "";
		String login ="";
		String pwd = "";  
		String msg = "";
		String command = "";
		int salonId = 0;
		String salonName = "Salon G�n�ral";
		
		line = IfClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName);
		assertEquals(salonName,IfClientServerProtocol.decodeProtocole_NomSalon(line));
	}
	
	@Test
	public void test_encodeProtocole_Ligne_Login_Vide() {
		String line = "";
		String login ="";
		String pwd = "";  
		String msg = "";
		String command = "";
		int salonId = 0;
		String salonName = "";
		
		line = IfClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName);
		assertEquals(login,IfClientServerProtocol.decodeProtocole_Login(line));
	}
	
	@Test
	public void test_encodeProtocole_Ligne_Pwd_Vide() {
		String line = "";
		String login ="";
		String pwd = "";  
		String msg = "";
		String command = "";
		int salonId = 0;
		String salonName = "";
		
		line = IfClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName);
		assertEquals(pwd,IfClientServerProtocol.decodeProtocole_PWD(line));
	}	
	
	@Test
	public void test_encodeProtocole_Ligne_Msg_Vide() {
		String line = "";
		String login ="";
		String pwd = "";  
		String msg = "";
		String command = "";
		int salonId = 0;
		String salonName = "";
		
		line = IfClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName);
		assertEquals(msg,IfClientServerProtocol.decodeProtocole_Message(line));
	}
	
	@Test
	public void test_encodeProtocole_Ligne_Command_Vide() {
		String line = "";
		String login ="";
		String pwd = "";  
		String msg = "";
		String command = "";
		int salonId = 0;
		String salonName = "";
		
		line = IfClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName);
		assertEquals(command,IfClientServerProtocol.decodeProtocole_Command(line));
	}
	
}
