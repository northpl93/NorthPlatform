package pl.north93.zgame.api.defaultcommands;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

public class ListComponents extends NorthCommand
{
    public ListComponents()
    {
        super("listcomponents", "components");
        this.setPermission("api.command.listcomponents");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        sender.sendMessage("&aAktualnie załadowane moduły API:");
    }
}
