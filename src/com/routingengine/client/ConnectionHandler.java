package com.routingengine.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import com.routingengine.Logger;
import com.routingengine.json.JsonReader;
import com.routingengine.json.JsonWriter;


public abstract class ConnectionHandler
  implements Runnable, Closeable
{
  protected Socket socket = null;
  protected JsonReader jsonReader = null;
  protected JsonWriter jsonWriter = null;
  
  public final void connect(Socket socket)
    throws IOException
  {
    this.socket = socket;
    Logger.log("Connected to " + getAddress());
    
    jsonReader = new JsonReader(socket.getInputStream());
    jsonWriter = new JsonWriter(socket.getOutputStream());
  }
  
  public final String getAddress()
  {
    return socket.getInetAddress().getHostAddress();
  }
  
  protected final void waitForInput()
    throws IOException, InterruptedException
  {
    while (!jsonReader.ready())
      TimeUnit.MILLISECONDS.sleep(100);
  }
  
  protected abstract void runMainLoop() throws IOException, InterruptedException;
  
  @Override
  public final void run()
  {
    if (socket == null)
      throw new IllegalStateException("socket not connected!");
    
    try {      
      runMainLoop();
    }
    
    catch (IOException exception) {
      Logger.log("I/O error in socket " + getAddress());
      
      exception.printStackTrace();  
    }
    
    catch (InterruptedException exception) {
      Logger.log("Connection handler interrupted");
      
      return;
    }
    
    finally {
      Logger.log("Closing socket " + getAddress());
      
      close();
    }
  }
  
  @Override
  public void close()
  {
    ConnectionHandler.closeQuietly(jsonReader);
    ConnectionHandler.closeQuietly(jsonWriter);
    ConnectionHandler.closeQuietly(socket);
    
    Logger.log("Connection closed");
  }
  
  public static final void closeQuietly(Closeable input)
  {
    try {
      if (input != null)
        input.close();
    }
    
    catch (IOException e) {
      Logger.log(input.toString() + " failed to close");
    }
  }
}