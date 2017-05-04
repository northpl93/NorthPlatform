package pl.arieals.api.minigame.server.gamehost.arena;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.arieals.api.minigame.server.shared.api.PlayerJoinInfo;
import pl.arieals.api.minigame.shared.api.LobbyMode;
import pl.arieals.api.minigame.shared.api.MiniGame;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.impl.ArenaManager;
import pl.north93.zgame.api.global.messages.MessagesBox;

/**
 * Każda LocalArena ma swojego PlayersManagera
 */
public class PlayersManager
{
    private final GameHostManager      gameHostManager;
    private final ArenaManager         manager;
    private final LocalArena           arena;
    private final List<Player>         players; // lista połączonych już graczy
    private final List<PlayerJoinInfo> joinInfos;

    public PlayersManager(final GameHostManager gameHostManager, final ArenaManager manager, final LocalArena arena)
    {
        this.gameHostManager = gameHostManager;
        this.manager = manager;
        this.arena = arena;
        this.players = new ArrayList<>();
        this.joinInfos = Collections.synchronizedList(new ArrayList<>()); // may be accessed by server thread and rpc method executor in tryAddPlayers
    }

    public List<Player> getPlayers()
    {
        return this.players;
    }

    public boolean tryAddPlayers(final List<PlayerJoinInfo> players, final boolean spectator)
    {
        // todo implement spectating?
        synchronized (this) // handle only one join request at same time
        {
            if (this.gameHostManager.getMiniGame().getLobbyMode() == LobbyMode.INTEGRATED && ! this.arena.getWorld().isReady())
            {
                // jesli gra uzywa lobby zintegrowanego z mapa to mapa musi byc gotowa/zaladowana
                return false;
            }
            if (! this.canJoin(players))
            {
                return false;
            }
            this.joinInfos.addAll(players);

            final List<UUID> playerIds = players.stream().map(PlayerJoinInfo::getUuid).collect(Collectors.toList());
            final RemoteArena remoteArena = this.arena.getAsRemoteArena();
            remoteArena.getPlayers().addAll(playerIds);
            this.manager.setArena(remoteArena);

            return true;
        }
    }

    private boolean canJoin(final List<PlayerJoinInfo> players)
    {
        final MiniGame miniGame = this.gameHostManager.getMiniGame();
        final int normalSlots = miniGame.getSlots() - miniGame.getVipSlots();

        final int totalPlayers = this.joinInfos.size();

        final long joiningPlayers = players.size();
        final long joiningVips = players.stream().filter(PlayerJoinInfo::isVip).count();
        final long joiningNormals = joiningPlayers - joiningVips;

        return joiningPlayers + totalPlayers <= normalSlots || joiningNormals + totalPlayers <= normalSlots && joiningPlayers + totalPlayers <= miniGame.getSlots();
    }

    public void playerConnected(final Player player)
    {
        this.players.add(player);
        this.gameHostManager.getLobbyManager().addPlayer(this.arena, player);
        Bukkit.getPluginManager().callEvent(new PlayerJoinArenaEvent(player, this.arena));
    }

    public void playerDisconnected(final Player player)
    {
        this.players.remove(player);
        this.joinInfos.removeIf(pji -> pji.getUuid().equals(player.getUniqueId()));

        final RemoteArena remoteArena = this.arena.getAsRemoteArena();
        remoteArena.getPlayers().remove(player.getUniqueId());
        this.manager.setArena(remoteArena);

        this.arena.getWorld().getMapVote().removeVote(player);

        Bukkit.getPluginManager().callEvent(new PlayerQuitArenaEvent(player, this.arena));
    }

    /**
     * Sprawdza czy na arenie jest wymagana ilość graczy do wystartowania.
     *
     * @return czy jest wymagana ilość graczy do wystartowania.
     */
    public boolean isEnoughToStart()
    {
        final MiniGame miniGame = this.gameHostManager.getMiniGame();
        return this.players.size() >= miniGame.getToStart();
    }

    /**
     * Wysyła przetłumaczoną wiadomość do graczy znajdujących się na tej arenie.
     *
     * @param messagesBox obiekt przechowujący wiadomości.
     * @param messageKey klucz wiadomości.
     * @param args argumenty.
     */
    public void broadcast(final MessagesBox messagesBox, final String messageKey, final Object... args)
    {
        for (final Player player : this.players)
        {
            final String message = messagesBox.getMessage(Locale.forLanguageTag(player.spigot().getLocale()), messageKey);
            player.sendMessage(MessageFormat.format(ChatColor.translateAlternateColorCodes('&', message), args));
        }
    }
}