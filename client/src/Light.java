import socket.ClientSocketManager;
import socket.json.ClientType;

void main() {
    ClientSocketManager lightClient = new ClientSocketManager("localhost", 6789);
    lightClient.register("light-1", 1, ClientType.LIGHT);

    Scanner input = new Scanner(System.in);
    boolean running = true;

    while (running)
    {
        System.out.print("> ");
        String line = input.nextLine();

        switch (line)
        {
            case "heartbeat" -> lightClient.heartbeat("light-1", 1, ClientType.LIGHT);
            case "quit" -> running = false;
            default -> System.out.println(
                    "Commands: heartbeat, quit");
        }
    }

    lightClient.disconnect();
}