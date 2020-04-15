package com.routingengine.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;


public abstract class JsonConnectionHandler
{
    protected Socket socket = null;
    protected JsonReader jsonReader = null;
    protected JsonWriter jsonWriter = null;
    public static final int SLEEP_MILLIS = 10;
    
    protected JsonReader getJsonReader(InputStream inputStream)
    {
        return new JsonReader(inputStream);
    }
    
    protected JsonWriter getJsonWriter(OutputStream outputStream)
    {
        return new JsonWriter(outputStream);
    }
    
    public void connect(Socket socket)
        throws IOException, ConnectionException
    {
        this.socket = socket;
        
        jsonReader = getJsonReader(socket.getInputStream());
        jsonWriter = getJsonWriter(socket.getOutputStream());
    }
    
    public final String getAddress()
    {
        return socket.getInetAddress().getHostAddress();
    }
    
    public final void waitForInput()
        throws IOException, InterruptedException
    {
        while (!jsonReader.ready())
            TimeUnit.MILLISECONDS.sleep(SLEEP_MILLIS);
    }
    
    public abstract void runMainLoop()
        throws IOException, InterruptedException, EndConnectionException;
    
    public static class ConnectionException extends Exception
    {
        public ConnectionException()
        {
            super();
        }
        
        public ConnectionException(String message)
        {
            super(message);
        }
    }
    
    public static class EndConnectionException extends ConnectionException
    {
        public EndConnectionException()
        {
            super();
        }
        
        public EndConnectionException(String message)
        {
            super(message);
        }
    }
}
