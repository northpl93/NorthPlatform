package pl.north93.zgame.api.global.agent.client;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import java.lang.management.ManagementFactory;

public class LocalAgentClient implements IAgentClient
{
    private static final String[] METHOD_DEF = new String[] { String.class.getName(), byte[].class.getName() };
    private ObjectName agentServiceName;

    @Override
    public void connect()
    {
        try
        {
            this.agentServiceName = new ObjectName("pl.north93:service=NorthTransformer");
        }
        catch (final MalformedObjectNameException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void redefineClass(final String className, final byte[] newBytes)
    {
        try
        {
            final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            mBeanServer.invoke(this.agentServiceName, "transformClass", new Object[] { className, newBytes }, METHOD_DEF);
        }
        catch (final InstanceNotFoundException | MBeanException | ReflectionException e)
        {
            e.printStackTrace();
        }
    }
}
