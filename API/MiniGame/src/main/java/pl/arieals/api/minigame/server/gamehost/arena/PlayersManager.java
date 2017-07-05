package pl.arieals.api.minigame.server.gamehost.arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.arieals.api.minigame.server.shared.api.PlayerJoinInfo;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.cfg.MiniGameConfig;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.impl.ArenaManager;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

/**
 * Każda LocalArena ma swojego PlayersManagera
 */
public class PlayersManager
{
    @Inject @Messages("MiniGameApi")
    private MessagesBox    messages;
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

    /**
     * @return gracze aktualnie znajdujący się na arenie.
     */
    public List<Player> getPlayers()
    {
        return this.players;
    }

    /**
     * @return maksymalna ilość graczy na arenie.
     */
    public int getMaxPlayers()
    {
        return this.gameHostManager.getMiniGameConfig().getSlots();
    }
    
    public int getMinPlayers()
    {
        return gameHostManager.getMiniGameConfig().getToStart();
    }
    
    public boolean tryAddPlayers(final List<PlayerJoinInfo> players, final boolean spectator)
    {
        // todo implement spectating?
        synchronized (this) // handle only one join request at same time
        {
            if (this.arena.getGamePhase() == GamePhase.INITIALISING)
            {
                // jesli gra uzywa lobby zintegrowanego z mapa to mapa musi byc gotowa/zaladowana
                return false;
            }
            if (!this.gameHostManager.getMiniGameConfig().isDynamic() && this.arena.getGamePhase() != GamePhase.LOBBY)
            {
                // jesli gra nie jest dynamiczna to wchodzic na nia mozna tylko podczas LOBBY
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
        final MiniGameConfig miniGame = this.gameHostManager.getMiniGameConfig();
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

        final PlayerJoinArenaEvent event = new PlayerJoinArenaEvent(player, this.arena, "player.joined_arena");
        Bukkit.getPluginManager().callEvent(event);
        
        if ( event.getJoinMessage() != null )
        {
            this.announceJoinLeft(player, event.getJoinMessage());
        }
    }

    public void playerDisconnected(final Player player)
    {
        this.players.remove(player);
        this.joinInfos.removeIf(pji -> pji.getUuid().equals(player.getUniqueId()));

        final RemoteArena remoteArena = this.arena.getAsRemoteArena();
        remoteArena.getPlayers().remove(player.getUniqueId());
        this.manager.setArena(remoteArena);

        final MapVote mapVote = this.arena.getMapVote();
        if (mapVote != null)
        {
            mapVote.removeVote(player);
        }

        final PlayerQuitArenaEvent event = new PlayerQuitArenaEvent(player, this.arena, "player.quit_arena");
        Bukkit.getPluginManager().callEvent(event);
        
        if ( event.getQuitMessage() != null )
        {
            this.announceJoinLeft(player, event.getQuitMessage());
        }
    }

    /**
     * Sprawdza czy na arenie jest wymagana ilość graczy do wystartowania.
     *
     * @return czy jest wymagana ilość graczy do wystartowania.
     */
    public boolean isEnoughToStart()
    {
        final MiniGameConfig miniGame = this.gameHostManager.getMiniGameConfig();
        return this.players.size() >= miniGame.getToStart();
    }

    /**
     * Wysyła przetłumaczoną wiadomość do graczy znajdujących się na tej arenie.
     * Dodatkowo uwzględnia warunek wysłania wiadomości.
     *
     * @param condition warunek który musi spełnić gracz aby otrzymać wiadomość.
     * @param messagesBox obiekt przechowujący wiadomości.
     * @param messageKey klucz wiadomości.
     * @param layout Wyglad tej wiadomości.
     * @param args argumenty.
     */
    public void broadcast(final Predicate<Player> condition, final MessagesBox messagesBox, final String messageKey, final MessageLayout layout, final Object... args)
    {
        for (final Player player : this.players)
        {
            if (! condition.test(player))
            {
                continue;
            }
            messagesBox.sendMessage(player, messageKey, layout, args);
        }
    }

    /**
     * Wysyła przetłumaczoną wiadomość do graczy znajdujących się na tej arenie
     * z domyślnym layoutem.
     * Dodatkowo uwzględnia warunek wysłania wiadomości.
     *
     * @param condition warunek który musi spełnić gracz aby otrzymać wiadomość.
     * @param messagesBox obiekt przechowujący wiadomości.
     * @param messageKey klucz wiadomości.
     * @param args argumenty.
     */
    public void broadcast(final Predicate<Player> condition, final MessagesBox messagesBox, final String messageKey, final Object... args)
    {
        this.broadcast(condition, messagesBox, messageKey, MessageLayout.DEFAULT, args);
    }

    /**
     * Wysyła przetłumaczoną wiadomość do graczy znajdujących się na tej arenie.
     *
     * @param messagesBox obiekt przechowujący wiadomości.
     * @param messageKey klucz wiadomości.
     * @param layout Wyglad tej wiadomości.
     * @param args argumenty.
     */
    public void broadcast(final MessagesBox messagesBox, final String messageKey, final MessageLayout layout, final Object... args)
    {
        for (final Player player : this.players)
        {
            messagesBox.sendMessage(player, messageKey, layout, args);
        }
    }

    /**
     * Wysyła przetłumaczoną wiadomość do graczy znajdujących się na tej arenie
     * z domyślnym layoutem.
     *
     * @param messagesBox obiekt przechowujący wiadomości.
     * @param messageKey klucz wiadomości.
     * @param args argumenty.
     */
    public void broadcast(final MessagesBox messagesBox, final String messageKey, final Object... args)
    {
        this.broadcast(messagesBox, messageKey, MessageLayout.DEFAULT, args);
    }
    
    private void announceJoinLeft(final Player player, final String messageKey)
    {
        final String name = player.getName();
        final int playersCount = this.getPlayers().size();
        final int maxPlayers = this.gameHostManager.getMiniGameConfig().getSlots();

        this.broadcast(this.messages, messageKey, name, playersCount, maxPlayers);
    }
}
