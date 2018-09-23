package pl.north93.zgame.api.standalone.commands;

import java.io.Console;

import lombok.extern.slf4j.Slf4j;
import pl.north93.zgame.api.global.commands.ICommandsManager;
import pl.north93.zgame.api.global.commands.NorthCommand;

@Slf4j
public class StandaloneCommandsManager implements ICommandsManager
{
    private final Console console;

    public StandaloneCommandsManager()
    {
        this.console = System.console();
        if (this.console == null)
        {
            log.warn("Console is unavailable");
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
        log.warn("Somebody tried to register command, but northpl is lazy and he didn't implement the Command Manager");
    }

    @Override
    public void stop()
    {

    }
}
