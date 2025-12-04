import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleServer {
    public static void main(String[] args) throws IOException {
        int port = 12345;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);
        List<ClientInfo> clients = Collections.synchronizedList(new ArrayList());
        int clientCount = 0;

        while(true) {
            Socket socket = serverSocket.accept();
            ++clientCount;
            String clientName = "Player" + clientCount;
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clients.add(new ClientInfo(clientName, out));
            System.out.println(clientName + " connected.");
            broadcast(clients, clientName + " joined the server.", (PrintWriter)null);
            (new Thread(() -> {
                try {
                    String message;
                    try {
                        while((message = in.readLine()) != null) {
                            System.out.println(clientName + ": " + message);
                            broadcast(clients, clientName + ": " + message, out);
                        }
                    } catch (IOException var14) {
                        System.out.println(clientName + " disconnected.");
                    }
                } finally {
                    clients.removeIf((c) -> c.writer == out);
                    broadcast(clients, clientName + " left the server.", (PrintWriter)null);

                    try {
                        socket.close();
                    } catch (IOException var13) {
                    }

                }

            })).start();
        }
    }

    private static void broadcast(List<ClientInfo> clients, String message, PrintWriter exclude) {
        synchronized(clients) {
            for(ClientInfo client : clients) {
                if (client.writer != exclude) {
                    client.writer.println(message);
                }
            }

        }
    }
}
