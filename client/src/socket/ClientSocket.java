package socket;

import socket.json.ClientType;

public interface ClientSocket
{
    void connect(String host, int port);
    void disconnect();

    void register(String senderId, Integer spotId, ClientType clientType);
    void heartbeat(String senderId, Integer spotId, ClientType clientType);
    void carParked(String senderId, Integer spotId);
    void carLeft(String senderId, Integer spotId);
}
