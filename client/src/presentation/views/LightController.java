package presentation.views;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import presentation.core.AcceptsIntegerArgument;
import presentation.core.DisplayUtil;
import presentation.core.ErrorUtil;
import presentation.core.HeartbeatManager;
import socket.ClientSocketManager;
import socket.json.ClientType;
import socket.json.JsonMessage;
import socket.json.MessageType;
import socket.json.SpotState;

import java.awt.datatransfer.ClipboardOwner;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LightController implements AcceptsIntegerArgument, PropertyChangeListener
{
    @FXML
    private TextArea textArea;
    @FXML
    private Label spotIdLbl;
    @FXML
    private VBox lightPane;

    private ClientSocketManager lightClient;
    private HeartbeatManager heartbeatManager;


    @FXML
    public void initialize() {
        lightClient = new ClientSocketManager("localhost", 6789);
        lightClient.addListener(this);
    }

    @Override
    public void argument(int spotId)
    {
        String senderId = "light-" + spotId;
        lightClient.register(senderId, spotId, ClientType.LIGHT);
        spotIdLbl.setText(spotId + "");

        heartbeatManager = new HeartbeatManager(
                lightClient,
                senderId,
                spotId,
                ClientType.LIGHT
        );

        heartbeatManager.start();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        JsonMessage messageReceived = (JsonMessage) evt.getNewValue();
        MessageType type = MessageType.valueOf(evt.getPropertyName());

        switch (type){
            case ACK -> System.out.println("LightController received ACK");
            case BROADCAST -> System.out.println("Broadcast message from server: " + messageReceived.getBODY().TEXT());
            case ERROR -> {
                String errorMessage = messageReceived.getBODY().ERROR_DESCRIPTION();
                ErrorUtil.handleError(errorMessage, lightClient, this);
            }
            case SET_LIGHT, SYNC_STATE -> {
                SpotState spotState = messageReceived.getBODY().SPOT_STATE();
                Platform.runLater(() -> changeLight(spotState));
            }
        }
        DisplayUtil.display(textArea, messageReceived);
    }

    private void changeLight(SpotState spotState)
    {
        lightPane.getStyleClass().removeAll("spot-occupied", "spot-free", "spot-unknown");

        switch (spotState) {
            case FREE -> lightPane.getStyleClass().add("spot-free");
            case OCCUPIED -> lightPane.getStyleClass().add("spot-occupied");
            case UNKNOWN -> lightPane.getStyleClass().add("spot-unknown");
        }
    }

    public void handleHeartbeatToggle()
    {
        if (heartbeatManager.isEnabled()) heartbeatManager.pause();
        else heartbeatManager.resume();
    }
}
