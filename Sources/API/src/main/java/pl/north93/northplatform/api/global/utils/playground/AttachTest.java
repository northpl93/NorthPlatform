package pl.north93.northplatform.api.global.utils.playground;

/*import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Map;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.tools.jdi.ProcessAttachingConnector;

import org.apache.commons.lang3.StringUtils;

public class AttachTest
{
    public AttachTest()
    {
        final ProcessAttachingConnector attacher = new ProcessAttachingConnector();
        final String vmId = StringUtils.split(ManagementFactory.getRuntimeMXBean().getName(), '@')[0];

        final Map<String, Connector.Argument> args = attacher.defaultArguments();
        args.get("pid").setValue(vmId);

        System.out.println(vmId);
        System.out.println(args.get("pid"));
        System.out.println(args);

        try
        {
            final VirtualMachine attach = attacher.attach(args);
            System.out.println(attach);

            attach.suspend();
        }
        catch (IOException | IllegalConnectorArgumentsException e)
        {
            e.printStackTrace();
        }
    }
}*/
