package presentation.views;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import presentation.core.ViewManager;
import socket.json.ClientType;

import java.io.IOException;
import java.util.Optional;

public class ClientPickerMenuController
{
    @FXML
    public void handleLightPicked() throws IOException
    {
        Integer spotId = showClientIdSelectionPopup(ClientType.LIGHT);
        if (spotId == null)
        {
            System.out.println("Dialog box closed");
            return;
        }

        System.out.println("Light registered on spot " + spotId);
        ViewManager.showLight(spotId);
    }

    @FXML
    public void handleSensorPicked() throws IOException
    {
        Integer spotId = showClientIdSelectionPopup(ClientType.SENSOR);
        if (spotId == null)
        {
            System.out.println("Dialog box closed");
            return;
        }

        System.out.println("Sensor registered on spot " + spotId);
        ViewManager.showSensor(spotId);
    }

    @FXML
    public void handleDisplayPicked() throws IOException
    {
        Integer clientId = showClientIdSelectionPopup(ClientType.DISPLAY);
        if (clientId == null)
        {
            System.out.println("Dialog box closed");
            return;
        }

        System.out.println("Display registered with clientId " + clientId);
        ViewManager.showDisplay(clientId);
    }

    private Integer showClientIdSelectionPopup( ClientType clientType)
    {
        String message = clientType == ClientType.DISPLAY ? "clientId" : "spot";

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(message + " picker");
        dialog.setHeaderText("Pick " + message + " to register " + clientType);
        dialog.setContentText(message + " number: ");

        while (true) {
            Optional<String> result = dialog.showAndWait();

            if (result.isEmpty()) {
                return null;
            }

            try {
                return Integer.parseInt(result.get().trim());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid input");
                alert.setHeaderText("Please enter a valid integer");
                alert.setContentText("Input must be a whole number.");
                alert.showAndWait();
            }
        }
    }
}
