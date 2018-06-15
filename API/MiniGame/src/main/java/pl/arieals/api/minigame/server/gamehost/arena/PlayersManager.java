package pl.arieals.api.minigame.server.gamehost.arena;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.SpectatorJoinEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.SpectatorModeChangeEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.SpectatorQuitEvent;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.PlayerJoinInfo;
import pl.arieals.api.minigame.shared.api.PlayerStatus;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.api.cfg.MiniGameConfig;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.chat.global.ChatRoom;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

/**
 * Każda LocalArena ma swojego PlayersManagera
 */
public class PlayersManager
{
    private static final long PLAYER_JOIN_TIMEOUT = TimeUnit.SECONDS.toMillis(30);
    @Inject @Messages("MiniGameApi")
    private MessagesBox                     messages;
    @Inject
    private Logger                          logger;
    private final GameHostManager           gameHostManager;
    private final LocalArena                arena;
    private final ChatRoom                  chatRoom; // pokój czatu
    private final ChatRoom                  spectatorsRoom; // pokój czatu spectatorów
    private final List<Player>              players; // lista połączonych już graczy
    private final List<Player>              spectators; // lista polaczonych spectatorow
    private final Map<UUID, PlayerJoinInfo> joinInfos; // lista graczy chcacych wejsc na arene juz na niej bedacych

    public PlayersManager(final GameHostManager gameHostManager, final LocalArena arena)
    {
        this.gameHostManager = gameHostManager;
        this.arena = arena;

        final LocalArenaManager arenaManager = gameHostManager.getArenaManager();
        this.chatRoom = arenaManager.getChatRoomFor(arena, false);
        this.spectatorsRoom = arenaManager.getChatRoomFor(arena, true);

        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>(0); // zakladamy brak spectatorow
        this.joinInfos = new ConcurrentHashMap<>(); // may be accessed by server thread and rpc method executor in tryAddPlayers
    }

    /**
     * Zwraca pokój czatu powiązany z tą areną i przeznaczony dla
     * graczy biorących udział w rozgrywce.
     * @return Pokój czatu przeznaczony dla graczy biorących udział w rozgrywce.
     */
    public ChatRoom getChatRoom()
    {
        return this.chatRoom;
    }

    /**
     * Zwraca pokój czatu powiązany z tą areną i przeznaczony dla
     * graczy oglądających grę.
     * @return Pokój czatu przeznaczony dla graczy oglądających grę.
     */
    public ChatRoom getSpectatorsRoom()
    {
        return this.spectatorsRoom;
    }

    /**
     * Zwraca graczy z statusami {@link PlayerStatus#PLAYING_SPECTATOR} i {@link PlayerStatus#PLAYING}.
     * @return grający gracze znajdujący się na arenie.
     */
    public List<Player> getPlayers()
    {
        return this.players;
    }

    /**
     * Zwraca graczy z statusem {@link PlayerStatus#SPECTATOR}.
     * @return spectatorzy będący na arenie.
     */
    public List<Player> getSpectators()
    {
        return this.spectators;
    }

    /**
     * Zwraca wszystkich graczy na tej arenie lacznie z spectatorami.
     * @return wszyscy gracze na tej arenie.
     */
    public List<Player> getAllPlayers()
    {
        final ArrayList<Player> players = new ArrayList<>(this.players);
        players.addAll(this.spectators);
        return players;
    }

    /**
     * Zwraca true jesli ta arena ma powiazanego gracza o danym unikalnym identyfikatorze.
     * UWAGA! Gracz moze jeszcze nie byc online wiec {@code Bukkit.getPlayer(uuid)} zwroci
     * {@code null}.
     * @param player identyfikator do sprawdzenia.
     * @return czy ta arena ma powiazanego danego gracza.
     */
    public boolean containsPlayer(final UUID player)
    {
        return this.joinInfos.containsKey(player);
    }

