package com.routingengine;

import static org.junit.jupiter.api.Assertions.fail;
import static com.routingengine.json.JsonUtils.*;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeTrue;
import static org.junit.Assume.assumeFalse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import com.routingengine.client.Client;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonResponse;
import com.routingengine.server.Server;


public abstract class MethodTestBase
{
    private static final String hostname = "localhost";
    private static final int port = 50000;
    private static final int THREAD_POOL_SIZE = 100;
    protected static ExecutorService executor;
    protected static Thread serverThread;
    protected static Client client;
    protected static String method;
    
    @BeforeAll
    protected static final void setUpBeforeClass()
        throws Exception
    {
        executor = newFixedThreadPool(THREAD_POOL_SIZE);
        
        serverThread = new Thread(new Server(hostname, port));
        serverThread.start();
        
        TimeUnit.SECONDS.sleep(2);
        
        client = new Client(hostname, port); 
    }
    
    @AfterAll
    protected static final void tearDownAfterClass()
        throws Exception
    {
        executor.shutdownNow();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException, EndConnectionException
            {
                exit();
            }
        });
        
        serverThread.interrupt();
        serverThread.join();
    }
    
    protected static final void execute(ClientConnectionHandler connectionHandler)
        throws IOException
    {
        client.setConnectionHandler(connectionHandler);
        
        client.run();
    }
    
    private static final void executeInNewClient(ClientConnectionHandler connectionHandler)
        throws IOException
    {
        Client client = new Client(hostname, port);
        
        client.setConnectionHandler(new ClientConnectionHandler ()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException, EndConnectionException
            {
                connectionHandler.connect(this.socket);
                connectionHandler.runMainLoop();
                exit();
            }
        });
        
        executor.execute(client);
    }
    
    protected static final void assumeResponseDidSucceed(JsonResponse response)
    {
        assumeTrue(castToString(response.getPayload()), response.didSucceed());
    }
    
    protected static final void assertResponseDidSucceed(JsonResponse response)
    {
        if (!response.didSucceed())
            fail(castToString(response.getPayload()));
    }
    
    protected static final String generateNewSupportRequest(String name, String email, int type)
        throws IOException, InterruptedException, ExecutionException
    {
        String[] supportRequestUUIDString = new String[] {null};
        
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = newSupportRequest(name, email, type);
                
                assumeResponseDidSucceed(response);
                
                SupportRequest supportRequest = SupportRequest.fromJson(castToJsonObject(response.getPayload()));
                
                supportRequestUUIDString[0] = supportRequest.getUUID().toString();
            }
        });
        
        assumeNotNull(supportRequestUUIDString[0]);
        
        return supportRequestUUIDString[0];
    }
    
    protected static final String generateNewSupportRequest(String name, String email, String type)
        throws IOException, InterruptedException, ExecutionException
    {
        String[] supportRequestUUIDString = new String[] {null};
        
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = newSupportRequest(name, email, type);
                
                assumeResponseDidSucceed(response);
                
                SupportRequest supportRequest = SupportRequest.fromJson(castToJsonObject(response.getPayload()));
                
                supportRequestUUIDString[0] = supportRequest.getUUID().toString();
            }
        });
        
        assumeNotNull(supportRequestUUIDString[0]);
        
        return supportRequestUUIDString[0];
    }
    
    protected static final String generateNewAgent(@SuppressWarnings("rawtypes") Map skills)
        throws IOException, InterruptedException, ExecutionException
    {
        String[] agentUUIDString = new String[] {null};
        
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = newAgent(skills);
                
                assumeResponseDidSucceed(response);
                
                Agent agent = Agent.fromJson(castToJsonObject(response.getPayload()));
                
                agentUUIDString[0] = agent.getUUID().toString();
            }
        });
        
        assumeNotNull(agentUUIDString[0]);
        
        return agentUUIDString[0];
    }
    
    protected static final void customerWaitsForAgent(String supportRequestUUIDString)
        throws IOException, InterruptedException, ExecutionException
    {
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = checkSupportRequest(supportRequestUUIDString);
                
                assumeResponseDidSucceed(response);
                
                SupportRequest supportRequest = SupportRequest.fromJson(castToJsonObject(response.getPayload()));
                
                assumeFalse("support request already has assigned agent", supportRequest.hasAssignedAgent());
                
                assumeTrue("support request is closed", supportRequest.isOpen());
                
                assumeFalse("support request already waiting", supportRequest.isWaiting());
            }
        });
        
        executeInNewClient(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                waitForAgent(supportRequestUUIDString);
            }
        });
    }
    
    protected static final void agentUpdatesAvailability(String agentUUIDString, Boolean isAvailable)
        throws IOException, InterruptedException, ExecutionException
    {
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = updateAgentAvailability(agentUUIDString, isAvailable);
                
                assumeResponseDidSucceed(response);
                
                Agent agent = Agent.fromJson(castToJsonObject(response.getPayload()));
                
                assumeTrue(isAvailable.equals(agent.isAvailable()));
            }
        });
    }
    
    protected static final void agentTakesSupportRequest(String agentUUIDString)
        throws IOException, InterruptedException, ExecutionException
    {
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = checkAgent(agentUUIDString);
                
                assumeResponseDidSucceed(response);
                
                Agent agent = Agent.fromJson(castToJsonObject(response.getPayload()));
                
                assumeFalse("agent already has assigned support request", agent.hasAssignedSupportRequest());
                
                assumeTrue("agent not activated", agent.isActivated());
                
                assumeFalse("agent already waiting", agent.isWaiting());
                
                assumeTrue("agent not available", agent.isAvailable());
            }
        });
        
        executeInNewClient(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                takeSupportRequest(agentUUIDString);
            }
        });
    }
    
    protected static final boolean agentDidTakeSupportRequest(String agentUUIDString, String supportRequestUUIDString)
        throws IOException, InterruptedException, ExecutionException
    {
        boolean[] didTake = new boolean[] {false};
        
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = checkAgent(agentUUIDString);
                
                assumeResponseDidSucceed(response);
                
                Agent agent = Agent.fromJson(castToJsonObject(response.getPayload()));
                
                assumeTrue("agent not activated", agent.isActivated());
                
                assumeFalse("agent is waiting", agent.isWaiting());
                
                assumeFalse("agent has not been assigned a support request", agent.isAvailable());
                
                assumeTrue("agent is not available", agent.hasAssignedSupportRequest());
                
                response = checkSupportRequest(supportRequestUUIDString);
                
                assumeResponseDidSucceed(response);
                
                SupportRequest supportRequest = SupportRequest.fromJson(castToJsonObject(response.getPayload()));
                
                assumeTrue("support request is closed", supportRequest.isOpen());
                
                assumeFalse("support request is waiting", supportRequest.isWaiting());
                
                assumeTrue("support request has not been assigned an agent", supportRequest.hasAssignedAgent());
                
                String assignedSupportRequestUUIDString = agent.getAssignedSupportRequest().getUUID().toString();
                
                didTake[0] = (supportRequestUUIDString.equals(assignedSupportRequestUUIDString));
            }
        });
        
        return didTake[0];
    }
    
    protected static final void removeSupportRequest(String supportRequestUUIDString)
        throws IOException, InterruptedException, ExecutionException
    {
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = removeSupportRequest(supportRequestUUIDString);
                
                assumeResponseDidSucceed(response);
            }
        });
    }
    
    protected static final void removeAgent(String agentUUIDString)
        throws IOException, InterruptedException, ExecutionException
    {
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = removeAgent(agentUUIDString);
                
                assertResponseDidSucceed(response);
            }
        });
    }
}
