package partie3;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Cette classe permet la reception d'un paquet UDP sur le port de reception
 * UDP/DNS. Elle analyse le paquet et extrait le hostname
 * 
 * Il s'agit d'un Thread qui ecoute en permanance pour ne pas affecter le
 * deroulement du programme
 * 
 * @author Max
 *
 */

public class UDPReceiver extends Thread {
	/**
	 * Les champs d'un Packet UDP 
	 * --------------------------
	 * En-tete (12 octects) 
	 * Question : l'adresse demande 
	 * Reponse : l'adresse IP
	 * Autorite :
	 * info sur le serveur d'autorite 
	 * Additionnel : information supplementaire
	 */

	/**
	 * Definition de l'En-tete d'un Packet UDP
	 * --------------------------------------- 
	 * Identifiant Parametres 
	 * QDcount
	 * Ancount
	 * NScount 
	 * ARcount
	 * 
	 * L'identifiant est un entier permettant d'identifier la requete. 
	 * parametres contient les champs suivant : 
	 * 		QR (1 bit) : indique si le message est une question (0) ou une reponse (1). 
	 * 		OPCODE (4 bits) : type de la requete (0000 pour une requete simple). 
	 * 		AA (1 bit) : le serveur qui a fourni la reponse a-t-il autorite sur le domaine? 
	 * 		TC (1 bit) : indique si le message est tronque.
	 *		RD (1 bit) : demande d'une requete recursive. 
	 * 		RA (1 bit) : indique que le serveur peut faire une demande recursive. 
	 *		UNUSED, AD, CD (1 bit chacun) : non utilises. 
	 * 		RCODE (4 bits) : code de retour.
	 *                       0 : OK, 1 : erreur sur le format de la requete,
	 *                       2: probleme du serveur, 3 : nom de domaine non trouve (valide seulement si AA), 
	 *                       4 : requete non supportee, 5 : le serveur refuse de repondre (raisons de s�ecurite ou autres).
	 * QDCount : nombre de questions. 
	 * ANCount, NSCount, ARCount : nombre d�entrees dans les champs �Reponse�, Autorite,  Additionnel.
	 */

	protected final static int BUF_SIZE = 1024;
	protected String SERVER_DNS = null;//serveur de redirection (ip)
	protected int portRedirect = 53; // port  de redirection (par defaut)
	protected int port; // port de r�ception
	private String adrIP = null; //bind ip d'ecoute
	private String DomainName = "none";
	private String DNSFile = null;
	private boolean RedirectionSeulement = false;
	
	private class ClientInfo { //quick container
		public String client_ip = null;
		public int client_port = 0;
	};
	private HashMap<Integer, ClientInfo> Clients = new HashMap<>();
	
	private boolean stop = false;

	
	public UDPReceiver() {
	}

	public UDPReceiver(String SERVER_DNS, int Port) {
		this.SERVER_DNS = SERVER_DNS;
		this.port = Port;
	}
	
	
	public void setport(int p) {
		this.port = p;
	}

	public void setRedirectionSeulement(boolean b) {
		this.RedirectionSeulement = b;
	}

	public String gethostNameFromPacket() {
		return DomainName;
	}

	public String getAdrIP() {
		return adrIP;
	}

	private void setAdrIP(String ip) {
		adrIP = ip;
	}

	public String getSERVER_DNS() {
		return SERVER_DNS;
	}

	public void setSERVER_DNS(String server_dns) {
		this.SERVER_DNS = server_dns;
	}

	public void setDNSFile(String filename) {
		DNSFile = filename;
	}

