package pl.north93.zgame.skyblock.server.cmd.admin;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.management.IslandHostManager;
import pl.north93.zgame.skyblock.server.world.Island;
import pl.north93.zgame.skyblock.server.world.points.PointsHelper;

public class SkyPoints extends NorthCommand
{
    @Inject
    private BukkitApiCore  apiCore;
    @Inject
    private SkyBlockServer server;

    public SkyPoints()
    {
        super("skypoints");
        this.setPermission("skyblock.admin");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final PointsHelper pointsHelper = this.server.<IslandHostManager>getServerManager().getPointsHelper();

        if (args.length() == 0)
        {
            sender.sendMessage("&c/skypoints recalculate - przelicza punkty wyspy, na ktorej sie znajdujesz");
            sender.sendMessage("&c/skypoints recalculateall - przelicza punkty wszystkich wysp na serwerze");
            sender.sendMessage("&c/skypoints persist - zapisuje punkty wszystkich wysp do bazy danych");
        }
        else if (args.length() == 1)
        {
            final String arg0 = args.asString(0);
            if (arg0.equalsIgnoreCase("recalculate"))
            {
                final Island island = this.server.getServerManager().getIslandAt(((Player) sender.unwrapped()).getLocation());
                if(island != null)
                {
                    sender.sendMessage("&aWyspa zostanie przeliczona.");
                    island.getPoints().recalculate();
                }
                else
                {
                    sender.sendMessage("&cNie znaleziono wyspy w tym miejscu!");
                }
            }
            else if (arg0.equalsIgnoreCase("recalculateall"))
            {
                pointsHelper.recalculateAll();
                sender.sendMessage("&cRozpoczeto przeliczanie punktow na tym serwerze...");
            }
            else if (arg0.equalsIgnoreCase("persist"))
            {
                this.apiCore.getPlatformConnector().runTaskAsynchronously(pointsHelper::persistAll);
                sender.sendMessage("&cRozpoczeto zapisywanie danych wszystkich wysp...");
            }
            else
            {
                sender.sendMessage("&cNiepoprawne argumenty");
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
