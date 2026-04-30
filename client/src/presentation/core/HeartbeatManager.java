package presentation.core;

import socket.ClientSocketManager;
import socket.json.ClientType;

public class HeartbeatManager
{
    private final ClientSocketManager clientSocketManager;
    private final String senderId;
    private final Integer spotId;
    private final ClientType clientType;
    private static final long INTERVAL_IN_MILLIS = 5000;

    private volatile boolean running;
    private volatile boolean enabled;
    private Thread thread;

    public HeartbeatManager(ClientSocketManager clientSocketManager, String senderId, Integer spotId, ClientType clientType)
    {
        this.clientSocketManager = clientSocketManager;
        this.senderId = senderId;
        this.spotId = spotId;
        this.clientType = clientType;
    }

    public void start() {
        if (running) return;

        running = true;
        enabled = true;

        thread = new Thread(() -> {
            while (running) {
                try
                {
                    Thread.sleep(INTERVAL_IN_MILLIS);

                    if (running && enabled) {
                        clientSocketManager.heartbeat(senderId, spotId, clientType);
                    }
                } catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    public void pause() {
        enabled = false;
    }

    public void resume() {
        enabled = true;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