	public void run() {
		try {
			DatagramSocket serveur = new DatagramSocket(this.port); // *Creation d'un socket UDP
			List<String> lAdrRequete = new ArrayList<String>();
		
			// *Boucle infinie de recpetion
			while (!this.stop) {
				byte[] buff = new byte[0xFF];
				DatagramPacket paquetRecu = new DatagramPacket(buff,buff.length);
				System.out.println("Serveur DNS  "+serveur.getLocalAddress()+"  en attente sur le port: "+ serveur.getLocalPort());

				// *Reception d'un paquet UDP via le socket
				serveur.receive(paquetRecu);
				
				System.out.println("paquet recu du  "+paquetRecu.getAddress()+"  du port: "+ paquetRecu.getPort());
				
				// *Creation d'un DataInputStream ou ByteArrayInputStream pour
				// manipuler les bytes du paquet

				ByteArrayInputStream TabInputStream = new ByteArrayInputStream (paquetRecu.getData());
				DataInputStream dis = new DataInputStream(TabInputStream);
				
				System.out.println(buff.toString());

				// Ignorer ID + etc
				dis.skipBytes(7);
				
				// Lire ANCOUNT
				int requete = dis.readByte(); // 8
				
				// ****** Dans le cas d'un paquet requete *****
				if(requete == 0)
				{
					// *Lecture du Query Domain name, a partir du 13 byte
					dis.skipBytes(4); // 12
					int n = dis.readByte(); // 13
					
					StringBuilder qNameBuild = new StringBuilder();
					while( n != 0 )
					{
						// Mettre un point chaque boucle
						if(qNameBuild.length() > 0)
							qNameBuild.append('.');
						
						// Composer
						byte [] b = new byte[n];
						dis.read(b);
						qNameBuild.append(new String(b));
						
						// Prochain byte
						n = dis.readByte();
					} // while
					
					// *Sauvegarde du Query Domain name
					DomainName = qNameBuild.toString();
					
					// *Sauvegarde de l'adresse, du port et de l'identifiant de la requete
					setAdrIP(paquetRecu.getAddress().getHostAddress());
					
					// *Si le mode est redirection seulement
					if(RedirectionSeulement)
					{
						// *Rediriger le paquet vers le serveur DNS
						UDPSender send = new UDPSender(SERVER_DNS, port, serveur);
						send.SendPacketNow(paquetRecu);
					}
					// *Sinon
					else
					{
						// *Rechercher l'adresse IP associe au Query Domain name
						// dans le fichier de correspondance de ce serveur					
						QueryFinder qFinder = new QueryFinder(DNSFile, DomainName);
						List<String> lAdr = qFinder.StartResearch(DomainName);
						
						// *Si la correspondance n'est pas trouvee
						if(lAdr.isEmpty())
						{
							// *Rediriger le paquet vers le serveur DNS
							UDPSender send = new UDPSender(SERVER_DNS, port, serveur);
							send.SendPacketNow(paquetRecu);
						}
						// *Sinon	
						else
						{
							// *Creer le paquet de reponse a l'aide du UDPAnswerPaquetCreator
							byte [] reponse =
									UDPAnswerPacketCreator.getInstance().CreateAnswerPacket(paquetRecu.getData(), lAdr);
							
							// *Placer ce paquet dans le socket
							paquetRecu.setData(reponse);
							
							// *Envoyer le paquet
							serveur.send(paquetRecu);
						}
					}
				}
				// ****** Dans le cas d'un paquet reponse *****
				else
				{
					// *Lecture du Query Domain name, a partir du 13 byte
					dis.skipBytes(4); // 12
					int qdn = dis.readByte(); // 13

					StringBuilder qNameBuild = new StringBuilder();
					while( qdn != 0 )
					{
						// Mettre un point chaque boucle
						if(qNameBuild.length() > 0)
							qNameBuild.append('.');

						// Composer
						byte [] b = new byte[qdn];
						dis.read(b);
						qNameBuild.append(new String(b));
						
						// Prochain byte
						qdn = dis.readByte();
					} // while
					
					// *Sauvegarde du Query Domain name
					DomainName = qNameBuild.toString();
					
					// *Passe par dessus Type et Class
					// *Passe par dessus les premiers champs du ressource record
					// pour arriver au ressource data qui contient l'adresse IP associe
					//  au hostname (dans le fond saut de 16 bytes)
					dis.skip(16);
					
					// *Capture de ou des adresse(s) IP (ANCOUNT est le nombre
					// de reponses retournees)
					
					for(int i = 0; i < requete; i++)
					{
						// Lire RDATA entre 0 a 255
						String ip = (dis.readByte() & 0xff) + "." +
									(dis.readByte() & 0xff) + "." +
									(dis.readByte() & 0xff) + "." +
									(dis.readByte() & 0xff);

						setAdrIP(ip);
						lAdrRequete.add(adrIP);

						// *Ajouter la ou les correspondance(s) dans le fichier DNS
						// si elles ne y sont pas deja
						if(!RedirectionSeulement)
						{
							AnswerRecorder ar = new AnswerRecorder(DNSFile);
							ar.StartRecord(DomainName, adrIP);
						}
					}
					
					// *Faire parvenir le paquet reponse au demandeur original,
					// ayant emis une requete avec cet identifiant	
					byte[] reponse = UDPAnswerPacketCreator.getInstance().CreateAnswerPacket(paquetRecu.getData(), lAdrRequete);
					
					// *Placer ce paquet dans le socket
					paquetRecu.setData(reponse);
					
					// *Envoyer le paquet
					serveur.send(paquetRecu);
				}
			}
//			serveur.close(); //closing server
		} catch (Exception e) {
			System.err.println("Probl�me � l'ex�cution :");
			e.printStackTrace(System.err);
		}
	}
}