    /**
     * @return maksymalna ilość graczy na arenie.
     */
    public int getMaxPlayers()
    {
        return this.gameHostManager.getMiniGameConfig().getSlots();
    }

    /**
     * @return minimalna ilość graczy wymagana do startu.
     */
    public int getMinPlayers()
    {
        return this.gameHostManager.getMiniGameConfig().getToStart();
    }

    /**
     * Sprawdza czy na arenie jest wymagana ilość graczy do wystartowania.
     * Uzywana jest zmienna oznaczajaca aktualnie polaczonych graczy.
     *
     * @return czy jest wymagana ilość graczy do wystartowania.
     */
    public boolean isEnoughToStart()
    {
        final MiniGameConfig miniGame = this.gameHostManager.getMiniGameConfig();
        return this.players.size() >= miniGame.getToStart();
    }

    // = = = OBSLUGA WEJSCIA GRACZA = = = //

    /**
     * Metoda probuje dodac graczy do tej areny.
     * Zsynchronizowana na this zeby moglo byc przetwarzane tylko jedno zadanie.
     *
     * @param players lista graczy do dodania
     * @param spectator czy tryb spectatora.
     * @return czy udalo sie dodac graczy.
     */
    public synchronized boolean tryAddPlayers(final List<PlayerJoinInfo> players, final boolean spectator)
    {
        if (this.arena.getGamePhase() == GamePhase.INITIALISING)
        {
            // jesli gra uzywa lobby zintegrowanego z mapa to mapa musi byc gotowa/zaladowana
            return false;
        }
        if (spectator)
        {
            // jesli dodajemy spectatorow uzywamy specjalnej logiki.
            return this.tryAddSpectators(players);
        }
        if (! this.gameHostManager.getMiniGameConfig().isDynamic() && this.arena.getGamePhase() != GamePhase.LOBBY)
        {
            // jesli gra nie jest dynamiczna to wchodzic na nia mozna tylko podczas LOBBY
            return false;
        }
        if (! this.canJoin(players))
        {
            return false;
        }
        players.forEach(playerJoinInfo -> this.joinInfos.put(playerJoinInfo.getUuid(), playerJoinInfo));

        final List<UUID> playerIds = players.stream().map(PlayerJoinInfo::getUuid).collect(Collectors.toList());
        final RemoteArena remoteArena = this.arena.getAsRemoteArena();
        remoteArena.getPlayers().addAll(playerIds);
        this.arena.uploadRemoteData();

        return true;
    }

    private boolean canJoin(final List<PlayerJoinInfo> players)
    {
        final MiniGameConfig miniGame = this.gameHostManager.getMiniGameConfig();
        final int normalSlots = miniGame.getSlots() - miniGame.getVipSlots();

        final List<PlayerJoinInfo> joinInfos = new ArrayList<>(this.joinInfos.values());
        joinInfos.addAll(players);

        final long all = joinInfos.size();
        final long vips = joinInfos.stream().filter(PlayerJoinInfo::isVip).count();
        final long normals = all - vips;

        if (all > miniGame.getSlots() || normals > normalSlots)
        {
            return false;
        }

        return normals + vips - miniGame.getVipSlots() <= normalSlots;
    }

    private boolean tryAddSpectators(final List<PlayerJoinInfo> players)
    {
        if (this.arena.getGamePhase() == GamePhase.POST_GAME)
        {
            // nie ma sensu tu wpuszczac spectatorow
            return false;
        }
        players.forEach(playerJoinInfo -> this.joinInfos.put(playerJoinInfo.getUuid(), playerJoinInfo));
        return true;
    }

