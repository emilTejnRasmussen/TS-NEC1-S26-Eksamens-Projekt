package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClientHandler implements Runnable
{

  private BufferedReader in;
  private PrintWriter out;
  private final String CLIENT_ADDRESS;
  private final ServerClientHandlerPool HANDLER_POOL;
  private final String SENDER_ID;

  public ServerClientHandler(Socket socket, ServerClientHandlerPool handlerPool,
      String senderId)
  {
    this.HANDLER_POOL = handlerPool;
    this.SENDER_ID = senderId;

    CLIENT_ADDRESS =
        socket.getInetAddress().getHostAddress() + ":" + socket.getPort();

    System.out.println("Connection establish with client " + CLIENT_ADDRESS);

    try
    {
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream(), true);
    }
    catch (IOException e)
    {
      System.out.println(
          "Error: ServerClientHandler failed to establish streams to client :"
              + CLIENT_ADDRESS);
    }
  }

  public void send(String message)
  {
    // TODO

  }


  @Override public void run()
  {
    try
    {

      while(true)
      {


      }


    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }
}
