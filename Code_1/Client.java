import java.net.*;
import java.util.*;
import java.io.*;

public class Client {

	// The client class allows us to make a query to the server pretending to be an HTTP client. 
	// It asks us which url we want to use and forms the request, finally sending it.

	public static void main(String args[] ) {
		
		System.out.println("Enter the url to consult.");
		Scanner ent = new Scanner(System.in);

		LinkedList<String> listcookies = getFromFile();
		System.out.println("Table of previous urls: " + listcookies);
		String cookies = "";
		String cookiev = null;
		String reply = "GET /"+ent.nextLine()+" HTTP1.1\r\n";
		ent.close();
		
		Socket myClient;
		DataInputStream entry;
		DataOutputStream exit;
		int i = 0;
		if (listcookies != null) for (String s : listcookies) {cookies+=i+"="+s+"; "; i++;}
		
		try {
			
			myClient = new Socket("localhost", 9999);
			entry = new DataInputStream(myClient.getInputStream());
			exit = new DataOutputStream(myClient.getOutputStream());
			
			if (!cookies.equals("")) reply += "Cookie: "+cookies+"\r\n";
			reply += "\r\n";
			System.out.println("Current query: "+reply);
			exit.writeBytes(reply);
			
			String inf = entry.readLine();
			while (inf != null) {
				System.out.println(inf);
				String[] parts = inf.split(" ");
				if (parts[0].equals("Set-Cookie:")) cookiev = parts[1];
				inf = entry.readLine();
			}
			if (cookiev != null) putInFile(cookiev);
			entry.close();
			exit.close();
		} catch (IOException e) {System.out.println(e);}
	}

	
	public static LinkedList<String> getFromFile() {
		LinkedList<String> line = new LinkedList<String>();
		
		File file = new File("cookie.txt"); 
		if (file.exists())
			try {
				FileReader fil = new FileReader("cookie.txt");
				BufferedReader s = new BufferedReader(fil);
				String linel = s.readLine();
				while (linel != null) {
					line.add(linel);
					linel = s.readLine();
				}
				if (s != null) s.close();
			} catch (Exception e) {System.out.println(e);}
		else {
			try{
				file.createNewFile();
			} catch (Exception e) {System.out.println(e);}
			line = null;
		}
		
		return line;
	}
	
	public static void putInFile(String value) {
		String[] parts = value.split("=");
		try {
			FileWriter file = new FileWriter("cookie.txt", true);
			BufferedWriter out = new BufferedWriter(file);
			out.write(parts[1]+"\n");
			out.close();
		} catch (IOException e) {System.out.println(e);}
	}
}
