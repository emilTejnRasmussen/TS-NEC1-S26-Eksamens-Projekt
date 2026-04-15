package socket;

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
    private final ServerClientHandlerPool HANDLER_POOL;
    private final String SENDER_ID;

    public ServerClientHandler(Socket socket, ServerClientHandlerPool handlerPool,
                               String senderId)
    {
        this.socket = socket;
        this.HANDLER_POOL = handlerPool;
        this.SENDER_ID = senderId;

        this.CLIENT_ADDRESS =
                socket.getInetAddress().getHostAddress() + ":" + socket.getPort();

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
        String json = JsonUtil.toJson(message);
        out.println(json);
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
            System.out.println("Client disconnected or error from " + CLIENT_ADDRESS
                    + ": " + e.getMessage());
        } finally
        {
            closeConnection();
        }
    }

    private void handleMessage(JsonMessage message)
    {
        MessageType type = message.getHEADER().TYPE();

        switch (type){
            case REGISTER -> handleRegister(message);
            case HEARTBEAT -> handleHeartbeat(message);
            case CAR_PARKED -> handleCarParked(message);
            case CAR_LEFT -> handleCarLeft(message);
            default -> {
                JsonMessage error = JsonMessage.createErrorMessage(
                        SENDER_ID,
                        "Unknown message type: " + type,
                        message.getHEADER().MESSAGE_ID()
                );
                send(error);
            }
        }
    }

    private void handleRegister(JsonMessage message)
    {
        System.out.println(message.getHEADER().SENDER_ID() + ": " + message.getBODY().TEXT());

        int responseMessageId = message.getHEADER().MESSAGE_ID();
        MessageType type = message.getHEADER().TYPE();

        JsonMessage response = JsonMessage.createAckMessage(SENDER_ID, responseMessageId, type);
        out.println(JsonUtil.toJson(response));
    }

    private void handleHeartbeat(JsonMessage message)
    {
        System.out.println(message.getHEADER().SENDER_ID() + ": " + message.getBODY().TEXT());
    }

    private void handleCarParked(JsonMessage message)
    {
        System.out.println(message.getHEADER().SENDER_ID() + ": " + message.getBODY().TEXT());
    }

    private void handleCarLeft(JsonMessage message)
    {
        System.out.println(message.getHEADER().SENDER_ID() + ": " + message.getBODY().TEXT());
    }

    private void closeConnection()
    {
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
}
