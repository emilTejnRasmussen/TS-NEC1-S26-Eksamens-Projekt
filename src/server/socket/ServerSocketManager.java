package server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerSocketManager
{
    public final ServerClientHandlerPool HANDLER_POOL;
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

                ServerClientHandler handler = new ServerClientHandler(socket, HANDLER_POOL);
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

    private void broadcastThread()
    {
        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (true) {
                String text = sc.nextLine();

                if (text.equalsIgnoreCase("q")) {
                    broadcast(MessageType.BROADCAST + ";SERVER: " + "shutting down server");
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
