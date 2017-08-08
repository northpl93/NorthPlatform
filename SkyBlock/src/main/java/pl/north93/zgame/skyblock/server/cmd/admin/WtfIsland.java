package pl.north93.zgame.skyblock.server.cmd.admin;

import java.util.UUID;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.management.IslandHostManager;
import pl.north93.zgame.skyblock.server.world.Island;
import pl.north93.zgame.skyblock.shared.api.IslandData;
import pl.north93.zgame.skyblock.shared.api.ServerMode;
import pl.north93.zgame.skyblock.shared.api.cfg.IslandConfig;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;

public class WtfIsland extends NorthCommand
{
    @Inject
    private INetworkManager networkManager;
    @Inject
    private SkyBlockServer  server;

    public WtfIsland()
    {
        super("wtfisland", "wtfmyisland");
        this.setPermission("skyblock.admin");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final String target = args.length() == 0 ? sender.getName() : args.asString(0);
        final IslandData island;
        if (target.equals("$here"))
        {
            if (this.server.getServerMode() != ServerMode.ISLAND_HOST)
            {
                sender.sendRawMessage("&c$here mozesz uzywac tylko na serwerze z wyspami");
                return;
            }

            final IslandHostManager manager = this.server.getServerManager();
            final Player player = (Player) sender.unwrapped();
            final Island byChunk = manager.getWorldManager(player.getWorld()).getIslands().getByChunk(player.getLocation().getChunk());
            if (byChunk == null)
            {
                sender.sendRawMessage("&cNa tym chunku nie ma zadnej wyspy!");
                return;
            }
            island = this.server.getIslandDao().getIsland(byChunk.getId());
        }
        else
        {
            final SkyPlayer skyPlayer;
            final Value<IOnlinePlayer> onlinePlayer = this.networkManager.getPlayers().unsafe().getOnline(target);
            if (onlinePlayer.isAvailable())
            {
                skyPlayer = SkyPlayer.get(onlinePlayer);
            }
            else
            {
                skyPlayer = SkyPlayer.get(this.networkManager.getPlayers().unsafe().getOffline(target));
            }

            if (skyPlayer.hasIsland())
            {
                island = this.server.getIslandDao().getIsland(skyPlayer.getIslandId());
            }
            else
            {
                sender.sendRawMessage("&c" + target + " nie ma wyspy.");
                return;
            }
        }

        sender.sendRawMessage("&eIsland ID: " + island.getIslandId());
        sender.sendRawMessage("&eServer: " + island.getServerId());
        sender.sendRawMessage("&eOwner: " + island.getOwnerId() + " " + this.networkManager.getPlayers().getNickFromUuid(island.getOwnerId()));
        sender.sendRawMessage("&eAccepting visits: " + (island.getAcceptingVisits() ? "&atrue" : "&cfalse"));
        final IslandConfig islandType = this.server.getSkyBlockConfig().getIslandType(island.getIslandType());
        sender.sendRawMessage("&eType: " + islandType.getName() + " (r= " + islandType.getRadius() + ")");
        sender.sendRawMessage("&eName: " + island.getName());
        sender.sendRawMessage("&ePoints: " + island.getPoints());
        sender.sendRawMessage("&eIs shown in ranking: " + (island.getShowInRanking() ? "&atrue" : "&cfalse"));
        sender.sendRawMessage("&eInvites:");
        for (final UUID invite : island.getInvitations())
        {
            sender.sendRawMessage("&e * " + invite + " " + this.networkManager.getPlayers().getNickFromUuid(invite));
        }
        sender.sendRawMessage("&eMembers:");
        for (final UUID member : island.getMembersUuid())
        {
            sender.sendRawMessage("&e * " + member + " " + this.networkManager.getPlayers().getNickFromUuid(member));
        }
    }
}
