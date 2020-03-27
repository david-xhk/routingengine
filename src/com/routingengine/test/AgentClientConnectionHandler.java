package com.routingengine.test;

import static com.routingengine.Logger.log;
import static com.routingengine.json.JsonUtils.getAsString;
import static com.routingengine.json.JsonUtils.getAsJsonObject;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonResponse;


public class AgentClientConnectionHandler extends ClientConnectionHandler
{
    public Random random;
    public int i;
    public int j;
    
    @Override
    public void runMainLoop()
        throws IOException, InterruptedException
    {
        agentLog("initialized!");
        
        randomSleep(10);
        
        agentLog("creating new agent");
        JsonResponse response = newAgent(Map.of(i%3, true));
        String agentUUIDString = getUUID(response);
        agentLog("uuid -> " + agentUUIDString);
        
        randomSleep(10);
        
        agentLog("updating availability");
        response = updateAgentAvailability(agentUUIDString, true);
        agentLog(response);
        
        randomSleep(10);
        
        for (int k = 0; k < j; k++) {
            agentLog("taking support request");
            response = takeSupportRequest(agentUUIDString);
            agentLog(response);
            
            if (!response.didSucceed()) {
                agentLog("checking agent");
                response = checkAgent(agentUUIDString);
                agentLog(response);
                k--;
                continue;
            }
            
            String supportRequestUUIDString = getUUID(getAssignedSupportRequest(response));
        
            randomSleep(10);
        
            agentLog("closing support request");
            response = closeSupportRequest(supportRequestUUIDString);
            agentLog(response);
        }
    }
    
    public final void randomSleep(int timeout)
        throws InterruptedException
    {
        TimeUnit.SECONDS.sleep(random.nextInt(timeout));
    }
    
    public final void agentLog(JsonResponse jsonResponse)
    {
        agentLog(jsonResponse.toString());
    }
    
    public final void agentLog(String message)
    {
        log("Agent " + i + " " + message);
    }
    
    public static final String getUUID(JsonResponse response)
    {
        return getUUID(response.getPayload());
    }
    
    public static final String getUUID(JsonElement jsonElement)
    {
        return getAsString(jsonElement, "uuid");
    }
    
    public static final JsonObject getAssignedSupportRequest(JsonResponse response)
    {
        return getAsJsonObject(response.getPayload(), "assigned_support_request");
    }
}
