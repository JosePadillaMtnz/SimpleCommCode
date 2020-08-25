import java.io.*;
import java.net.*;
import java.util.*;

public class Client extends Thread {
	private DatagramSocket socket, socket2;
	private HashMap<Integer, InetAddress> directions = new HashMap<Integer, InetAddress>();
	private LinkedList<Integer> listServers = new LinkedList<Integer>();
	private byte[] buf = new byte[256];
	private Unicast control;
	private String serv0 = "ServerT", serv1 = "ServerH";
	
	public Client(int port) {
		try {
			socket = new DatagramSocket(port);
			socket2 = new DatagramSocket();
			control = new Unicast();
		} catch(IOException e) {System.out.println(e);}
	}
	
	public void run() {
		new Thread(control).start();
		while(true) {
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(packet);
				InetAddress address = packet.getAddress();
				int port = packet.getPort();
				directions.put(port, address);
				if (!listServers.contains(port)) listServers.add(port);
				String received = new String(packet.getData(), 0, packet.getLength());
				System.out.println(received);
			} catch(IOException e) {System.out.println(e);}
		}
	}

	class Unicast implements Runnable {
		public void run() {
			Scanner sc = new Scanner(System.in);
			boolean t = true;
			while (t) {
				try {Thread.sleep(1000);} catch (InterruptedException e1) {e1.printStackTrace();}
				
				System.out.println("To send control messages enter 0.");
				int value = sc.nextInt();
				while (value != 0) {value = sc.nextInt();}
				System.out.println("----> First choose the server to send the message to.\n--> Enter 1 for the "+serv0+".\n--> Enter 2 for the "+serv1+".");
				int server = sc.nextInt();
				System.out.println("----> Choose the operation to use.\n--> Enter 1 to indicate the server shutdown.\n--> Enter 2 to get real-time logs.\n"
						+ "--> Enter 3 to change the server send frequency.\n--> Enter 4 to change the range of a server component.");
				int operation = sc.nextInt();
				
				if ((server == 1 || server == 2) && (operation > 0 || operation < 5)) { 
					int port = listServers.get(server-1);
					InetAddress direction = directions.get(port);
					String msg = "";
					
					switch(operation) {
						case 1: msg = "End"; break;
						case 2: msg = "Resent"; break;
						case 3:
							System.out.println("----> Enter the seconds of frequency you want.");
							int frecuencia = sc.nextInt();
							msg = "Frequency-"+frecuencia;
							break;
						case 4:
							if (server == 1) {
								System.out.println("----> Change value in "+serv0);
								int register, value2;
								msg = "Register-";
								if (serv0.equals("ServerT")) {
									System.out.println("--> Enter 1 to change maximum temperature.\n--> Enter 2 to change maximum particle (pm10).\n"
											+ "--> Enter 3 to change maximum sulfur dioxide (so2).");
									register = sc.nextInt();
									System.out.println("----> Enter the new maximum value.");
									value2 = sc.nextInt();
									switch(register){
										case 1: msg += "temp-"+value2; break;
										case 2: msg += "pm10-"+value2; break;
										case 3: msg += "so2-"+value2; break;
										default: System.out.println("Value out of range"); continue;
									}
								} else {
									System.out.println("--> Enter 1 to change maximum humidity.\n--> Enter 2 to change maximum nitrogen dioxide (no2).\n"
											+ "--> Enter 3 to change maximum ozone (o3).");
									register = sc.nextInt();
									System.out.println("----> Enter the new maximum value.");
									value2 = sc.nextInt();
									switch(register){
										case 1: msg += "hum-"+value2; break;
										case 2: msg += "no2-"+value2; break;
										case 3: msg += "o3-"+value2; break;
										default: System.out.println("Value out of range."); continue;
									}
								}
							} else {
								System.out.println("----> Change value in "+serv1);
								int register, value2;
								msg = "Register-";
								if (serv1.equals("ServerT")) {
									System.out.println("--> Enter 1 to change maximum temperature.\n--> Enter 2 to change maximum particle (pm10).\n"
									+ "--> Enter 3 to change maximum sulfur dioxide (so2).");
									register = sc.nextInt();
									System.out.println("----> Enter the new maximum value.");
									value2 = sc.nextInt();
									switch(register){
										case 1: msg += "temp-"+value2; break;
										case 2: msg += "pm10-"+value2; break;
										case 3: msg += "so2-"+value2; break;
										default: System.out.println("Value out of range"); continue;
									}
								} else {
									System.out.println("--> Enter 1 to change maximum humidity.\n--> Enter 2 to change maximum nitrogen dioxide (no2).\n"
									+ "--> Enter 3 to change maximum ozone (o3).");
									register = sc.nextInt();
									System.out.println("----> Enter the new maximum value.");
									value2 = sc.nextInt();
									switch(register){
										case 1: msg += "hum-"+value2; break;
										case 2: msg += "no2-"+value2; break;
										case 3: msg += "o3-"+value2; break;
										default: System.out.println("Value out of range."); continue;
									}
								}
							}
						}
					buf = msg.getBytes();
					DatagramPacket packet = new DatagramPacket(buf, buf.length, direction, port);
					
					try {
						socket2.send(packet);
						buf = new byte[256];
						packet = new DatagramPacket(buf, buf.length);
						socket2.receive(packet);
					} catch(IOException e) {System.out.println(e);}
					
					String received = new String(packet.getData(), 0, packet.getLength());
					System.out.println(received);
					
				} else System.out.println("Incorrect data entered.\nEnter 0 and try again.");
			}
			sc.close();
		}
	}
}