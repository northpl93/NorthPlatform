package pl.north93.zgame.features.bukkit.general;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class HealCommand extends NorthCommand
{
    @Inject @Messages("BaseFeatures")
    private MessagesBox messages;

    public HealCommand()
    {
        super("heal");
        this.setPermission("api.command.heal");
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
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExhaustion(0);

        sender.sendMessage(this.messages, "command.heal.healed");
    }
}
