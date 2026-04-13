package server.socket.json;

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

  //  CREATE MESSAGES
  public static JsonMessage createErrorMessage(String senderId,
      String errorMessage, int responseMessageId)
  {
    Header header = createHeader(senderId, responseMessageId,
        MessageType.ERROR);

    Body body = new Body(null, errorMessage);
    return new JsonMessage(header, body);
  }

  public static JsonMessage createBroadcastMessage(
      String senderId, String text)
  {
    Header header = createHeader(senderId, null,
        MessageType.BROADCAST);

    Body body = new Body(text, null);
    return new JsonMessage(header, body);
  }

  public static JsonMessage createBroadcastSpotStatusChangedMessage(
      String senderId, String text)
  {
    Header header = createHeader(senderId, null,
        MessageType.BROADCAST_SPOT_STATUS_CHANGED);

    Body body = new Body(text, null);
    return new JsonMessage(header, body);
  }

  public static JsonMessage createBroadcastTotalChangeMessage(String senderId,
      String text)
  {
    Header header = createHeader(senderId, null,
        MessageType.BROADCAST_TOTAL_CHANGED);

    Body body = new Body(text, null);
    return new JsonMessage(header, body);
  }

  private static Header createHeader(String senderId, Integer responseMessageId,
      MessageType type)
  {
    return new Header(CURRENT_VERSION, NEXT_ID.getAndIncrement(),
        responseMessageId, Instant.now().toString(), senderId, type.toString());
  }

  public static class Header
  {
    private final String VERSION;
    private final int MESSAGE_ID;
    private final Integer MESSAGE_RESPONSE_ID;
    private final String TIMESTAMP;
    private final String SENDER_ID;
    private final String TYPE;

    public Header(String VERSION, int MESSAGE_ID, Integer MESSAGE_RESPONSE_ID,
        String TIMESTAMP, String SENDER_ID, String TYPE)
    {
      this.VERSION = VERSION;
      this.MESSAGE_ID = MESSAGE_ID;
      this.MESSAGE_RESPONSE_ID = MESSAGE_RESPONSE_ID;
      this.TIMESTAMP = TIMESTAMP;
      this.SENDER_ID = SENDER_ID;
      this.TYPE = TYPE;
    }

    public String getVERSION()
    {
      return VERSION;
    }

    public int getMESSAGE_ID()
    {
      return MESSAGE_ID;
    }

    public Integer getMESSAGE_RESPONSE_ID()
    {
      return MESSAGE_RESPONSE_ID;
    }

    public String getTIMESTAMP()
    {
      return TIMESTAMP;
    }

    public String getSENDER_ID()
    {
      return SENDER_ID;
    }

    public String getTYPE()
    {
      return TYPE;
    }
  }

  public static class Body
  {
    private final String TEXT;
    private final String ERROR_DESCRIPTION;

    public Body(String TEXT, String ERROR_DESCRIPTION)
    {
      this.TEXT = TEXT;
      this.ERROR_DESCRIPTION = ERROR_DESCRIPTION;
    }

    public String getTEXT()
    {
      return TEXT;
    }

    public String getERROR_DESCRIPTION()
    {
      return ERROR_DESCRIPTION;
    }
  }

}
