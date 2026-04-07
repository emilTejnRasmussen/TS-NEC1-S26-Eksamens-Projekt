package server.socket;

import java.util.HashSet;
import java.util.Set;

public class ServerClientHandlerPool
{
    private final Set<ServerClientHandler> CLIENT_HANDLERS;

    public ServerClientHandlerPool()
    {
        this.CLIENT_HANDLERS = new HashSet<>();
    }

    public void broadcast(String message, ServerClientHandler source)
    {
        for (ServerClientHandler handler : CLIENT_HANDLERS)
            if (!handler.equals(source))
                handler.send(message);
    }

    public void add(ServerClientHandler handler)
    {

    }
}
