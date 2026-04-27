package presentation.core;

import javafx.scene.control.TextArea;
import socket.json.ClientType;
import socket.json.JsonMessage;
import socket.json.MessageType;


public class DisplayUtil
{

    public static void display(TextArea textArea, JsonMessage message)
    {
        MessageType messageType = message.getHEADER().TYPE();
        switch (messageType)
        {
            case ACK -> displayACK(textArea, message);
            case ERROR -> displayError(textArea, message);
            case SYNC_STATE -> displaySyncState(textArea, message);
            case SET_LIGHT -> displaySetLight(textArea, message);
            case UPDATE_TOTAL -> displayUpdateTotal(textArea, message);
            case BROADCAST -> displayBroadCast(textArea, message);
        }
    }

    public static void displayACK(TextArea textArea, JsonMessage message)
    {
        String displayMessage =
                "\tType: " + MessageType.ACK + "\n" +
                        "\tMessage: " + message.getBODY().TEXT();
        writeToTextField(textArea, displayMessage);
    }

    public static void displayError(TextArea textArea, JsonMessage message)
    {
        String displayMessage =
                "\tType: " + MessageType.ERROR + "\n" +
                        "\tError: " + message.getBODY().ERROR_DESCRIPTION();
        writeToTextField(textArea, displayMessage);
    }

    public static void displaySyncState(TextArea textArea, JsonMessage message)
    {
        ClientType clientType = message.getBODY().CLIENT_TYPE();

        if (clientType == ClientType.DISPLAY)
        {
            String displayMessage =
                    "\tType: " + MessageType.SYNC_STATE + "\n" +
                            "\tClient Type: " + message.getBODY().CLIENT_TYPE() + "\n" +
                            "\tTotal spaces: " + message.getBODY().TOTAL_SPACES() + "\n" +
                            "\tFree spaces: " + message.getBODY().FREE_SPACES();

            writeToTextField(textArea, displayMessage);
        }

        if (clientType == ClientType.LIGHT)
        {
            String displayMessage =
                    "\tType: " + MessageType.SYNC_STATE + "\n" +
                            "\tClient Type: " + message.getBODY().CLIENT_TYPE() + "\n" +
                            "\tSpot number: " + message.getBODY().SPOT_ID() + "\n" +
                            "\tSpot state: " + message.getBODY().spotState() + "\n" +
                            "\tColor: " + message.getBODY().COLOR();

            writeToTextField(textArea, displayMessage);
        }
    }

    public static void displaySetLight(TextArea textArea, JsonMessage message)
    {
        String displayMessage =
                "\tType: " + MessageType.SET_LIGHT + "\n" +
                        "\tSpot number: " + message.getBODY().SPOT_ID() + "\n" +
                        "\tSpot state: " + message.getBODY().spotState() + "\n" +
                        "\tColor: " + message.getBODY().COLOR();

        writeToTextField(textArea, displayMessage);

    }

    public static void displayUpdateTotal(TextArea textArea, JsonMessage message)
    {
        String displayMessage =
                "\tType: " + MessageType.UPDATE_TOTAL + "\n" +
                        "\tTotal spaces: " + message.getBODY().TOTAL_SPACES() + "\n" +
                        "\tFree spaces: " + message.getBODY().FREE_SPACES();

        writeToTextField(textArea, displayMessage);
    }

    public static void displayBroadCast(TextArea textArea, JsonMessage message)
    {

    }

    private static void writeToTextField(TextArea textArea, String message)
    {
        textArea.appendText("\n\nMessage received:\n" + message);
    }
}
