package presentation.views;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import presentation.core.AcceptsSpotId;
import socket.ClientSocketManager;
import socket.json.ClientType;
import socket.json.JsonMessage;
import socket.json.MessageType;
import socket.json.SpotState;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LightController implements AcceptsSpotId, PropertyChangeListener
{
    @FXML
    private Label spotIdLbl;
    @FXML
    private VBox lightPane;

    private ClientSocketManager lightClient;


    @FXML
    public void initialize() {
        lightClient = new ClientSocketManager("localhost", 6789);
        lightClient.addListener(this);
    }

    private void setOccupied() {
        lightPane.getStyleClass().add("spot-occupied");
    }

    private void setFreed() {
        lightPane.getStyleClass().add("spot-free");
    }

    private void setUnknown() {
        lightPane.getStyleClass().add("spot-unknown");
    }

    @Override
    public void argument(int spotId)
    {
        lightClient.register("light-" + spotId, spotId, ClientType.LIGHT);
        spotIdLbl.textProperty().set(spotId + "");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        MessageType type = MessageType.valueOf(evt.getPropertyName());
        switch (type){
            case ACK -> System.out.println("LightController received ACK");
            case ERROR -> System.out.println("LightController received ERROR");
            case SET_LIGHT, SYNC_STATE -> {
                SpotState spotState = ((JsonMessage) evt.getNewValue()).getBODY().spotState();
                changeLight(spotState);
            }

        }
    }

    private void changeLight(SpotState spotState)
    {
        lightPane.getStyleClass().removeAll("spot-occupied", "spot-free", "spot-unknown");

        switch (spotState) {
            case FREE -> setFreed();
            case OCCUPIED -> setOccupied();
            case UNKNOWN -> setUnknown();
        }
    }
}
