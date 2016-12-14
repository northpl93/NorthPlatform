package pl.north93.zgame.api.bukkitcommands;

import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectResource;

public class Heal extends NorthCommand
{
    @InjectResource(bundleName = "Commands")
    private ResourceBundle messages;

    public Heal()
    {
        super("heal");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (! sender.isPlayer())
        {
            sender.sendMessage(this.messages, "command.only_players");
            return;
        }

        final Player player;
        if (args.length() > 0)
        {
            player = Bukkit.getPlayer(args.asString(0));
            if (player == null)
            {
                sender.sendMessage(this.messages, "command.no_player");
                return;
            }
        }
        else
        {
            player = (Player) sender.unwrapped();
        }
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setSaturation(20);

        sender.sendMessage(this.messages, "command.heal.healed");
    }
}
