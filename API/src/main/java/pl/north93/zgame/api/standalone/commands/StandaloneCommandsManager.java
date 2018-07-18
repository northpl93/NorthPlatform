package pl.north93.zgame.api.standalone.commands;

import java.io.Console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.north93.zgame.api.global.commands.ICommandsManager;
import pl.north93.zgame.api.global.commands.NorthCommand;

public class StandaloneCommandsManager implements ICommandsManager
{
    private final Logger  logger = LoggerFactory.getLogger(StandaloneCommandsManager.class);
    private final Console console;

    public StandaloneCommandsManager()
    {
        this.console = System.console();
        if (this.console == null)
        {
            this.logger.warn("Console is unavailable");
            return;
        }
        /*new Thread(() ->
        {
            while (true)
            {
                final String line = this.console.readLine(API.getApiCore().getId() + " > ");
                System.out.println(line);
            }
        }).start();*/ // todo implement it
    }

    @Override
    public void registerCommand(final NorthCommand northCommand)
    {

        this.logger.warn("Somebody tried to register command, but northpl is lazy and he didn't implement the Command Manager");
    }

    @Override
    public void stop()
    {

    }
}
