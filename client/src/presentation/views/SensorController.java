package presentation.views;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import presentation.core.AcceptsIntegerArgument;
import presentation.core.DisplayUtil;
import presentation.core.ErrorUtil;
import presentation.core.HeartbeatManager;
import socket.ClientSocketManager;
import socket.json.ClientType;
import socket.json.JsonMessage;
import socket.json.MessageType;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class SensorController implements AcceptsIntegerArgument, PropertyChangeListener
{
    @FXML
    private TextArea textField;
    @FXML
    private Label spotIdLbl;
    @FXML
    private Button sensorBtn;

    private String senderId;
    private int spotId;
    private boolean isOccupied;

    private ClientSocketManager sensorClient;
    private HeartbeatManager heartbeatManager;


    @FXML
    public void initialize()
    {
        sensorClient = new ClientSocketManager("localhost", 6789);
        sensorClient.addListener(this);

        textField.selectEnd();
    }

    @Override
    public void argument(int spotId)
    {
        this.spotId = spotId;
        this.senderId = "sensor-" + spotId;

        this.sensorClient.register(senderId, spotId, ClientType.SENSOR);
        this.spotIdLbl.setText(spotId + "");

        heartbeatManager = new HeartbeatManager(
                sensorClient,
                senderId,
                spotId,
                ClientType.SENSOR
        );

        heartbeatManager.start();
    }

    @FXML
    public void handleSensorFired()
    {
        if (isOccupied)
        {
            sensorClient.carLeft(senderId, spotId);
        } else
        {
            sensorClient.carParked(senderId, spotId);
        }

        isOccupied = !isOccupied;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        MessageType type = MessageType.valueOf(evt.getPropertyName());
        JsonMessage messageReceived = ((JsonMessage) evt.getNewValue());


        switch (type)
        {
            case ACK ->
            {
                System.out.println("SensorController received ACK");
                String buttonText = isOccupied ?
                        "Move" :
                        "Park";

                Platform.runLater(() -> sensorBtn.setText(buttonText));
            }
            case BROADCAST -> System.out.println("Broadcast message from server: " + messageReceived.getBODY().TEXT());
            case ERROR -> {
                String errorMessage = messageReceived.getBODY().ERROR_DESCRIPTION();
                ErrorUtil.handleError(errorMessage, sensorClient, this);
            }
        }

        DisplayUtil.display(textField, messageReceived);
    }

    public void handleHeartbeatToggle()
    {
        if (heartbeatManager.isEnabled()) heartbeatManager.pause();
        else heartbeatManager.resume();
    }
}
