package presentation.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ViewManager
{
    private static Stage primaryStage;

    public static void showClientPickerMenu(Stage stage) throws IOException
    {
        primaryStage = stage;

        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(ViewManager.class.getResource("/ClientPickerMenu.fxml")));
        VBox vBox = loader.load();
        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showLight(Integer spotId) throws IOException
    {
        primaryStage.hide();
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(ViewManager.class.getResource("/Light.fxml")));

        VBox vBox = loader.load();
        AcceptsSpotId controller = loader.getController();
        controller.argument(spotId);

        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void showSensor() {

    }

    public static void showDisplay() {

    }
}
