package pl.north93.zgame.skyplayerexp.server.compass;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.skyplayerexp.server.ExperienceServer;

public class CompassCmd extends NorthCommand
{
    @Inject
    private BukkitApiCore    apiCore;
    @Inject
    private ExperienceServer experience;

    public CompassCmd()
    {
        super("kompas", "k");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        if (args.length() == 0)
        {
            this.experience.getServerGuiManager().openServerMenu(player);
        }
        else if (args.length() == 1)
        {
            final String arg = args.asString(0);
            final ICompassManager compassManager = this.experience.getCompassManager();
            if (arg.equalsIgnoreCase("wlacz") || arg.equalsIgnoreCase("on"))
            {
                if (! player.getGameMode().equals(GameMode.SURVIVAL))
                {
                    sender.sendMessage("&f&l> &7Kompas mozna wlaczyc tylko w trybie survival.");
                    return;
                }
                compassManager.getCompassConnector().switchCompassStateInConfig(player.getName(), true);
                sender.sendMessage("&f&l> &6Wlaczono &7kompas, aby go wylaczyc wpisz &6/kompas wylacz");
                if (compassManager.getCompassConnector().isLobby())
                {
                    compassManager.switchCompassState(player, true);
                }
                else
                {
                    sender.sendMessage("&f&l> &7Pamietaj, ze kompas na pasku przedmiotow dziala tylko na lobby!");
                }
            }
            else if (arg.equalsIgnoreCase("wylacz") || arg.equalsIgnoreCase("off"))
            {
                compassManager.getCompassConnector().switchCompassStateInConfig(player.getName(), false);
                compassManager.switchCompassState(player, false);
                sender.sendMessage("&f&l> &6Wylaczono &7kompas, aby go wlaczyc wpisz &6/kompas wlacz");
            }
            else
            {
                sender.sendMessage("&f&l> &7Poprawne argumenty komendy to: &6wlacz &7lub &6wylacz");
            }
        }
        else
        {
            sender.sendMessage("&f&l> &7Poprawne argumenty komendy to: &6wlacz &7lub &6wylacz");
        }
    }
}
