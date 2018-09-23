package pl.arieals.api.minigame.server.gamehost.arena.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.MiniGameApi;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.arena.world.MapVote;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.SpectatorJoinEvent;
import pl.arieals.api.minigame.server.gamehost.event.player.SpectatorQuitEvent;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.PlayerJoinInfo;
import pl.arieals.api.minigame.shared.api.PlayerStatus;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.api.arena.reconnect.ReconnectTicket;
import pl.arieals.api.minigame.shared.api.cfg.MiniGameConfig;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.utils.MetadataUtils;
import pl.north93.zgame.api.global.metadata.MetaStore;

/**
 * Każda LocalArena ma swojego PlayersManagera
 */
@Slf4j
public class PlayersManager
{
    private static final long PLAYER_JOIN_TIMEOUT = TimeUnit.SECONDS.toMillis(30);
    private final GameHostManager           gameHostManager;
    private final ReconnectHandler          reconnectHandler;
    private final LocalArena                arena;
    private final Set<INorthPlayer>         players; // lista połączonych już graczy
    private final Set<INorthPlayer>         spectators; // lista polaczonych spectatorow
    private final Map<UUID, PlayerJoinInfo> joinInfos; // lista graczy chcacych wejsc na arene juz na niej bedacych

    public PlayersManager(final GameHostManager gameHostManager, final LocalArena arena)
    {
        this.gameHostManager = gameHostManager;
        this.arena = arena;

        this.reconnectHandler = new ReconnectHandler(gameHostManager, arena);
        this.players = new HashSet<>();
        this.spectators = new HashSet<>(0); // zakladamy brak spectatorow
        this.joinInfos = new ConcurrentHashMap<>(); // may be accessed by server thread and rpc method executor in tryAddPlayers
    }

    /**
     * Zwraca graczy z statusami {@link PlayerStatus#PLAYING_SPECTATOR} i {@link PlayerStatus#PLAYING}.
     * @return grający gracze znajdujący się na arenie.
     */
    public Set<INorthPlayer> getPlayers()
    {
        return this.players;
    }

    /**
     * Zwraca graczy z statusem {@link PlayerStatus#SPECTATOR}.
     * @return spectatorzy będący na arenie.
     */
    public Set<INorthPlayer> getSpectators()
    {
        return this.spectators;
    }

