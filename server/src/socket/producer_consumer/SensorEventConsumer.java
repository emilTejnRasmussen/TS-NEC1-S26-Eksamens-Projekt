package socket.producer_consumer;

import service.ParkingLotService;
import socket.json.MessageType;

import java.util.concurrent.BlockingQueue;

public class SensorEventConsumer implements Runnable
{
    private final BlockingQueue<SensorEvent> sensorQueue;
    private final ParkingLotService parkingLotService;

    public SensorEventConsumer(BlockingQueue<SensorEvent> sensorQueue,
                               ParkingLotService parkingLotService)
    {
        this.sensorQueue = sensorQueue;
        this.parkingLotService = parkingLotService;
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                SensorEvent event = sensorQueue.take();
                MessageType type = event.jsonMessage().getHEADER().TYPE();

                switch (type)
                {
                    case CAR_PARKED -> parkingLotService.handleCarParked(event.serverClientHandler(), event.jsonMessage());
                    case CAR_LEFT -> parkingLotService.handleCarLeft(event.serverClientHandler(), event.jsonMessage());
                }
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}