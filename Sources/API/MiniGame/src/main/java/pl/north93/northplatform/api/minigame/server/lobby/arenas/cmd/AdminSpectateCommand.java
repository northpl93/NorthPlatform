package pl.north93.northplatform.api.minigame.server.lobby.arenas.cmd;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.global.network.players.PlayerNotFoundException;
import pl.north93.northplatform.api.global.network.server.IServersManager;
import pl.north93.northplatform.api.global.network.server.Server;
import pl.north93.northplatform.api.global.network.server.joinaction.IServerJoinAction;
import pl.north93.northplatform.api.minigame.server.lobby.arenas.IArenaClient;
import pl.north93.northplatform.api.minigame.shared.api.PlayerJoinInfo;
import pl.north93.northplatform.api.minigame.shared.api.arena.IArena;
import pl.north93.northplatform.api.minigame.shared.api.status.IPlayerStatus;
import pl.north93.northplatform.api.minigame.shared.api.status.IPlayerStatusManager;
import pl.north93.northplatform.api.minigame.shared.api.status.InGameStatus;

public class AdminSpectateCommand extends NorthCommand
{
    @Inject
    private IArenaClient arenaClient;
    @Inject
    private IPlayersManager playersManager;
    @Inject
    private IServersManager serversManager;
    @Inject
    private IPlayerStatusManager statusManager;

    public AdminSpectateCommand()
    {
        super("adminspectate", "adminspec");
        this.setPermission("minigameapi.cmd.adminspectate");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        if (args.length() != 1)
        {
            sender.sendMessage("&c/adminspec nick");
            return;
        }

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

        if (status.getType() != IPlayerStatus.StatusType.GAME)
        {
            sender.sendMessage("&cPodany gracz nie jest w grze lub jest offline");
            return;
        }

        final InGameStatus gameStatus = (InGameStatus) status;

        final IArena arena = this.arenaClient.get(gameStatus.getArenaId());
        final PlayerJoinInfo joinInfo = new PlayerJoinInfo(player.getUniqueId(), true, true);

        sender.sendMessage("&aPrzenoszenie do gry gracza {0}", identity.getNick());
        sender.sendMessage("&aID serwera: {0}", gameStatus.getServerId());
        sender.sendMessage("&aID areny: {0}", gameStatus.getArenaId());

        this.arenaClient.spectate(arena, joinInfo);
        this.teleportAdminToPlayer(player, arena, identity.getNick());
    }

    private void teleportAdminToPlayer(final Player admin, final IArena arena, final String target)
    {
        this.playersManager.ifOnline(admin.getName(), player ->
        {
            final Server server = this.serversManager.withUuid(arena.getServerId());
            player.connectTo(server, new TeleportAdminToPlayer(target));
        });
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
    public void playerPreSpawn(final INorthPlayer player, final Location spawn)
    {
        final INorthPlayer target = INorthPlayer.getExact(this.playerName);
        if (target != null)
        {
            // ustawiamy poprawna lokacje przed spawnem, aby przyspieszyc teleportacje
            // (unikamy ewentualnej dwukrotnej zmiany swaiata u klienta)
            target.getLocation(spawn);
        }
    }

    @Override
    public void playerJoined(final INorthPlayer player)
    {
        player.setGameMode(GameMode.CREATIVE);
        player.setCollidable(false);

        final INorthPlayer target = INorthPlayer.getExact(this.playerName);
        if (target != null)
        {
            // dla formalnosci wykonujemy teleportacje, aby wykonal sie event
            player.teleport(target, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("playerName", this.playerName).toString();
    }
}