package socket;

import socket.json.ClientType;
import socket.json.JsonMessage;
import socket.json.JsonUtil;
import socket.json.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSocketManager implements ClientSocket
{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientSocketManager(String host, int port)
    {
        connect(host, port);
    }

    @Override
    public void connect(String host, int port)
    {
        if (socket != null && !socket.isClosed())
            disconnect();

        try
        {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Client established connection with server");

            Thread receiverThread = createReceiverThread();
            receiverThread.setDaemon(true);
            receiverThread.start();

        } catch (IOException e)
        {
            System.out.println("Error: Client failed to establish connection to server");
        }

    }

    @Override
    public void disconnect()
    {
        try
        {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();

            System.out.println("Client closed connection with server");
        }
        catch (IOException e)
        {
            System.out.println("Error: Client failed to close the connection to server");
        }
    }

    @Override
    public void register(String senderId, Integer spotId, ClientType clientType)
    {
        JsonMessage message = JsonMessage.createRegisterMessage(senderId, spotId, clientType);
        send(message);
    }

    @Override
    public void heartbeat(String senderId, Integer spotId, ClientType clientType)
    {
        JsonMessage message = JsonMessage.createHeartbeatMessage(senderId, spotId, clientType);
        send(message);
    }

    @Override
    public void carParked(String senderId, Integer spotId)
    {
        JsonMessage message = JsonMessage.createCarParkedMessage(senderId, spotId);
        send(message);
    }

    @Override
    public void carLeft(String senderId, Integer spotId)
    {
        JsonMessage message = JsonMessage.createCarLeftMessage(senderId, spotId);
        send(message);
    }

    private Thread createReceiverThread()
    {
        return new Thread(() -> {
            try
            {
                while (true)
                {
                    JsonMessage message = readMessage();
                    if (message == null) break;

                    MessageType messageType = message.getHEADER().TYPE();

                    switch (messageType) {
                        case ACK -> handleAck(message);
                        case ERROR -> handleError(message);
                        case SET_LIGHT -> handleSetLight(message);
                        case UPDATE_TOTAL -> handleUpdateTotal(message);
                        case SYNC_STATE -> handleSyncState(message);
                    }
                }
            } catch (IOException e)
            {
                System.out.println("Error: Message receiver IO failure");
            }
        });
    }

    private void handleAck(JsonMessage message)
    {
        System.out.println("ACK received: " + message.getBODY().TEXT());
    }

    private void handleError(JsonMessage message)
    {
        System.out.println("ERROR received: " + message.getBODY().ERROR_DESCRIPTION());
    }

    private void handleSetLight(JsonMessage message)
    {
        System.out.println("SET_LIGHT received for spot "
                + message.getBODY().SPOT_ID()
                + ", color=" + message.getBODY().COLOR());
    }

    private void handleUpdateTotal(JsonMessage message)
    {
        int freeSpaces = message.getBODY().FREE_SPACES();
        System.out.print("DISPLAY_TEXT: ");
        if (freeSpaces == 0) System.out.println("Ingen ledige pladser");
        else if (freeSpaces == 1) System.out.println(freeSpaces + " ledig plads");
        else System.out.println(freeSpaces + " ledige pladser");
    }

    private void handleSyncState(JsonMessage message)
    {
        System.out.println("SYNC_STATE received: " + message.getBODY());
    }

    private JsonMessage readMessage() throws IOException
    {
        String line = in.readLine();
        if (line == null)
        {
            System.out.println("Server closed the connection.");
            disconnect();
            return null;
        }
        return JsonUtil.fromJson(line);
    }

    private void send(JsonMessage message)
    {
        if (out == null)
        {
            System.out.println("No server connection.");
            return;
        }
        out.println(JsonUtil.toJson(message));
    }
}
