package pl.north93.zgame.features.bukkit.server;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

public class DangerousCommand extends NorthCommand
{
    public DangerousCommand()
    {
        super("dangerous", "stop", "reload", "rl");
        this.setPermission("basefeatures.cmd.dangerous");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        sender.sendMessage("&6Niebezpieczne zablokowane komendy: " + String.join(", ", this.getAliases()) + ".");
    }
}
