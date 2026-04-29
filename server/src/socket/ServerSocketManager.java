package socket;

import registry.ClientRegistry;
import service.ParkingLotService;
import socket.json.JsonMessage;
import socket.json.JsonUtil;
import socket.json.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerSocketManager
{
    private final ClientRegistry clientRegistry;
    private final ParkingLotService parkingLotService;
    private static final String SENDER_ID = "server";
    private ServerSocket welcomeSocket;
    private boolean RUNNING = true;

    public ServerSocketManager(int port, ClientRegistry clientRegistry, ParkingLotService parkingLotService)
    {
        this.clientRegistry = clientRegistry;
        this.parkingLotService = parkingLotService;

        System.out.println("Starting Server...");

        try {

            welcomeSocket = new ServerSocket(port);
            broadcastThread();

            while(RUNNING) {
                System.out.println("Waiting for a client to establish connection.");

                Socket socket = welcomeSocket.accept();

                ServerClientHandler handler = new ServerClientHandler(socket, parkingLotService);
                clientRegistry.addClient(handler);

                Thread thread = new Thread(handler);
                thread.setDaemon(true);
                thread.start();
            }

        } catch (IOException e) {
            System.out.println("Error: Server socket IO failure");
        }
    }

    public void broadcast(JsonMessage message) {
        parkingLotService.broadcast(message, null);
    }

    // TODO fix this
    private void broadcastThread()
    {
        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (true) {
                String text = sc.nextLine();

                if (text.equalsIgnoreCase("q")) {
                  JsonMessage message = JsonMessage.createBroadcastMessage(SENDER_ID, "shutting down server");
                  broadcast(message);
                    System.out.println("Terminating server");
                    terminateServer();
                    sc.close();
                    break;
                }
                else {
                    JsonMessage message = JsonMessage.createBroadcastMessage(SENDER_ID, text);
                    broadcast(message);
                }
            }
        }).start();
    }

    private void terminateServer()
    {
        RUNNING = false;
        try
        {
            if (welcomeSocket != null && !welcomeSocket.isClosed()){
                welcomeSocket.close();
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
