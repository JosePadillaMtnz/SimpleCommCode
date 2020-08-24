import java.net.*;
import java.util.*;
import java.io.*;

public class Server {
	
	// The Server class creates two sockets, one for the server and one for the client to connect. 
	// In this case, it will be necessary to connect to port 9999, but it could be changed to any other.

	public static void main(String[] args) {
		ServerSocket s = null;
		Socket client = null;
	
		try { s = new ServerSocket(9999);
		} catch(IOException e) {System.out.println(e);}
		
		while (true) {
			try {
				client = s.accept();
				new RequestManager(client).start();
			} catch (IOException e) {System.out.println(e);}
		}
	}
}

class RequestManager extends Thread {

	// The RequestManager class manages connections and cookies. 
	// It processes the received cookie and adds the information of the new access, updating the cookie and sending it.

	Socket s;
	
	public RequestManager(Socket a) {
		this.s = a;
	}
	
	public String getCookies(String line) {
		String[] parts = line.split(": ");
		return parts[1];
	}
	
	public void run() {
		BufferedReader sin;
		PrintStream sout;
		String line = null, url = null, cookie = "";
		String info = "<html><head><title>All access</title></head><body bgcolor=\"aquamarine\"><h1>All accesses made from your browser to web pages</h1><h4>Times accessed | Page accessed</h4><br>";
		
		try {
			sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
			sout = new PrintStream(s.getOutputStream());
			line = sin.readLine();
			if (line != null) {
				
				while (line.length() > 0 ) { 
					String[] parts = line.split(" ");
					if (parts[0].equals("GET") || parts[0].equals("POST")) url = parts[1];
					if (parts[0].equals("Cookie:")) cookie = getCookies(line);
					line = sin.readLine();	
				}
				
				String rcookie; int numcookie = 0;
				String rhead = "HTTP/1.1 200 OK\r\n";
				String rtype = "Content-Type: text/html\r\n";
				String rlength = null, reply = null;
				
				if (cookie.length()>1) {
					HashMap<String, Integer> visits = new HashMap<String, Integer>();
					String[] fcookie = cookie.split("; ");
	
					for (String value : fcookie){
						numcookie++;
						String[] page = value.split("=");
						if (!visits.containsKey(page[1])) {
							visits.put(page[1], 1);
						} else {
							visits.replace(page[1], visits.get(page[1])+1);
						}
					}
					
					if (!visits.containsKey(url)) {
						visits.put(url, 1);
					} else {
						visits.replace(url, visits.get(url)+1);
					}
					
					Set<String> vi = visits.keySet();
					for (String v:vi) {
						int num = visits.get(v);
						info += "<p>"+num+" | "+v+"</p>";
					}
					info += "</body></html>";
					
					rcookie = "Set-Cookie: "+numcookie+"="+url+"\r\n";
					rlength = "Content-Length: "+info.length()+"\r\n";
					reply = rhead+rcookie+rtype+rlength+"\r\n"+info;
				} 
				else {
					rcookie = "Set-Cookie: 0="+url+"\r\n";
					info += "<p>1 | "+url+"</p>";
					info += "</body></html>";
					rlength = "Content-Length: "+info.length()+"\r\n";
					reply = rhead+rcookie+rtype+rlength+"\r\n"+info;
				}
				
				
				sout.println(reply);
			}
			sin.close();
			sout.close();
			s.close();
		} catch (IOException e) {System.out.println(e);}
	}
}