package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.Agent;
import com.routingengine.MethodManager;
import com.routingengine.json.JsonUtils;


public class CheckAgentMethod extends MethodManager.Method
{  
  @Override
  public JsonElement handle(JsonObject arguments)
  {
    Agent agent = getAgent(arguments);
    
    return agent.toJson();
  }
  
  public Agent getAgent(JsonObject arguments)
  {
    String agentUUIDString = JsonUtils.getAsString(arguments, "uuid");
    
    return routingEngine.getAgent(agentUUIDString);
  }
}