
import java.net.*;
import java.io.*;

public class server {
	
	static DB db = new DB();
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		////db.delete(2);
		while (1 == 1) {
			start(serverSocket);
		}

	}

	public static void start(ServerSocket serverSocket) throws IOException {
		try {
			serverSocket = new ServerSocket(4444);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 4444.");
			System.exit(1);
		}

		Socket clientSocket = null;
		try {
			clientSocket = serverSocket.accept();
		} catch (IOException e) {
			System.err.println("Accept failed.");
			System.exit(1);
		}

		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			out.println(Islem.isle(inputLine));
		}
		out.close();
		in.close();
		clientSocket.close();
		serverSocket.close();
	}
}
