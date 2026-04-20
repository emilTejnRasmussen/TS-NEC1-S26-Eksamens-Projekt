package service;

import guard.Check;
import registry.ClientRegistry;
import socket.ServerClientHandler;
import socket.json.ClientType;
import socket.json.JsonMessage;
import socket.json.MessageType;
import state.ParkingLotState;
import state.SpotState;

import java.util.ArrayList;
import java.util.List;

public class ParkingLotService
{
    private static final String SENDER_ID = "server";

    private final ClientRegistry clientRegistry;
    private final ParkingLotState parkingLotState;

    public ParkingLotService(ClientRegistry clientRegistry, ParkingLotState parkingLotState)
    {
        this.clientRegistry = clientRegistry;
        this.parkingLotState = parkingLotState;
    }

    public synchronized void handleRegister(ServerClientHandler serverClientHandler, JsonMessage message)
    {

        String clientId = message.getHEADER().SENDER_ID();
        ClientType clientType = message.getBODY().CLIENT_TYPE();
        Integer spotId = message.getBODY().SPOT_ID();
        boolean isRegistered = serverClientHandler.isRegistered();

        try
        {
            validateRegistration(clientId, clientType, spotId, isRegistered);
        } catch (IllegalStateException e)
        {
            sendError(serverClientHandler, message, e.getMessage());
            return;
        }

        serverClientHandler.setRegisteredClientId(clientId);
        serverClientHandler.setRegisteredClientType(clientType);
        serverClientHandler.setRegisteredSpotId(spotId);

        switch (clientType)
        {
            case DISPLAY -> clientRegistry.registerDisplay(serverClientHandler);
            case LIGHT -> clientRegistry.registerLight(spotId, serverClientHandler);
            case SENSOR ->
            {
                clientRegistry.registerSensor(spotId, serverClientHandler);
                if (parkingLotState.getSpotState(spotId) == SpotState.UNKNOWN)
                {
                    parkingLotState.setSpotState(spotId, SpotState.FREE);
                }
            }
        }

        serverClientHandler.setRegistered(true);
        sendAck(serverClientHandler, message);


        switch (clientType)
        {
            case LIGHT, DISPLAY -> syncClientState(serverClientHandler, message);
            case SENSOR ->
            {
                ServerClientHandler lightHandler = clientRegistry.getLight(spotId);
                if (lightHandler != null)
                    updateLight(spotId, parkingLotState.getSpotState(spotId));
            }
        }

        updateDisplays();
    }

    public synchronized void handleHeartBeat(ServerClientHandler serverClientHandler, JsonMessage message)
    {
        sendAck(serverClientHandler, message);
    }

    public synchronized void handleCarParked(ServerClientHandler serverClientHandler, JsonMessage message)
    {
        if (isClientNotSensor(serverClientHandler, message)) return;

        int spotId = serverClientHandler.getRegisteredSpotId();
        parkingLotState.setSpotState(spotId, SpotState.OCCUPIED);

        sendAck(serverClientHandler, message);

        updateLight(spotId, parkingLotState.getSpotState(spotId));
        updateDisplays();
    }

    public synchronized void handleCarLeft(ServerClientHandler serverClientHandler, JsonMessage message)
    {
        if (isClientNotSensor(serverClientHandler, message)) return;

        int spotId = serverClientHandler.getRegisteredSpotId();
        parkingLotState.setSpotState(spotId, SpotState.FREE);

        sendAck(serverClientHandler, message);

        updateLight(spotId, parkingLotState.getSpotState(spotId));
        updateDisplays();
    }

    public synchronized void broadcast(JsonMessage message, ServerClientHandler serverClientHandler)
    {
        List<ServerClientHandler> serverClientHandlers = new ArrayList<>(clientRegistry.getAllClients());

        for (ServerClientHandler handler : serverClientHandlers)
        {
            if (!handler.equals(serverClientHandler))
            {
                handler.send(message);
            }
        }
    }

    public synchronized void handleDisconnect(ServerClientHandler serverClientHandler)
    {
        if (serverClientHandler == null) return;

        ClientType clientType = serverClientHandler.getRegisteredClientType();
        Integer spotId = serverClientHandler.getRegisteredSpotId();

        clientRegistry.removeClient(serverClientHandler);

        if (!serverClientHandler.isRegistered() || clientType == null || spotId == null) return;

        if (clientType == ClientType.SENSOR)
        {
            parkingLotState.setSpotState(spotId, SpotState.UNKNOWN);
            updateLight(spotId, SpotState.UNKNOWN);
            updateDisplays();
        }
    }

    private boolean isClientNotSensor(ServerClientHandler serverClientHandler, JsonMessage message)
    {
        ClientType clientType = serverClientHandler.getRegisteredClientType();
        if (clientType != ClientType.SENSOR)
        {
            sendError(serverClientHandler, message, "Only sensors can call this action.. " + clientType + " is not a sensor.");
            return true;
        }
        return false;
    }

