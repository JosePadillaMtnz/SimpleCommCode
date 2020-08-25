import java.io.IOException;
import java.net.*;
import java.util.*;

public class ServerT extends Thread{
	
	private DatagramSocket socket;
	private static HashMap<String, Integer> ranks = new HashMap<String, Integer>();
	private static HashMap<String, String> units = new HashMap<String, String>();
	private boolean running = true;
	private byte[] buf = new byte[256];
	private ControlMsg ThreadUni;
	private int frequency = 20000;
	
	public ServerT(){ 
		try {socket = new DatagramSocket(); socket.setBroadcast(true);} catch(IOException e) {System.out.println(e);}
		ThreadUni = new ControlMsg();
		ranks.put("temp", 50); ranks.put("pm10", 50); ranks.put("so2", 6);
		units.put("temp", "�C"); units.put("pm10", "ug/m3"); units.put("so2", "ug/m3");
	}
	
	public static String getValues() {
		String reply = ">>> ServerT Data <<<\n";
		for (String value : ranks.keySet()) {
			double value2 = Math.random()*ranks.get(value);
			reply += "> "+value+"="+value2+" "+units.get(value)+";\n";
		}
		return reply;
	}
	
	public static InetAddress getBroadcastAddress() {
		List<InetAddress> broadcastList = new ArrayList<>();
	    Enumeration<NetworkInterface> interfaces;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
		    while (interfaces.hasMoreElements()) {
		        NetworkInterface networkInterface = interfaces.nextElement();
		        if (networkInterface.isLoopback() || !networkInterface.isUp()) {continue;}
		        networkInterface.getInterfaceAddresses().stream() 
		          .map(a -> a.getBroadcast())
		          .filter(Objects::nonNull)
		          .forEach(broadcastList::add);
		    }
		} catch (SocketException e) {e.printStackTrace(); }
	    return broadcastList.get(0);
	}
	
	public void run() {
		new Thread(ThreadUni).start();
		running = true;
		while (running) {
			try {
				String reply = getValues();
				buf = reply.getBytes();
				DatagramPacket packet = new DatagramPacket(buf, buf.length, getBroadcastAddress(), 5000); 
				socket.send(packet);					
				Thread.sleep(frequency); 
			} catch (Exception e) {e.printStackTrace();}
		}
	}
	
	class ControlMsg implements Runnable {
		public void run() {
			running = true;
			while (running) {
				try {
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					socket.receive(packet);
				
					InetAddress address = packet.getAddress();
					int port = packet.getPort();
					String received = new String(packet.getData(), 0, packet.getLength());
					
					if (received.equals("Model")) {
						buf = new String("ServerT").getBytes();
					}
					else if (received.equals("End")) {
						running = false;
						buf = new String("ServerT shutdown succesfully.").getBytes();
					}
					else if (received.equals("Resent")) {
						String valores = getValues();
						buf = valores.getBytes();
					}
					else if (received.contains("Frequency")) {
						String[] parts = received.split("-");
						if (frequency != Integer.parseInt(parts[1])*1000) {
							frequency=Integer.parseInt(parts[1])*1000;
							buf = new String("Frequency in ServerT changed succesfully.").getBytes();
						} else buf = new String("Already has that frequency.").getBytes();
					}
					else if (received.contains("Register")) {
						String[] parts = received.split("-");
						if (ranks.containsKey(parts[1])) {
							ranks.replace(parts[1], Integer.parseInt(parts[2]));
							buf = new String("Register range changed succesfully.").getBytes();
						} else buf = new String("Could not change register range.").getBytes();
					}
					
					packet = new DatagramPacket(buf, buf.length, address, port);
					socket.send(packet);
				} catch(IOException e) {System.out.println(e);}
			}
			socket.close();
		}
	}
}