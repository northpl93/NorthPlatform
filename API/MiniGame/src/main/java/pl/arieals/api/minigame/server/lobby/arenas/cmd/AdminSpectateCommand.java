package pl.arieals.api.minigame.server.lobby.arenas.cmd;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.lobby.arenas.IArenaClient;
import pl.arieals.api.minigame.shared.api.PlayerJoinInfo;
import pl.arieals.api.minigame.shared.api.arena.IArena;
import pl.arieals.api.minigame.shared.api.status.IPlayerStatus;
import pl.arieals.api.minigame.shared.api.status.IPlayerStatusManager;
import pl.arieals.api.minigame.shared.api.status.InGameStatus;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.network.players.PlayerNotFoundException;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.joinaction.IServerJoinAction;
import pl.north93.zgame.api.global.redis.observable.Value;

public class AdminSpectateCommand extends NorthCommand
{
    @Inject
    private INetworkManager      networkManager;
    @Inject
    private IPlayerStatusManager statusManager;
    @Inject
    private IArenaClient         arenaClient;

    public AdminSpectateCommand()
    {
        super("adminspectate", "adminspec");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        if (args.length() == 1)
        {
            final Identity identity = Identity.create(null, args.asString(0));

            final IPlayerStatus status;
            try
            {
                status = this.statusManager.getPlayerStatus(identity);
            }
            catch (final PlayerNotFoundException exception)
            {
                sender.sendMessage("&cPodany gracz nie istnieje");
                return;
            }

            if (status.getType() != IPlayerStatus.LocationType.GAME)
            {
                sender.sendMessage("&cPodany gracz nie jest w grze lub jest offline");
                return;
            }

            final InGameStatus gameStatus = (InGameStatus) status;

            final IArena arena = this.arenaClient.get(gameStatus.getArenaId());
            final PlayerJoinInfo joinInfo = new PlayerJoinInfo(player.getUniqueId(), false, true);

            sender.sendMessage("&aPrzenoszenie do gry gracza {0}", identity.getNick());
            sender.sendMessage("&aID serwera: {0}", gameStatus.getServerId());
            sender.sendMessage("&aID areny: {0}", gameStatus.getArenaId());

            this.arenaClient.spectate(arena, joinInfo);
            this.teleportAdminToPlayer(player, arena, identity.getNick());
        }
        else
        {
            sender.sendMessage("&c/adminspec <nick>");
        }
    }

    private void teleportAdminToPlayer(final Player admin, final IArena arena, final String target)
    {
        final Value<IOnlinePlayer> value = this.networkManager.getPlayers().unsafe().getOnline(admin.getName());
        if (! value.isPreset())
        {
            return;
        }

        final Server server = this.networkManager.getServers().withUuid(arena.getServerId());
        value.get().connectTo(server, new TeleportAdminToPlayer(target));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

class TeleportAdminToPlayer implements IServerJoinAction
{
    private String playerName;

    public TeleportAdminToPlayer()
    {
    }

    public TeleportAdminToPlayer(final String playerName)
    {
        this.playerName = playerName;
    }

    @Override
    public void playerJoined(final INorthPlayer player)
    {
        player.setGameMode(GameMode.CREATIVE);
        player.setCollidable(false);

        final Player target = Bukkit.getPlayer(this.playerName);
        if (target != null)
        {
            player.teleport(target, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("playerName", this.playerName).toString();
    }
}