    public void playerConnected(final Player player) // TODO support for reconnecting
    {
        final BukkitApiCore apiCore = this.gameHostManager.getApiCore();

        final PlayerJoinInfo playerJoinInfo = this.joinInfos.get(player.getUniqueId());
        if (playerJoinInfo.isSpectator())
        {
            this.spectators.add(player);
            apiCore.callEvent(new SpectatorJoinEvent(player, this.arena));
            this.updateStatus(player, PlayerStatus.SPECTATOR);
            return;
        }

        this.players.add(player);
        this.updateStatus(player, PlayerStatus.PLAYING);
        final PlayerJoinArenaEvent event = apiCore.callEvent(new PlayerJoinArenaEvent(player, this.arena, false, "player.joined_arena"));
        if ( event.getJoinMessage() != null )
        {
            this.announceJoinLeft(player, event.getJoinMessage());
        }
    }

    public void playerDisconnected(final Player player)
    {
        final BukkitApiCore apiCore = this.gameHostManager.getApiCore();

        this.joinInfos.remove(player.getUniqueId());
        if (this.spectators.contains(player))
        {
            this.spectators.remove(player);
            apiCore.callEvent(new SpectatorQuitEvent(this.arena, player));
            return;
        }

        this.players.remove(player);

        final RemoteArena remoteArena = this.arena.getAsRemoteArena();
        remoteArena.getPlayers().remove(player.getUniqueId());
        this.arena.uploadRemoteData();

        final MapVote mapVote = this.arena.getMapVote();
        if (mapVote != null)
        {
            mapVote.removeVote(player);
        }

        final PlayerQuitArenaEvent event = new PlayerQuitArenaEvent(player, this.arena, "player.quit_arena");
        apiCore.callEvent(event);
        
        if ( event.getQuitMessage() != null )
        {
            this.announceJoinLeft(player, event.getQuitMessage());
        }
    }

    public synchronized void checkTimeouts()
    {
        final long now = System.currentTimeMillis();

        final Iterator<PlayerJoinInfo> joinInfos = this.joinInfos.values().iterator();
        while (joinInfos.hasNext())
        {
            final PlayerJoinInfo joinInfo = joinInfos.next();
            if (Bukkit.getPlayer(joinInfo.getUuid()) != null || now - joinInfo.getIssuedAt() <= PLAYER_JOIN_TIMEOUT)
            {
                continue;
            }

            this.logger.log(Level.INFO, "Player {0} join timeout on arena {1}", new Object[]{joinInfo.getUuid(), this.arena.getId()});
            joinInfos.remove();
        }
    }

    // = = = AKTUALNY STAN GRACZA = = = //

    public PlayerStatus getStatus(final Player player)
    {
        final List<MetadataValue> minigameApiStatus = player.getMetadata("minigameApiStatus");
        if (minigameApiStatus.isEmpty())
        {
            return null;
        }
        return (PlayerStatus) minigameApiStatus.get(0).value();
    }

    public void updateStatus(final Player player, final PlayerStatus newStatus)
    {
        final PlayerStatus oldStatus = this.getStatus(player);
        if (newStatus == PlayerStatus.SPECTATOR && ! this.spectators.contains(player))
        {
            throw new IllegalArgumentException("You can't set SPECTATOR status for player that isn't spectating.");
        }
        else if (newStatus != PlayerStatus.SPECTATOR && this.spectators.contains(player))
        {
            throw new IllegalArgumentException("You can't set any other status than SPECTATING for spectating player.");
        }

        player.setMetadata("minigameApiStatus", new FixedMetadataValue(this.gameHostManager.getPlugin(), newStatus));
        this.gameHostManager.getApiCore().callEvent(new SpectatorModeChangeEvent(this.arena, player, oldStatus, newStatus));
    }

    // = = = WYSYLANIE WIADOMOSCI = = = //

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
        final int playersCount = this.joinInfos.size();
        final int maxPlayers = this.gameHostManager.getMiniGameConfig().getSlots();

        this.broadcast(this.messages, messageKey, name, playersCount, maxPlayers);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("players", this.players).append("spectators", this.spectators).append("joinInfos", this.joinInfos).toString();
    }
}
