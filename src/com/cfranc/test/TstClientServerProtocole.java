package com.cfranc.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.cfranc.irc.ClientServerProtocol;

public class TstClientServerProtocole {

	@Test
	public void test_encodeProtocole_Ligne_Login() {
		String line = "";
		String login = "Toto";
		String pwd = "";
		String msg = "";
		String command = "";
		int salonId = 0;
		String salonName = "";
		String recepteur = "";
		int newSalonId = 0;

		line = ClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName, recepteur,
				newSalonId);
		assertEquals(login, ClientServerProtocol.decodeProtocole_Login(line));
	}

	@Test
	public void test_encodeProtocole_Ligne_pwd() {
		String line = "";
		String login = "";
		String pwd = "pvd12345";
		String msg = "";
		String command = "";
		int salonId = 0;
		String salonName = "";
		String recepteur = "";
		int newSalonId = 0;

		line = ClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName, recepteur,
				newSalonId);
		assertEquals(pwd, ClientServerProtocol.decodeProtocole_PWD(line));
	}

	@Test
	public void test_encodeProtocole_Ligne_Msg() {
		String line = "";
		String login = "";
		String pwd = "";
		String msg = "Hello World !!!";
		String command = "";
		int salonId = 0;
		String salonName = "";
		String recepteur = "";
		int newSalonId = 0;

		line = ClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName, recepteur,
				newSalonId);
		assertEquals(msg, ClientServerProtocol.decodeProtocole_Message(line));
	}

	@Test
	public void test_encodeProtocole_Ligne_Command() {
		String line = "";
		String login = "";
		String pwd = "";
		String msg = "";
		String command = "-";
		int salonId = 0;
		String salonName = "";
		String recepteur = "";
		int newSalonId = 0;

		line = ClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName, recepteur,
				newSalonId);
		assertEquals(command, ClientServerProtocol.decodeProtocole_Command(line));
	}

	@Test
	public void test_encodeProtocole_Ligne_SalonId() {
		String line = "";
		String login = "";
		String pwd = "";
		String msg = "";
		String command = "";
		int salonId = 6;
		String salonName = "";
		String recepteur = "";

		int newSalonId = 0;

		line = ClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName, recepteur,
				newSalonId);
		assertEquals(salonId, ClientServerProtocol.decodeProtocole_IdSalon(line));
	}

	@Test
	public void test_encodeProtocole_Ligne_SalonName() {
		String line = "";
		String login = "";
		String pwd = "";
		String msg = "";
		String command = "";
		int salonId = 0;
		String salonName = "Salon Général";
		String recepteur = "";

		int newSalonId = 0;

		line = ClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName, recepteur,
				newSalonId);
		assertEquals(salonName, ClientServerProtocol.decodeProtocole_NomSalon(line));
	}

	@Test
	public void test_encodeProtocole_Ligne_Recepteur() {
		String line = "";
		String login = "";
		String pwd = "";
		String msg = "";
		String command = "";
		int salonId = 0;
		String salonName = "";
		String recepteur = "Titi";

		int newSalonId = 0;

		line = ClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName, recepteur,
				newSalonId);
		assertEquals(recepteur, ClientServerProtocol.decodeProtocole_UtilisateurRecepteur(line));
	}

	@Test
	public void test_encodeProtocole_Ligne_Login_Vide() {
		String line = "";
		String login = "";
		String pwd = "";
		String msg = "";
		String command = "";
		int salonId = 0;
		String salonName = "";
		String recepteur = "";

		int newSalonId = 0;

		line = ClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName, recepteur,
				newSalonId);
		assertEquals(login, ClientServerProtocol.decodeProtocole_Login(line));
	}

	@Test
	public void test_encodeProtocole_Ligne_Pwd_Vide() {
		String line = "";
		String login = "";
		String pwd = "";
		String msg = "";
		String command = "";
		int salonId = 0;
		String salonName = "";
		String recepteur = "";

		int newSalonId = 0;

		line = ClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName, recepteur,
				newSalonId);
		assertEquals(pwd, ClientServerProtocol.decodeProtocole_PWD(line));
	}

	@Test
	public void test_encodeProtocole_Ligne_Msg_Vide() {
		String line = "";
		String login = "";
		String pwd = "";
		String msg = "";
		String command = "";
		int salonId = 0;
		String salonName = "";
		String recepteur = "";

		int newSalonId = 0;

		line = ClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName, recepteur,
				newSalonId);
		assertEquals(msg, ClientServerProtocol.decodeProtocole_Message(line));
	}

	@Test
	public void test_encodeProtocole_Ligne_Command_Vide() {
		String line = "";
		String login = "";
		String pwd = "";
		String msg = "";
		String command = "";
		int salonId = 0;
		String salonName = "";
		String recepteur = "";

		int newSalonId = 0;

		line = ClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName, recepteur,
				newSalonId);
		assertEquals(command, ClientServerProtocol.decodeProtocole_Command(line));
	}

	@Test
	public void test_encodeProtocole_Ligne_Recepteur_Vide() {
		String line = "";
		String login = "";
		String pwd = "";
		String msg = "";
		String command = "";
		int salonId = 0;
		String salonName = "Salon Général";
		String recepteur = "";

		int newSalonId = 0;

		line = ClientServerProtocol.encodeProtocole_Ligne(login, pwd, msg, command, salonId, salonName, recepteur,
				newSalonId);
		assertEquals(recepteur, ClientServerProtocol.decodeProtocole_UtilisateurRecepteur(line));
	}
}
