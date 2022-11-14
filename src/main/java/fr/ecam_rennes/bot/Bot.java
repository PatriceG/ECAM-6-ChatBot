package fr.ecam_rennes.bot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;


import fr.ecam_rennes.geocodeur.GeoCodeur;

/**
 * Classe principale du Bot
 * 
 * @author Pat
 */
public class Bot implements PacketListener {
	
	/**
	 * Format de date à utiliser dans l'implémentation de la commande "quelle heure est-t-il"? 
	 */
	protected final String DATE_FORMAT="yyyy-MM-dd HH:mm:ssZ";
	

	/**
	 * préférences de l'application, lues depuis le fichier bot.properties
	 */
	protected Preferences prefs = Preferences.getInstance();

	
	/**
	 * Connexion XMPP au serveur de Chat
	 */
	protected XMPPConnection conn;
	

	
	/**
	 * Constructeur
	 */
	public Bot() {
		
	}

	public void init() throws IOException {		

		// configure la connexion XMPP
		// ConnectionConfiguration connectionConfig = new
		// ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
		ConnectionConfiguration connectionConfig = new ConnectionConfiguration(prefs.getProperty("server.name"),
				Integer.parseInt(prefs.getProperty("server.port")));
		conn = new XMPPConnection(connectionConfig);
		try {
			System.out.println("Démarrage...");
			// connexion
			conn.connect();
			conn.login(prefs.getProperty("bot.nickname"), prefs.getProperty("bot.password"));
			conn.getRoster().setSubscriptionMode(Roster.SubscriptionMode.accept_all);
			Presence presence = new Presence(Presence.Type.available);
			// set presence options here...
			conn.sendPacket(presence);
			// on s'enregistre pour recevoir les packets de type "chat"
			// seulement
			conn.addPacketListener(this, new MessageTypeFilter(Message.Type.chat));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Callback appelé sur réception d'un Packet XMPP
	 */
	public void processPacket(Packet packet) {

		Message msg = (Message) packet;
		System.out.println("Packet reçu de: " + msg.getFrom());
		System.out.println("contenu: " + msg.getBody());

		//exemple: message sans argument, on teste tout le contenu du message et on appelle la méthode de traitement de l'action
		if ("aide".equals(msg.getBody())) {
			//msg.getFrom() contient l'identifiant de l'utilisateur qui a envoyé le message
			//indispensable pour pouvoir lui répondre.
			sendHelpMessage(msg.getFrom());
		} else {
			
			//exemple: traitement du message trad langue texte à traduite
			//on a des arguments donc on doit découper le message reçu pour isoler les différents arguments
			//avant d'appeler la méthode de traduction
			if (msg.getBody().startsWith("trad")) {
				String[] el = msg.getBody().split(" ");
				String aTraduire = msg.getBody().substring(msg.getBody().indexOf(el[1]) + el[1].length() + 1);
				String langue = el[1];
				//TODO remove
			} else {
				// TODO compléter la lecture du body du message pour traiter les différentes commandes
				// aiguiller vers les différentes méthodes (à définir & implémenter) qui implémentent le comportement du Bot
				// ne pas tout coder dans des blocs if/else dans cette méthode!
				
				
				//si la commande saisie est inconnue, renvoyer "commande inconnue" à l'utilisateur
			}
		}
	}
	
	


	/**
	 * Envoie un message d'aide
	 * 
	 * @param user
	 */
	protected void sendHelpMessage(String user) {
		String msg = "Les commandes comprises sont:\n" + "aide : ce message d'aide\n"
				+ "trad <langue> <texte> : Traduire <message> en <langue>\n";
				
		sendMessage(msg, user);
	}

	/**
	 * Méthode utilitaire pour envoyer un message à un utilisateur
	 * 
	 * @param txt
	 *            - corps du message
	 * @param user
	 *            - ID de l'utilisateur
	 */
	protected void sendMessage(String txt, String user) {
		Message msg = new Message(user);
		msg.setType(Message.Type.chat);
		msg.setBody(txt);
		conn.sendPacket(msg);
	}



	/**
	 * Point d'entrée
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		XMPPConnection.DEBUG_ENABLED = true;
		Bot bot = new Bot();

		try {
			bot.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
