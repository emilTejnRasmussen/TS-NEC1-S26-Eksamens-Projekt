package registry;

import socket.ServerClientHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClientRegistry
{
    private final Set<ServerClientHandler> allClients = new HashSet<>();
    private final Map<Integer, ServerClientHandler> sensors = new HashMap<>();
    private final Map<Integer, ServerClientHandler> lights = new HashMap<>();
    private final Set<ServerClientHandler> displays = new HashSet<>();

    public synchronized void addClient(ServerClientHandler serverClientHandler){
        allClients.add(serverClientHandler);
    }

    public synchronized void registerSensor(int spotId, ServerClientHandler handler) {
        sensors.put(spotId, handler);
    }

    public synchronized void registerLight(int spotId, ServerClientHandler handler) {
        lights.put(spotId, handler);
    }

    public synchronized void registerDisplay(ServerClientHandler handler) {
        displays.add(handler);
    }

    public synchronized ServerClientHandler getLight(int spotId) {
        return lights.get(spotId);
    }

    public synchronized Set<ServerClientHandler> getDisplays() {
        return new HashSet<>(displays);
    }

    public synchronized ServerClientHandler getSensor(int spotId)
    {
        return sensors.get(spotId);
    }

    public synchronized Set<ServerClientHandler> getAllClients() {


        return new HashSet<>(allClients);
    }

    public synchronized void removeClient(ServerClientHandler handler)
    {
        allClients.remove(handler);
        sensors.values().remove(handler);
        lights.values().remove(handler);
        displays.remove(handler);
    }
}
