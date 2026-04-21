package presentation.views;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import presentation.core.ViewManager;

import java.io.IOException;
import java.util.Optional;

public class ClientPickerMenuController
{
    @FXML
    public void handleLightPicked() throws IOException
    {
        Integer spotId = showSpotIdSelectionPopup("LIGHT");
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
        Integer spotId = showSpotIdSelectionPopup("SENSOR");
        if (spotId == null)
        {
            System.out.println("Dialog box closed");
            return;
        }

        System.out.println("Sensor registered on spot " + spotId);
        ViewManager.showSensor(spotId);
    }

    @FXML
    public void handleDisplayPicked()
    {

    }

    private Integer showSpotIdSelectionPopup(String clientType)
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Spot picker");
        dialog.setHeaderText("Pick spot to register " + clientType);
        dialog.setContentText("Spot number: ");

        while (true) {
            Optional<String> result = dialog.showAndWait();

            // User pressed Cancel or closed the dialog
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
