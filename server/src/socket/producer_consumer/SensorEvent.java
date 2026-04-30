package socket.producer_consumer;

import socket.ServerClientHandler;
import socket.json.JsonMessage;

public record SensorEvent(ServerClientHandler serverClientHandler, JsonMessage jsonMessage)
{}
