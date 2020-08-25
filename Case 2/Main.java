public class Main {
	public static void main(String[] args) {
		Client client = new Client(5000);
		client.start();
		ServerT servt = new ServerT();
		ServerH servh = new ServerH();
		servt.start();
		servh.start();
	}
}