    /**
     * Zwraca wszystkich graczy na tej arenie lacznie z spectatorami.
     * @return wszyscy gracze na tej arenie.
     */
    public Set<INorthPlayer> getAllPlayers()
    {
        final Set<INorthPlayer> players = new HashSet<>(this.players);
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
     * Metoda próbuje rozpocząć proces powrotu danego gracza do gry.
     *
     * @param ticket Bilet powrotu do gry pobrany z obiektu gracza.
     * @return True jeśli gracz może wrócić do gry.
     */
    public synchronized boolean tryReconnect(final ReconnectTicket ticket)
    {
        if (this.reconnectHandler.tryReconnect(ticket))
        {
            final PlayerJoinInfo joinInfo = new PlayerJoinInfo(ticket.getPlayerId(), false, false);
            this.joinInfos.put(ticket.getPlayerId(), joinInfo);

            return true;
        }

        return false;
    }

    /**
     * Metoda probuje dodac graczy do tej areny.
     * Zsynchronizowana na this zeby moglo byc przetwarzane tylko jedno zadanie.
     *
     * @param players lista graczy do dodania
     * @param metadata Metadata do dodania, jeśli udało się dodać graczy.
     * @return czy udalo sie dodac graczy.
     */
    public synchronized boolean tryAddPlayers(final List<PlayerJoinInfo> players, final MetaStore metadata)
    {
        if (this.arena.getGamePhase() == GamePhase.INITIALISING)
        {
            // jesli gra uzywa lobby zintegrowanego z mapa to mapa musi byc gotowa/zaladowana
            return false;
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

        final RemoteArena remoteArena = this.arena.getAsRemoteArena();
        for (final PlayerJoinInfo player : players)
        {
            remoteArena.getPlayers().add(player.getUuid());
        }
        remoteArena.getMetadata().addAll(metadata);
        this.arena.uploadRemoteData();

        return true;
    }

    public boolean tryAddSpectators(final List<PlayerJoinInfo> players)
    {
        final GamePhase gamePhase = this.arena.getGamePhase();
        if (gamePhase == GamePhase.INITIALISING || gamePhase == GamePhase.POST_GAME)
        {
            // INITIALISING - jesli gra uzywa lobby zintegrowanego z mapa to mapa musi byc gotowa/zaladowana
            // POST_GAME - nie ma sensu tu wpuszczac spectatorow
            return false;
        }
        players.forEach(playerJoinInfo -> this.joinInfos.put(playerJoinInfo.getUuid(), playerJoinInfo));
        return true;
    }

    private boolean canJoin(final List<PlayerJoinInfo> players)
    {
        // jeżeli gra jest dynamiczna pozwalamy wejsc wszystkim - to dana minigra musi obslyzyc zapis do gry we wlasnym zakresie
        if ( this.arena.isDynamic() )
        {
            return true;
        }
        
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

    /**
     * Punkt wejścia obsługujący właściwe wejście gracza na serwer.
     *
     * @param player Gracz który wchodzi na serwer do tej areny.
     */
    public void playerConnected(final INorthPlayer player)
    {
        final PlayerJoinInfo playerJoinInfo = this.joinInfos.get(player.getUniqueId());
        if (playerJoinInfo.isSpectator())
        {
            this.connectSpectator(player);
        }
        else
        {
            this.connectPlayer(player);
        }
    }

    private void connectSpectator(final INorthPlayer player)
    {
        this.spectators.add(player);

        final BukkitApiCore apiCore = this.gameHostManager.getApiCore();
        apiCore.callEvent(new SpectatorJoinEvent(player, this.arena));

        MiniGameApi.setPlayerStatus(player, PlayerStatus.SPECTATOR);
    }

    private void connectPlayer(final INorthPlayer player)
    {
        this.players.add(player);

        final boolean isReconnected = this.reconnectHandler.handleReconnect(player);
        final PlayerJoinArenaEvent event = new PlayerJoinArenaEvent(player, this.arena, isReconnected, "player.joined_arena");
        this.gameHostManager.getApiCore().callEvent(event);

        if (event.getJoinMessage() != null)
        {
            this.arena.getChatManager().announceJoinLeft(player, event.getJoinMessage());
        }

        MiniGameApi.setPlayerStatus(player, PlayerStatus.PLAYING);
    }

    /**
     * Punkt wejścia obsługujący wyjście gracza z serwera.
     *
     * @param player Gracz wychodzący z serwera/
     */
    public void playerDisconnected(final INorthPlayer player)
    {
        this.joinInfos.remove(player.getUniqueId());
        if (this.spectators.contains(player))
        {
            this.disconnectSpectator(player);
        }
        else
        {
            this.disconnectPlayer(player);
        }
    }

    private void disconnectSpectator(final INorthPlayer player)
    {
        this.spectators.remove(player);
        this.gameHostManager.getApiCore().callEvent(new SpectatorQuitEvent(this.arena, player));
    }

    private void disconnectPlayer(final INorthPlayer player)
    {
        final BukkitApiCore apiCore = this.gameHostManager.getApiCore();
        this.players.remove(player);

        final PlayerQuitArenaEvent event = apiCore.callEvent(new PlayerQuitArenaEvent(player, this.arena, this.shouldBeAbleToReconnect(), "player.quit_arena"));
        if (event.canReconnect())
        {
            this.reconnectHandler.addReconnectCandidate(player);
        }
        else
        {
            // gracz i tak juz nie wroci na ten serwer, a pozostale dane moga powodowac memory-leak
            MetadataUtils.removePlayerMetadata(player);
        }

        final RemoteArena remoteArena = this.arena.getAsRemoteArena();
        remoteArena.getPlayers().remove(player.getUniqueId());
        this.arena.uploadRemoteData();

        final MapVote mapVote = this.arena.getMapVote();
        if (mapVote != null)
        {
            mapVote.removeVote(player);
        }

        if (event.getQuitMessage() != null)
        {
            this.arena.getChatManager().announceJoinLeft(player, event.getQuitMessage());
        }
    }

    private boolean shouldBeAbleToReconnect()
    {
        if (this.arena.getGamePhase() != GamePhase.STARTED)
        {
            return false;
        }

        return this.reconnectHandler.isReconnectSupported();
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

            log.info("Player {} join timeout on arena {}", joinInfo.getUuid(), this.arena.getId());
            joinInfos.remove();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("players", this.players).append("spectators", this.spectators).append("joinInfos", this.joinInfos).toString();
    }
}
