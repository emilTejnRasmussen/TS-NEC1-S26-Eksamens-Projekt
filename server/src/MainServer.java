import registry.ClientRegistry;
import service.ParkingLotService;
import socket.ServerSocketManager;
import state.ParkingLotState;

void main() {
    ClientRegistry clientRegistry = new ClientRegistry();
    ParkingLotState parkingLotState = new ParkingLotState(40);
    ParkingLotService parkingLotService = new ParkingLotService(clientRegistry, parkingLotState);

    new ServerSocketManager(6789, clientRegistry, parkingLotService);
}
