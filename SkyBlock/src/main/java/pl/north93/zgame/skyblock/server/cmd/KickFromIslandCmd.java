package pl.north93.zgame.skyblock.server.cmd;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.server.world.Island;

public class KickFromIslandCmd extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer server;

    public KickFromIslandCmd()
    {
        super("kickfromisland", "wyrzuczwyspy", "wywal", "wykop");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() != 1)
        {
            sender.sendMessage("&cUzyj: /wykop nick");
            return;
        }

        final Player senderPlayer = (Player) sender.unwrapped();
        final Island island = this.server.getServerManager().getIslandAt(senderPlayer.getLocation());

        if (island == null || !this.server.canAccess(senderPlayer, island))
        {
            sender.sendMessage("&cMusisz znajdowac sie na swojej wyspie aby kogos wykopac!");
            return;
        }

        final IOnlinePlayer kickedPlayer = this.networkManager.getOnlinePlayer(args.asString(0)).get();
        if (kickedPlayer == null)
        {
            sender.sendMessage("&cPodany gracz nie znajduje sie na tej wyspie!");
            return;
        }

        final Player kickedBukkitPlayer = Bukkit.getPlayerExact(kickedPlayer.getNick());
        if (kickedBukkitPlayer == null || !island.getPlayersInIsland().contains(kickedBukkitPlayer))
        {
            sender.sendMessage("&cPodany gracz nie znajduje sie na tej wyspie!");
            return;
        }

        if (kickedBukkitPlayer.hasPermission("skyblock.kick.ignore"))
        {
            sender.sendMessage("&cNie mozesz wykopac tego gracza!");
            return;
        }

        sender.sendMessage("&f&l> &7Pomyslnie wykopano &6" + kickedPlayer.getNick() + " &7 z wyspy!");
        kickedPlayer.sendMessage("&f&l> &7Zostales wykopany z wyspy przez &6" + sender.getName() + "&7!");
        this.server.getServerManager().tpPlayerToSpawn(kickedBukkitPlayer);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
