import socket.ClientSocketManager;
import socket.json.ClientType;

import java.util.Scanner;

ClientSocketManager sensor1;
ClientSocketManager light1;
ClientSocketManager display1;

void main()
{
    sensor1 = new ClientSocketManager("localhost", 6789);
    light1 = new ClientSocketManager("localhost", 6789);
    display1 = new ClientSocketManager("localhost", 6789);

    sensor1.register("sensor-1", 1, ClientType.SENSOR);
    light1.register("light-1", 1, ClientType.LIGHT);
    display1.register("display-1", null, ClientType.DISPLAY);

    Scanner input = new Scanner(System.in);
    boolean running = true;

    while (running)
    {
        System.out.print("> ");
        String line = input.nextLine();

        switch (line)
        {
            case "park" -> sensor1.carParked("sensor-1", 1);
            case "leave" -> sensor1.carLeft("sensor-1", 1);
            case "heartbeat sensor" -> sensor1.heartbeat("sensor-1", 1, ClientType.SENSOR);
            case "heartbeat light" -> light1.heartbeat("light-1", 1, ClientType.LIGHT);
            case "heartbeat display" -> display1.heartbeat("display-1", null, ClientType.DISPLAY);
            case "quit" -> running = false;
            default -> System.out.println(
                    "Commands: park, leave, heartbeat sensor, heartbeat light, heartbeat display, quit");
        }
    }

    sensor1.disconnect();
    light1.disconnect();
    display1.disconnect();
}