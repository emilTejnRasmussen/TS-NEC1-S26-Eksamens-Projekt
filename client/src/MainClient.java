import socket.ClientSocketManager;
import socket.json.ClientType;
ClientSocketManager socket;
void main() {
    socket = new ClientSocketManager("localhost", 6789);
    boolean running = true;
    Scanner input = new Scanner(System.in);

    createClients();
    while (running) {
        System.out.print("> ");
        String line = input.nextLine();

        switch (line) {
            case "reg client" -> socket.register("sensor-1", 1, ClientType.SENSOR);
        }
    }
    socket.disconnect();
}

private void createClients()
{
    for (int i = 1; i <= 5; i++)
    {
        createSensor(i);
        createLight(i);
    }
    createDisplay();
}

private void createSensor(int i)
{
    socket.register("sensor-" + i, i, ClientType.SENSOR);
}

private void createLight(int i)
{
    socket.register("light-" + i, i, ClientType.LIGHT);
}

private void createDisplay()
{
    socket.register("display-" + 1, null, ClientType.LIGHT);
}