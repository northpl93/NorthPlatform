package pl.north93.zgame.api.standalone.commands;

import pl.north93.zgame.api.global.commands.ICommandsManager;
import pl.north93.zgame.api.global.commands.NorthCommand;

public class StandaloneCommandsManager implements ICommandsManager
{
    @Override
    public void registerCommand(final NorthCommand northCommand)
    {
        System.err.println("Somebody tried to register command, but northpl is lazy and he didn't implement the Command Manager");
    }
}
