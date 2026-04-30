package socket;

import service.ParkingLotService;
import socket.json.JsonMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerSocketManager
{
    private final ServerClientHandlerPool HANDLER_POOL;
    private final ParkingLotService parkingLotService;
    private static final String SENDER_ID = "server";
    private ServerSocket welcomeSocket;
    private boolean RUNNING = true;

    private static final long HEARTBEAT_TIMEOUT_MS = 10000;
    private static final long HEARTBEAT_CHECK_INTERVAL_MS = 2000;


    public ServerSocketManager(int port, ServerClientHandlerPool HANDLER_POOL, ParkingLotService parkingLotService)
    {
        this.HANDLER_POOL = HANDLER_POOL;
        this.parkingLotService = parkingLotService;

        System.out.println("Starting Server...");

        try {

            welcomeSocket = new ServerSocket(port);
            broadcastThread();

            while(RUNNING) {
                System.out.println("Waiting for a client to establish connection.");

                Socket socket = welcomeSocket.accept();

                ServerClientHandler handler = new ServerClientHandler(socket, parkingLotService);
                HANDLER_POOL.addClient(handler);

                Thread thread = new Thread(handler);
                thread.setDaemon(true);
                thread.start();

                startHeartbeatMonitor();
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

    private void startHeartbeatMonitor()
    {
        Thread monitorThread = new Thread(() -> {
            while (RUNNING) {
                try {
                    Thread.sleep(HEARTBEAT_CHECK_INTERVAL_MS);

                    long now = System.currentTimeMillis();

                    for (ServerClientHandler handler : HANDLER_POOL.getAllClients()) {
                        if (!handler.isRegistered()) continue;
                        if (handler.isTimedOut()) continue;

                        long elapsed = now - handler.getLastSeen();

                        if (elapsed > HEARTBEAT_TIMEOUT_MS) {
                            System.out.println("Heartbeat timeout for " + handler.getRegisteredClientId());
                            parkingLotService.handleHeartbeatTimeout(handler);
                        }
                    }
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        monitorThread.setDaemon(true);
        monitorThread.start();
    }
}
