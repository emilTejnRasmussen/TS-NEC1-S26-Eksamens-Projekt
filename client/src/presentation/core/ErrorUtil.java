package presentation.core;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import socket.ClientSocketManager;

import java.beans.PropertyChangeListener;
import java.io.IOException;

public class ErrorUtil
{
    private ErrorUtil()
    {
    }

    public static void handleError(String errorMessage, ClientSocketManager client, PropertyChangeListener clientListener) {
        if (client != null) {
            client.removeListener(clientListener);
        }

        new Thread(() -> {
            try {
                if (client != null) {
                    client.disconnect();
                }
            } catch (Exception e) {
                System.out.println("Error while disconnecting: " + e.getMessage());
            }

            Platform.runLater(() -> {
                try {
                    ViewManager.showClientPickerMenu();

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Connection error");
                    alert.setHeaderText("Client disconnected");
                    alert.setContentText(errorMessage);
                    alert.showAndWait();

                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("View error");
                    alert.setHeaderText("Could not return to client picker menu");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            });
        }).start();
    }
}
