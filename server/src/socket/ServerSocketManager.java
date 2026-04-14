package socket;

import socket.json.JsonMessage;
import socket.json.JsonUtil;
import socket.json.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerSocketManager
{
    public final ServerClientHandlerPool HANDLER_POOL;
    private static final String SENDER_ID = "server";
    private ServerSocket welcomeSocket;
    private boolean RUNNING = true;

    public ServerSocketManager(int port)
    {

        HANDLER_POOL = new ServerClientHandlerPool();

        System.out.println("Starting Server...");

        try {

            welcomeSocket = new ServerSocket(port);
            broadcastThread();

            while(RUNNING) {
                System.out.println("Waiting for a client to establish connection.");

                Socket socket = welcomeSocket.accept();

                ServerClientHandler handler = new ServerClientHandler(socket, HANDLER_POOL, SENDER_ID);
                Thread thread = new Thread(handler);
                thread.setDaemon(true);
                thread.start();
                HANDLER_POOL.add(handler);
            }

        } catch (IOException e) {
            System.out.println("Error: Server socket IO failure");
        }
    }

    public void broadcast(String message) {
        HANDLER_POOL.broadcast(message, null);
    }

    // TODO fix this
    private void broadcastThread()
    {
        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (true) {
                String text = sc.nextLine();

                if (text.equalsIgnoreCase("q")) {
                  JsonMessage message = JsonMessage.createBroadcastMessage(SENDER_ID, "SERVER: " + "shutting down server");
                  broadcast(JsonUtil.toJson(message));
                    System.out.println("Terminating server");
                    terminateServer();
                    sc.close();
                    break;
                }
                else {
                    broadcast(MessageType.BROADCAST + ";Server: " + text);
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
