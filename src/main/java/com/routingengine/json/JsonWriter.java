package com.routingengine.json;

import java.io.IOException;
import java.io.OutputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


public class JsonWriter
{
    private final OutputStream outputStream;
    private final Gson gson;
    private StringBuilder buffer;
    
    public JsonWriter(OutputStream outputStream)
    {
        this.outputStream = outputStream;
        
        gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();
        
        initializeBuffer();
    }
    
    public final void initializeBuffer()
    {
        buffer = new StringBuilder();
    }
    
    public void write(byte[] bytes)
        throws IOException
    {
        outputStream.write(bytes);
    }
    
    public void writeString(String string)
        throws IOException
    {
        buffer.append(string);
    }
    
    public void writeLine(String line)
        throws IOException
    {
        writeString(line + "\n");
    }
    
    public void writeJsonObject(JsonObject jsonObject)
        throws IOException
    {
        writeLine(gson.toJson(jsonObject));
    }
    
    public final void flush()
        throws IOException
    {
        final String CHARSET = "UTF-8";
        
        String message = buffer.toString();
        
        write(message.getBytes(CHARSET));
        
        outputStream.flush();
        
        initializeBuffer();
    }
}
