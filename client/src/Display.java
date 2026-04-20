import socket.ClientSocketManager;
import socket.json.ClientType;

void main() {
    ClientSocketManager displayClient = new ClientSocketManager("localhost", 6789);
    displayClient.register("light-1", 1, ClientType.DISPLAY);

    Scanner input = new Scanner(System.in);
    boolean running = true;

    while (running)
    {
        System.out.print("> ");
        String line = input.nextLine();

        switch (line)
        {
            case "heartbeat" -> displayClient.heartbeat("display-1", 1, ClientType.DISPLAY);
            case "quit" -> running = false;
            default -> System.out.println(
                    "Commands: heartbeat, quit");
        }
    }

    displayClient.disconnect();


}