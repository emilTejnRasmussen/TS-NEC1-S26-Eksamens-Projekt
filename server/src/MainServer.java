import socket.ServerClientHandlerPool;
import service.ParkingLotService;
import socket.ServerSocketManager;
import state.ParkingLotState;

void main() {
    ServerClientHandlerPool serverClientHandlerPool = new ServerClientHandlerPool();
    ParkingLotState parkingLotState = new ParkingLotState(40);
    ParkingLotService parkingLotService = new ParkingLotService(serverClientHandlerPool, parkingLotState);

    new ServerSocketManager(6789, serverClientHandlerPool, parkingLotService);
}
