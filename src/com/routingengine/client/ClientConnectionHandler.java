package com.routingengine.client;

import java.io.IOException;
import java.util.Map;
import com.routingengine.json.JsonProtocolException;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;
import com.routingengine.json.JsonUtils;


public abstract class ClientConnectionHandler extends ConnectionHandler
{
  protected final JsonResponse ping()
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("ping")
      .publish(jsonWriter);
    
    return awaitResponse();
  }
  
  protected final JsonResponse newSupportRequest(String name, String email, int requestTypeIndex)
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("new_support_request")
      .setArgument("name", name)
      .setArgument("email", email)
      .setArgument("type", requestTypeIndex)
      .publish(jsonWriter);
    
    return awaitResponse();
  }
  
  protected final JsonResponse newSupportRequest(String name, String email, String requestTypeString)
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("new_support_request")
      .setArgument("name", name)
      .setArgument("email", email)
      .setArgument("type", requestTypeString)
      .publish(jsonWriter);
  
    return awaitResponse();
  }
  
  protected final JsonResponse newSupportRequest(String name, String email, int requestTypeIndex, String address)
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("new_support_request")
      .setArgument("name", name)
      .setArgument("email", email)
      .setArgument("type", requestTypeIndex)
      .setArgument("address", address)
      .publish(jsonWriter);
    
    return awaitResponse();
  }
  
  protected final JsonResponse newSupportRequest(String name, String email, String requestTypeString, String address)
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("new_support_request")
      .setArgument("name", name)
      .setArgument("email", email)
      .setArgument("type", requestTypeString)
      .setArgument("address", address)
      .publish(jsonWriter);
  
    return awaitResponse();
  }
  
  protected final JsonResponse checkSupportRequest(String supportRequestUUIDString)
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("check_support_request")
      .setArgument("uuid", supportRequestUUIDString)
      .publish(jsonWriter);
  
    return awaitResponse();
  }
  
  protected final JsonResponse changeSupportRequestType(String supportRequestUUIDString, int requestTypeIndex)
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("change_support_request_type")
      .setArgument("uuid", supportRequestUUIDString)
      .setArgument("type", requestTypeIndex)
      .publish(jsonWriter);
  
    return awaitResponse();
  }
  
  protected final JsonResponse changeSupportRequestType(String supportRequestUUIDString, String requestTypeString)
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("change_support_request_type")
      .setArgument("uuid", supportRequestUUIDString)
      .setArgument("type", requestTypeString)
      .publish(jsonWriter);
  
    return awaitResponse();
  }
  
  protected final JsonResponse waitForAgent(String supportRequestUUIDString)
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("wait_for_agent")
      .setArgument("uuid", supportRequestUUIDString)
      .publish(jsonWriter);
  
    return awaitResponse();
  }
  
  protected final JsonResponse closeSupportRequest(String supportRequestUUIDString)
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("close_support_request")
      .setArgument("uuid", supportRequestUUIDString)
      .publish(jsonWriter);
  
    return awaitResponse();
  }
  
  protected final JsonResponse removeSupportRequest(String supportRequestUUIDString)
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("remove_support_request")
      .setArgument("uuid", supportRequestUUIDString)
      .publish(jsonWriter);
  
    return awaitResponse();
  }
  
  protected final JsonResponse newAgent(@SuppressWarnings("rawtypes") Map skills)
    throws IOException, InterruptedException
  {
    new JsonRequest()
    .setMethod("new_agent")
    .setArgument("skills", JsonUtils.toJsonElement(skills))
    .publish(jsonWriter);

  return awaitResponse();
  }
  
  protected final JsonResponse newAgent(@SuppressWarnings("rawtypes") Map skills, String address)
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("new_agent")
      .setArgument("skills", skills)
      .setArgument("address", address)
      .publish(jsonWriter);

    return awaitResponse();
  }
  
  protected final JsonResponse checkAgent(String agentUUIDString)
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("check_agent")
      .setArgument("uuid", agentUUIDString)
      .publish(jsonWriter);
  
    return awaitResponse();
  }
  
  protected final JsonResponse updateAgentSkills(String agentUUIDString, @SuppressWarnings("rawtypes") Map skills)
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("update_agent_skills")
      .setArgument("uuid", agentUUIDString)
      .setArgument("skills", skills)
      .publish(jsonWriter);
  
    return awaitResponse();
  }
  
  protected final JsonResponse updateAgentAvailability(String agentUUIDString, Boolean isAvailable)
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("update_agent_availability")
      .setArgument("uuid", agentUUIDString)
      .setArgument("available", isAvailable)
      .publish(jsonWriter);
  
    return awaitResponse();
  }
  
  protected final JsonResponse takeSupportRequest(String agentUUIDString)
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("take_support_request")
      .setArgument("uuid", agentUUIDString)
      .publish(jsonWriter);
  
    return awaitResponse();
  }
  
  protected final JsonResponse dropSupportRequest(String agentUUIDString)
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("drop_support_request")
      .setArgument("uuid", agentUUIDString)
      .publish(jsonWriter);
  
    return awaitResponse();
  }
  
  protected final JsonResponse removeAgent(String agentUUIDString)
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("remove_agent")
      .setArgument("uuid", agentUUIDString)
      .publish(jsonWriter);
  
    return awaitResponse();
  }
  
  protected final JsonResponse getStatusOverview()
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("get_status_overview")
      .publish(jsonWriter);
  
    return awaitResponse();
  }
  
  protected final JsonResponse getAgentStatus()
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("get_agent_status")
      .publish(jsonWriter);
  
    return awaitResponse();
  }
  
  protected final JsonResponse getSupportRequestStatus()
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("get_support_request_status")
      .publish(jsonWriter);
  
    return awaitResponse();
  }
  
  protected final JsonResponse getQueueStatus()
    throws IOException, InterruptedException
  {
    new JsonRequest()
      .setMethod("get_queue_status")
      .publish(jsonWriter);
    
    return awaitResponse();
  }
  
  private final JsonResponse awaitResponse()
    throws IOException, InterruptedException
  {
    waitForInput();
    
    try {
      return JsonResponse.fromReader(jsonReader);
    }
    
    catch (JsonProtocolException exception) {
      return null;
    }
  }
}