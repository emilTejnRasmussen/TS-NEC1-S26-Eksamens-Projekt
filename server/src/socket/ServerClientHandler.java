package socket;

import registry.ClientRegistry;
import service.ParkingLotService;
import socket.json.ClientType;
import socket.json.JsonMessage;
import socket.json.JsonUtil;
import socket.json.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClientHandler implements Runnable
{
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final String CLIENT_ADDRESS;

    private final ParkingLotService parkingLotService;

    private ClientType registeredClientType;
    private Integer registeredSpotId;
    private String registeredClientId;
    private boolean isRegistered = false;

    public ServerClientHandler(Socket socket, ParkingLotService parkingLotService)
    {
        this.socket = socket;

        this.CLIENT_ADDRESS =
                socket.getInetAddress().getHostAddress() + ":" + socket.getPort();

        this.parkingLotService = parkingLotService;

        System.out.println("Connection establish with client " + CLIENT_ADDRESS);

        try
        {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e)
        {
            System.out.println(
                    "Error: ServerClientHandler failed to establish streams to client :"
                            + CLIENT_ADDRESS);
        }
    }

    public void send(JsonMessage message)
    {
        if (out == null) return;
        out.println(JsonUtil.toJson(message));
    }

    @Override
    public void run()
    {
        try
        {
            String line;

            while ((line = in.readLine()) != null)
            {
                System.out.println("Received from " + CLIENT_ADDRESS + ": " + line);
                JsonMessage message = JsonUtil.fromJson(line);

                handleMessage(message);
            }

        } catch (Exception e)
        {
            System.out.println(
                    "Client disconnected or error from " + CLIENT_ADDRESS + ": "
                            + e.getMessage());
        } finally
        {
            closeConnection();
        }
    }

    private void handleMessage(JsonMessage message)
    {
        MessageType type = message.getHEADER().TYPE();

        switch (type)
        {
            case REGISTER -> parkingLotService.handleRegister(this, message);
            case HEARTBEAT -> parkingLotService.handleHeartBeat(this, message);
            case CAR_PARKED -> parkingLotService.handleCarParked(this, message);
            case CAR_LEFT -> parkingLotService.handleCarLeft(this, message);
            default ->
            {
                JsonMessage error = JsonMessage.createErrorMessage(
                        "server",
                        message.getHEADER().MESSAGE_ID(),
                        "Unknown message type: " + type
                );
                send(error);
            }
        }
    }

    private void closeConnection()
    {
        parkingLotService.handleDisconnect(this);

        try
        {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e)
        {
            System.out.println("Error closing connection for " + CLIENT_ADDRESS);
        }
    }

    public Integer getRegisteredSpotId()
    {
        return registeredSpotId;
    }

    public void setRegisteredSpotId(Integer registeredSpotId)
    {
        this.registeredSpotId = registeredSpotId;
    }

    public ClientType getRegisteredClientType()
    {
        return registeredClientType;
    }

    public void setRegisteredClientType(ClientType registeredClientType)
    {
        this.registeredClientType = registeredClientType;
    }

    public String getRegisteredClientId()
    {
        return registeredClientId;
    }

    public void setRegisteredClientId(String registeredClientId)
    {
        this.registeredClientId = registeredClientId;
    }

    public boolean isRegistered()
    {
        return isRegistered;
    }

    public void setRegistered(boolean registered)
    {
        isRegistered = registered;
    }
}
