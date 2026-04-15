package socket.json;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class JsonMessage
{
  public static final String CURRENT_VERSION = "1.0";
  public static final AtomicInteger NEXT_ID = new AtomicInteger(1);

  private final Header HEADER;
  private final Body BODY;

  private JsonMessage(Header header, Body body)
  {
    this.HEADER = header;
    this.BODY = body;
  }

  public Header getHEADER()
  {
    return HEADER;
  }

  public Body getBODY()
  {
    return BODY;
  }

  //  MESSAGES

  // Client -> Server
  public static JsonMessage createRegisterMessage(String senderId,
      Integer spotId, ClientType clientType)
  {
    Header header = createHeader(senderId, null, MessageType.REGISTER);

    Body body;

    if (spotId == null)
    {
      body = new Body(clientType + " registered ", null, clientType, null,
          null, null, null, null);
    }
    else
    {
      body = new Body(clientType + " registered at parking spot: " + spotId,
          null, clientType, spotId, null, null, null, null);
    }
    return new JsonMessage(header, body);
  }

  public static JsonMessage createHeartbeatMessage(String senderId,
      Integer spotId, ClientType clientType)
  {
    Header header = createHeader(senderId, null, MessageType.HEARTBEAT);

    Body body = new Body(clientType + " is alive", null, clientType, spotId,
        null, null, null, null);

    return new JsonMessage(header, body);
  }

  public static JsonMessage createCarParkedMessage(String senderId, int spotId)
  {
    Header header = createHeader(senderId, null, MessageType.CAR_PARKED);

    String message = "Parking spot " + spotId + " is occupied";
    Body body = new Body(message, null,
        ClientType.SENSOR, spotId, null, null, null, null);
    return new JsonMessage(header, body);
  }

  public static JsonMessage createCarLeftMessage(String senderId, int spotId)
  {
    Header header = createHeader(senderId, null, MessageType.CAR_LEFT);

    String message = "Parking spot " + spotId + " is freed";
    Body body = new Body(message, null, ClientType.SENSOR, spotId, null, null,
        null, null);
    return new JsonMessage(header, body);
  }

  // Server -> client
  public static JsonMessage createAckMessage(String senderId,
      int responseMessageId, MessageType type)
  {
    Header header = createHeader(senderId, responseMessageId, MessageType.ACK);

    Body body = new Body(type + " acknowledged", null, null, null, null, null,
        null, null);
    return new JsonMessage(header, body);
  }

  public static JsonMessage createErrorMessage(String senderId,
      String errorMessage, int responseMessageId)
  {
    Header header = createHeader(senderId, responseMessageId,
        MessageType.ERROR);

    Body body = new Body(null, errorMessage, null, null, null, null, null, null);
    return new JsonMessage(header, body);
  }

  public static JsonMessage createSetLightMessage(String senderId,
      boolean isOccupied, Integer spotId)
  {
    Header header = createHeader(senderId, null, MessageType.SET_LIGHT);

    String color = isOccupied ? "RED" : "GREEN";

    Body body = new Body("Change to color to " + color, null, null, spotId,
        isOccupied, color, null, null);
    return new JsonMessage(header, body);
  }

  public static JsonMessage createUpdateTotalMessage(String senderId,
      int freeSpaces, int totalSpaces)
  {
    Header header = createHeader(senderId, null, MessageType.UPDATE_TOTAL);

    Body body = new Body(MessageType.UPDATE_TOTAL.toString(), null, null, null,
        null, null, freeSpaces, totalSpaces);
    return new JsonMessage(header, body);
  }

  public static JsonMessage createSyncStateMessage(String senderId,
      Integer responseMessageId, ClientType clientType, Integer spotId,
      Boolean occupied, String color, Integer freeSpaces, Integer totalSpaces)
  {
    Header header = createHeader(senderId, responseMessageId,
        MessageType.SYNC_STATE);

    Body body = new Body(
        "STATE_SYNC",
        null,
        clientType,
        spotId,
        occupied,
        color,
        freeSpaces,
        totalSpaces
    );
    return new JsonMessage(header, body);
  }

  public static JsonMessage createBroadcastMessage(String senderId, String text)
  {
    Header header = createHeader(senderId, null, MessageType.BROADCAST);

    Body body = new Body(text, null, null, null, null, null, null, null);
    return new JsonMessage(header, body);
  }

  public static JsonMessage createBroadcastSpotStatusChangedMessage(
      String senderId, String text)
  {
    Header header = createHeader(senderId, null,
        MessageType.BROADCAST_SPOT_STATUS_CHANGED);

    Body body = new Body(text, null, null, null, null, null, null, null);
    return new JsonMessage(header, body);
  }

  public static JsonMessage createBroadcastTotalChangeMessage(String senderId,
      String text)
  {
    Header header = createHeader(senderId, null,
        MessageType.BROADCAST_TOTAL_CHANGED);

    Body body = new Body(text, null, null, null, null, null, null, null);
    return new JsonMessage(header, body);
  }

  private static Header createHeader(String senderId, Integer responseMessageId,
      MessageType type)
  {
    return new Header(CURRENT_VERSION, NEXT_ID.getAndIncrement(),
        responseMessageId, Instant.now().toString(), senderId, type);
  }

  public record Header(String VERSION, int MESSAGE_ID,
                       Integer MESSAGE_RESPONSE_ID, String TIMESTAMP,
                       String SENDER_ID, MessageType TYPE)
  {
  }

  public record Body(String TEXT, String ERROR_DESCRIPTION,
                     ClientType CLIENT_TYPE, Integer SPOT_ID, Boolean OCCUPIED,
                     String COLOR, Integer FREE_SPACES, Integer TOTAL_SPACES)
  {
  }

}
