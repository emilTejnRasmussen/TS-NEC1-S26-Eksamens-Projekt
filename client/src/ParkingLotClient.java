import javafx.application.Application;

import javafx.stage.Stage;
import presentation.core.ViewManager;

public class ParkingLotClient extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        ViewManager.setPrimaryStage(primaryStage);
        ViewManager.showClientPickerMenu();
    }
}
