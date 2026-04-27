package presentation.views;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import presentation.core.AcceptsIntegerArgument;
import presentation.core.DisplayUtil;
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

    @FXML
    public void initialize()
    {
        displayClient = new ClientSocketManager("localhost", 6789);
        displayClient.addListener(this);
    }

    @Override
    public void argument(int clientId)
    {
        displayClient.register("display-" + clientId, null, ClientType.DISPLAY);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        JsonMessage messageReceived = (JsonMessage) evt.getNewValue();
        MessageType type = MessageType.valueOf(evt.getPropertyName());

        switch (type){
            case ACK -> System.out.println("DisplayController received ACK");
            case ERROR -> System.out.println("DisplayController received ERROR");
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

}
