package presentation.views;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

public class DisplayController implements AcceptsIntegerArgument, PropertyChangeListener
{
    @FXML
    private Label displayLbl;
    @FXML
    private TextArea textArea;

    private ClientSocketManager displayClient;
    private HeartbeatManager heartbeatManager;

    @FXML
    public void initialize()
    {
        displayClient = new ClientSocketManager("localhost", 6789);
        displayClient.addListener(this);
    }

    @Override
    public void argument(int clientId)
    {
        String senderId = "display-" + clientId;
        displayClient.register(senderId, null, ClientType.DISPLAY);
        heartbeatManager = new HeartbeatManager(
                displayClient,
                senderId,
                null,
                ClientType.DISPLAY
        );

        heartbeatManager.start();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        JsonMessage messageReceived = (JsonMessage) evt.getNewValue();
        MessageType type = MessageType.valueOf(evt.getPropertyName());

        switch (type){
            case ACK -> System.out.println("DisplayController received ACK");
            case BROADCAST -> System.out.println("Broadcast message from server: " + messageReceived.getBODY().TEXT());
            case ERROR -> {
                String errorMessage = messageReceived.getBODY().ERROR_DESCRIPTION();
                ErrorUtil.handleError(errorMessage, displayClient, this);
            }
            case UPDATE_TOTAL, SYNC_STATE -> {
                int freeSpaces = messageReceived.getBODY().FREE_SPACES();
                Platform.runLater(() -> changeDisplay(freeSpaces));
            }
        }
        DisplayUtil.display(textArea, messageReceived);
    }

    private void changeDisplay(int freeSpaces)
    {
        switch (freeSpaces)
        {
            case 0 -> displayLbl.setText("Ingen ledige");
            case 1 -> displayLbl.setText(freeSpaces + " ledig");
            default -> displayLbl.setText(freeSpaces + " ledige");
        }
    }

    public void handleHeartbeatToggle()
    {
        if (heartbeatManager.isEnabled()) heartbeatManager.pause();
        else heartbeatManager.resume();
    }
}
