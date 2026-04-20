import socket.ClientSocketManager;
import socket.json.ClientType;

void main() {
    ClientSocketManager sensorClient = new ClientSocketManager("localhost", 6789);
    sensorClient.register("sensor-1", 1, ClientType.SENSOR);

    Scanner input = new Scanner(System.in);
    boolean running = true;

    while (running)
    {
        System.out.print("> ");
        String line = input.nextLine();

        switch (line)
        {
            case "park" -> sensorClient.carParked("sensor-1", 1);
            case "leave" -> sensorClient.carLeft("sensor-1", 1);
            case "heartbeat" -> sensorClient.heartbeat("sensor-1", 1, ClientType.SENSOR);
            case "quit" -> running = false;
            default -> System.out.println(
                    "Commands: park, leave, heartbeat, quit");
        }
    }

    sensorClient.disconnect();


}