package pl.north93.zgame.skyblock.server.cmd;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.world.Island;
import pl.north93.zgame.skyblock.shared.api.IslandRole;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;


public class KickAllFromIslandCmd extends NorthCommand
{
    @Inject
    private INetworkManager networkManager;
    @Inject
    private SkyBlockServer server;

    public KickAllFromIslandCmd()
    {
        super("kickallfromisland", "wyrzucwszystkich", "wywalwszystkich", "wykopwszystkich");
    }

    @Override
    public void execute(NorthCommandSender sender, Arguments args, String label) {
        final Value<IOnlinePlayer> onlineSender = this.networkManager.getOnlinePlayer(sender.getName());
        final SkyPlayer skyPlayer = SkyPlayer.get(onlineSender);
        if (! skyPlayer.hasIsland())
        {
            sender.sendRawMessage("&f&l> &7Musisz miec wyspe, aby uzyc tej komendy!");
            return;
        }
        if (! skyPlayer.getIslandRole().equals(IslandRole.OWNER))
        {
            sender.sendRawMessage("&f&l> &7Musisz byc wlascicielem wyspy, aby uzyc tej komendy!");
            return;
        }

        final Player senderPlayer = (Player) sender.unwrapped();
        final Island island = this.server.getServerManager().getIslandAt(senderPlayer.getLocation());
        if(island == null || ! island.getId().equals(skyPlayer.getIslandId()))
        {
            sender.sendRawMessage("&f&l> &7Musisz znajdowac sie na swojej wyspie, aby uzyc tej komendy!");
            return;
        }

        int kicked = 0;
        for(final Player kickedPlayer : island.getPlayersInIsland())
        {
            if(kickedPlayer.hasPermission("skyblock.kick.ignore") || kickedPlayer.getUniqueId().equals(senderPlayer.getUniqueId()))
            {
                continue;
            }

            this.server.getServerManager().tpPlayerToSpawn(kickedPlayer);
            kicked++;
        }

        sender.sendRawMessage("&f&l> &7Wykopano &6" + kicked + " &7graczy z wyspy!");
    }
}
