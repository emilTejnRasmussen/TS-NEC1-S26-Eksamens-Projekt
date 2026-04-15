package socket;

import socket.json.JsonMessage;

import java.util.*;

public class ServerClientHandlerPool
{
    private final Set<ServerClientHandler> CLIENT_HANDLERS;
    private final Map<Integer, ServerClientHandler> SENSOR_HANDLERS;
    private final Map<Integer, ServerClientHandler> LIGHT_HANDLERS;
    private final Set<ServerClientHandler> DISPLAY_HANDLERS;

    private final Map<Integer, Boolean> occupiedBySpot;
    private int totalSpaces;

  public ServerClientHandlerPool()
    {
        this.CLIENT_HANDLERS = new HashSet<>();
        this.SENSOR_HANDLERS = new HashMap<>();
        this.LIGHT_HANDLERS = new HashMap<>();
        this.DISPLAY_HANDLERS = new HashSet<>();
        this.occupiedBySpot = new HashMap<>();
        this.totalSpaces = 0;
    }

    public void broadcast(JsonMessage message, ServerClientHandler source)
    {
        for (ServerClientHandler handler : CLIENT_HANDLERS)
            if (!handler.equals(source))
                handler.send(message);
    }

    public void add(ServerClientHandler handler)
    {
      CLIENT_HANDLERS.add(handler);
    }

  public List<ServerClientHandler> getClients()
  {
    return new ArrayList<>(CLIENT_HANDLERS);
  }

  public synchronized void registerSensor(Integer spotId, ServerClientHandler serverClientHandler) {
    if (!SENSOR_HANDLERS.containsKey(spotId)){
      totalSpaces++;
    }
    SENSOR_HANDLERS.put(spotId, serverClientHandler);
  }

  public synchronized void registerLight(Integer spotId, ServerClientHandler serverClientHandler) {
    LIGHT_HANDLERS.put(spotId, serverClientHandler);
  }

  public synchronized void registerDisplay(ServerClientHandler serverClientHandler) {
    DISPLAY_HANDLERS.add(serverClientHandler);
  }

  public synchronized void sendToLight(Integer spotId, JsonMessage message) {
    ServerClientHandler handler = getLight(spotId);

    if (handler != null) {
      handler.send(message);
    }
  }

  public synchronized void sendToAllDisplays(JsonMessage message) {
    for(ServerClientHandler handler : DISPLAY_HANDLERS) {
        handler.send(message);
    }
  }

  public synchronized void removeSensor(Integer spotId) {
    if (SENSOR_HANDLERS.remove(spotId) != null) {
      totalSpaces--;
    }
  }

  public synchronized void removeLight(Integer spotId) {
    LIGHT_HANDLERS.remove(spotId);
  }

  public synchronized void removeDisplay(ServerClientHandler serverClientHandler) {
    DISPLAY_HANDLERS.remove(serverClientHandler);
  }

  public synchronized ServerClientHandler getSensor(Integer spotId) {
    return SENSOR_HANDLERS.get(spotId);
  }

  public synchronized ServerClientHandler getLight(Integer spotId) {
    return LIGHT_HANDLERS.get(spotId);
  }

  public synchronized List<ServerClientHandler> getDisplays() {
    return new ArrayList<>(DISPLAY_HANDLERS);
  }

  public synchronized void setOccupied(Integer spotId, boolean occupied)
  {
    occupiedBySpot.put(spotId, occupied);
  }

  public synchronized boolean isOccupied(Integer spotId)
  {
    return occupiedBySpot.getOrDefault(spotId, false);
  }

  public synchronized int getFreeSpaces()
  {
    int occupiedCount = 0;
    for (boolean occupied : occupiedBySpot.values())
    {
      if (occupied) occupiedCount++;
    }
    return totalSpaces - occupiedCount;
  }

  public synchronized int getTotalSpaces()
  {
    return totalSpaces;
  }
}