    private void sendAck(ServerClientHandler handler, JsonMessage incoming)
    {
        int responseId = incoming.getHEADER().MESSAGE_ID();
        MessageType type = incoming.getHEADER().TYPE();
        JsonMessage ackMessage = JsonMessage.createAckMessage(SENDER_ID, responseId, type);

        handler.send(ackMessage);
    }

    private void sendError(ServerClientHandler handler, JsonMessage incoming, String error)
    {
        int responseId = incoming.getHEADER().MESSAGE_ID();
        JsonMessage errorMessage = JsonMessage.createErrorMessage(SENDER_ID, responseId, error);

        handler.send(errorMessage);
    }

    private void syncClientState(ServerClientHandler handler, JsonMessage message)
    {
        int responseMessageId = message.getHEADER().MESSAGE_ID();
        ClientType clientType = handler.getRegisteredClientType();
        Integer spotId = handler.getRegisteredSpotId();

        SpotState spotState = null;

        if (clientType == ClientType.LIGHT && spotId != null)
        {
            spotState = parkingLotState.getSpotState(spotId);
            Check.Against.nullValue(spotState, "Spot State");
        }

        JsonMessage syncMessage = createSyncMessage(responseMessageId, clientType, spotId, spotState);
        handler.send(syncMessage);
    }

    private void updateDisplays()
    {
        List<ServerClientHandler> allDisplays = new ArrayList<>(clientRegistry.getDisplays());

        JsonMessage updateDisplayMessage = JsonMessage.createUpdateDisplayMessage(
                SENDER_ID,
                parkingLotState.getFreeSpaces(),
                parkingLotState.getTotalSpaces()
        );

        for (ServerClientHandler display : allDisplays)
        {
            display.send(updateDisplayMessage);
        }
    }

    private void updateLight(int spotId, SpotState spotState)
    {
        ServerClientHandler lightHandler = clientRegistry.getLight(spotId);
        if (lightHandler == null) return;

        JsonMessage setLightMessage = JsonMessage.createSetLightMessage(SENDER_ID, spotState, spotId);

        lightHandler.send(setLightMessage);
    }

    private void validateRegistration(String clientId, ClientType clientType, Integer spotId, boolean isRegistered)
    {
        validateClientNotAlreadyRegistered(isRegistered);
        validateClientId(clientId);
        validateClientType(clientType);

        if (clientType != ClientType.DISPLAY)
        {
            validateSpotId(spotId);
            validateAgainstDuplicateRegistration(clientType, spotId);
        }


        // TODO maybe more needed - some sort of unique constraint for clientId ¯\(ツ)/¯
    }

    private void validateClientNotAlreadyRegistered(boolean isRegistered)
    {
        if (isRegistered) throw new IllegalStateException("This connection is already registered.");
    }

    private void validateAgainstDuplicateRegistration(ClientType clientType, Integer spotId)
    {
        switch (clientType)
        {
            case LIGHT ->
            {
                if (clientRegistry.getLight(spotId) != null)
                {
                    throw new IllegalStateException("A LIGHT is already registered for spot " + spotId + ".");
                }
            }
            case SENSOR ->
            {
                if (clientRegistry.getSensor(spotId) != null)
                {
                    throw new IllegalStateException("A SENSOR is already registered for spot " + spotId + ".");
                }
            }
        }
    }

    private void validateClientId(String clientId)
    {
        String name = "Client ID";
        Check.Against.nullValue(clientId, name);
        Check.Against.isBlank(clientId, name);
    }

    private void validateClientType(ClientType clientType)
    {
        Check.Against.nullValue(clientType, "Client Type");
    }

    private void validateSpotId(Integer spotId)
    {
        String name = "Spot ID";
        Check.Against.nullValue(spotId, name);
        Check.That.isPositive(spotId, name);
        Check.That.isLessThanOrEqual(spotId, parkingLotState.getTotalSpaces(), name);
    }

    private JsonMessage createSyncMessage(Integer responseMessageId,
                                          ClientType clientType, Integer spotId, SpotState spotState)
    {
        if (clientType == ClientType.LIGHT)
        {
            String color = switch (spotState)
            {
                case FREE -> "green";
                case OCCUPIED -> "red";
                case UNKNOWN -> "yellow";
            };

            return JsonMessage.createSyncStateMessage(SENDER_ID, responseMessageId,
                    clientType, spotId, spotState, color,
                    null, null);
        }

        if (clientType == ClientType.DISPLAY)
        {
            return JsonMessage.createSyncStateMessage(SENDER_ID, responseMessageId,
                    clientType, null, null, null, parkingLotState.getFreeSpaces(),
                    parkingLotState.getTotalSpaces());
        }
        return null;
    }
}
