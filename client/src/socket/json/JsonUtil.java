package socket.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class JsonUtil
{
  private static final Gson GSON = new GsonBuilder().create();

  public static String toJson(JsonMessage message)
  {
    return GSON.toJson(message);
  }

  public static JsonMessage fromJson(String json)
  {
    return GSON.fromJson(json, JsonMessage.class);
  }

}
