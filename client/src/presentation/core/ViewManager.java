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
        showClient("/Light.fxml", spotId);
    }

    public static void showSensor(Integer spotId) throws IOException
    {
        showClient("/Sensor.fxml", spotId);
    }

    public static void showDisplay() {

    }

    private static void showClient(String url, int spotId) throws IOException
    {
        primaryStage.hide();
        primaryStage.setScene(load(url, spotId));
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
    }

    private static Scene load(String url, int spotId) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(ViewManager.class.getResource(url)));

        VBox vBox = loader.load();
        AcceptsSpotId controller = loader.getController();
        controller.argument(spotId);

        return new Scene(vBox);
    }
}
