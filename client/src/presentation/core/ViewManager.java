package presentation.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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

        setIcon("picker");
        primaryStage.setTitle("ClientTypePicker");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showLight(Integer spotId) throws IOException
    {
        showClient("/Light.fxml", spotId);
        setIcon("light");
        primaryStage.setTitle("Light");
    }

    public static void showSensor(Integer spotId) throws IOException
    {
        showClient("/Sensor.fxml", spotId);
        setIcon("sensor");
        primaryStage.setTitle("Sensor");
    }

    public static void showDisplay(Integer clientId) throws IOException
    {
        showClient("/Display.fxml", clientId);
        setIcon("display");
        primaryStage.setTitle("Display");
    }

    private static void showClient(String url, Integer clientId) throws IOException
    {
        primaryStage.hide();
        primaryStage.setScene(load(url, clientId));
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private static Scene load(String url, Integer clientId) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(ViewManager.class.getResource(url)));
        VBox vBox = loader.load();

        Object controller = loader.getController();
        if (controller instanceof AcceptsIntegerArgument ctrl)
        {
            ctrl.argument(clientId);
        }

        return new Scene(vBox);
    }

    private static void setIcon(String type)
    {
        primaryStage.getIcons().clear();

        Image icon = new Image(Objects.requireNonNull(
                ViewManager.class.getResourceAsStream("/icons/" + type + ".png")
        ));
        primaryStage.getIcons().add(icon);
    }
}
