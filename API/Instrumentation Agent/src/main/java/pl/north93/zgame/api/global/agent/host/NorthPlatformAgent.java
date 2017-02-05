package pl.north93.zgame.api.global.agent.host;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;

import sun.management.Agent;

public class NorthPlatformAgent extends Agent
{
    public static void agentmain(final String args, final Instrumentation instrument) throws Exception
    {
        final AgentService ts = new AgentService(instrument);
        final ObjectName on = new ObjectName("pl.north93:service=NorthTransformer");

        final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        server.registerMBean(ts, on);
        log("NorthPlatformAgent installed successfully");
    }

    private static void log(final Object object)
    {
        System.out.println(object);
    }
}
