package pl.north93.zgame.api.standalone.commands;

import java.io.Console;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.commands.ICommandsManager;
import pl.north93.zgame.api.global.commands.NorthCommand;

public class StandaloneCommandsManager implements ICommandsManager
{
    private final Console console;

    public StandaloneCommandsManager()
    {
        this.console = System.console();
        if (this.console == null)
        {
            API.getApiCore().getLogger().warning("Console is unavailable");
            return;
        }
        this.console.writer().format(API.getApiCore().getId() + " > ");
    }

    @Override
    public void registerCommand(final NorthCommand northCommand)
    {

        System.err.println("Somebody tried to register command, but northpl is lazy and he didn't implement the Command Manager");
    }

    @Override
    public void stop()
    {

    }
}
