package pl.north93.zgame.daemon.servers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class LocalServerConsole
{
    @Inject
    private static Logger logger;
    private final LocalServerInstance instance;
    private final Process process;

    private LocalServerConsole(final LocalServerInstance instance, final Process process)
    {
        this.instance = instance;
        this.process = process;
    }

    public LocalServerInstance getInstance()
    {
        return this.instance;
    }

    public Process getProcess()
    {
        return this.process;
    }

    public void executeCommand(final String consoleCommand)
    {
        try
        {
            final OutputStream outputStream = this.process.getOutputStream();
            final String line = consoleCommand + '\n';
            outputStream.write(line.getBytes(StandardCharsets.UTF_8));
        }
        catch (final IOException e)
        {
            logger.log(Level.SEVERE, "Failed to execute server command: " + consoleCommand, e);
        }
    }

    public static LocalServerConsole createProcess(final LocalServerInstance instance)
    {
        final ProcessBuilder processBuilder = new ProcessBuilder();

        processBuilder.directory(instance.getWorkspace());
        processBuilder.command(StringUtils.split(instance.getArguments().buildStartLine(), ' '));
        processBuilder.inheritIO(); // testy

        final Process process;
        try
        {
            process = processBuilder.start();
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Failed to create process for " + instance.getServerId(), e);
        }

        return new LocalServerConsole(instance, process);
    }
}
