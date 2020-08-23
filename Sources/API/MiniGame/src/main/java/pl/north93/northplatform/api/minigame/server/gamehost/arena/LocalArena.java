package pl.north93.northplatform.api.minigame.server.gamehost.arena;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.utils.StaticTimer;
import pl.north93.northplatform.api.global.metadata.MetaStore;
import pl.north93.northplatform.api.minigame.server.gamehost.GameHostManager;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.player.ArenaChatManager;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.player.PlayersManager;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.world.ArenaWorld;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.world.DeathMatch;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.world.MapVote;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.MapSwitchedEvent.MapSwitchReason;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.gamephase.GamePhaseEventFactory;
import pl.north93.northplatform.api.minigame.server.gamehost.region.IRegionManager;
import pl.north93.northplatform.api.minigame.server.gamehost.reward.IArenaRewards;
import pl.north93.northplatform.api.minigame.server.gamehost.reward.impl.ArenaRewardsImpl;
import pl.north93.northplatform.api.minigame.server.gamehost.scheduler.ArenaScheduler;
import pl.north93.northplatform.api.minigame.server.gamehost.scheduler.IArenaScheduler;
import pl.north93.northplatform.api.minigame.shared.api.GameIdentity;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.north93.northplatform.api.minigame.shared.api.LobbyMode;
import pl.north93.northplatform.api.minigame.shared.api.arena.IArena;
import pl.north93.northplatform.api.minigame.shared.api.arena.RemoteArena;
import pl.north93.northplatform.api.minigame.shared.api.arena.netevent.ArenaDataChangedNetEvent;
import pl.north93.northplatform.api.minigame.shared.api.arena.netevent.ArenaDeletedNetEvent;
import pl.north93.northplatform.api.minigame.shared.api.match.IMatchAccess;
import pl.north93.northplatform.api.minigame.shared.impl.arena.ArenaManager;

@Slf4j
public class LocalArena implements IArena
{
    private static final int MAX_TIME_TO_DISCONNECT = 30 * 20; // czas po jakim serwer wyrzuci graczy ktorzy nie wylecieli z areny
    private final GameHostManager gameHostManager;
    private final LocalArenaManager localArenaManager;
    private final RemoteArena data;
    private final ArenaWorld world;
    private final PlayersManager playersManager;
    private final ArenaChatManager chatManager;
    private final StaticTimer timer;
    private final ArenaScheduler scheduler;
    private final DeathMatch deathMatch;
    private final IArenaRewards rewards;
    private final ArenaStartScheduler startScheduler;
    private IMatchAccess match;
    private IArenaData arenaData;
    private MapVote mapVote;

    public LocalArena(final GameHostManager gameHostManager, final LocalArenaManager localArenaManager, final RemoteArena data)
    {
        this.gameHostManager = gameHostManager;
        this.localArenaManager = localArenaManager;
        this.data = data;
        this.world = new ArenaWorld(gameHostManager, this);
        this.playersManager = new PlayersManager(gameHostManager, this);
        this.chatManager = new ArenaChatManager(localArenaManager, this);
        this.timer = new StaticTimer();
        this.scheduler = new ArenaScheduler(this);
        this.deathMatch = new DeathMatch(gameHostManager, this);
        this.rewards = new ArenaRewardsImpl(this);
        this.startScheduler = new ArenaStartScheduler(this);
    }

    @Override
    public UUID getId()
    {
        return this.data.getId();
    }

    @Override
    public UUID getServerId()
    {
        return this.data.getServerId();
    }

    @Override
    public GameIdentity getMiniGame()
    {
        return this.gameHostManager.getMiniGameConfig().getGameIdentity();
    }

    @Override
    public String getWorldId()
    {
        return this.data.getWorldId();
    }

    @Override
    public String getWorldDisplayName()
    {
        return this.data.getWorldDisplayName();
    }
    
    @Override
    public GamePhase getGamePhase()
    {
        return this.data.getGamePhase();
    }
    
    @Override
    public MetaStore getMetaStore()
    {
        return this.data.getMetaStore();
    }

    public void setGamePhase(final GamePhase gamePhase)
    {
        if ( this.getGamePhase() == gamePhase )
        {
            return; // nic nie rób, jeżeli nie następuje zmiana fazy gry
        }

        this.scheduler.cancelAndClear();
        this.data.setGamePhase(gamePhase);

        log.info("Switched {} to game phase {}", this.getId(), gamePhase);
        GamePhaseEventFactory.getInstance().callEvent(this);
        
        // upload remote data after changes made by listeners
        this.uploadRemoteData();
    }

    /**
     * Przesuwa czas na arenie o podana wartosc w podanej jednostce. <br>
     * Ustawia nowy czas w timerze i przyspiesza wykonywanie zadan
     * w ArenaSchedulerze. Dziala tylko w trybie {@link GamePhase#STARTED}.
     *
     * @param time wartosc przesuniecia czasu do przodu.
     * @param timeUnit jednostka.
     */
    public void forwardTime(final long time, final TimeUnit timeUnit)
    {
        Preconditions.checkState(this.getGamePhase() == GamePhase.STARTED);

        final long timeMilis = timeUnit.toMillis(time);
        final long currentTime = this.timer.getCurrentTime(TimeUnit.MILLISECONDS);

        final long newTime = currentTime + timeMilis;
        this.timer.start(newTime, TimeUnit.MILLISECONDS, true);
        this.scheduler.moveTimeForward(timeMilis / 50);
    }

    @Override
    public Set<UUID> getPlayers()
    {
        return this.data.getPlayers();
    }

    @Override
    public int getMaxPlayers()
    {
        return this.gameHostManager.getMiniGameConfig().getSlots();
    }

    @Override
    public boolean isDynamic()
    {
        return this.gameHostManager.getMiniGameConfig().isDynamic();
    }

    public StaticTimer getTimer()
    {
        return this.timer;
    }

    public IArenaScheduler getScheduler()
    {
        return this.scheduler;
    }

    public ArenaWorld getWorld()
    {
        return this.world;
    }

    public ArenaStartScheduler getStartScheduler()
    {
        return this.startScheduler;
    }

    public DeathMatch getDeathMatch()
    {
        return this.deathMatch;
    }

    /**
     * Zwraca system nagrod powiazany z ta arena.
     * @return system nagrod tej areny.
     */
    public IArenaRewards getRewards()
    {
        return this.rewards;
    }

    public MapVote getMapVote()
    {
        return this.mapVote;
    }

    /**
     * Metoda pomocnicza, zwraca Region Managera z GameHostManagera
     * @return {@code IRegionManager}
     */
    public IRegionManager getRegionManager()
    {
        return this.gameHostManager.getRegionManager();
    }

    public PlayersManager getPlayersManager()
    {
        return this.playersManager;
    }

    public ArenaChatManager getChatManager()
    {
        return this.chatManager;
    }

    public RemoteArena getAsRemoteArena()
    {
        return this.data;
    }

    public void setArenaData(final IArenaData arenaData)
    {
        this.arenaData = arenaData;
    }

    public LobbyMode getLobbyMode()
    {
        return this.gameHostManager.getMiniGameConfig().getLobbyMode();
    }

    /**
     * Zwraca ustawiony wczesniej obiekt areny.
     *
     * @param <T> typ ustawionego obiektu.
     * @see LocalArena#setArenaData(IArenaData)
     * @return ustawiony obiekt areny w setArenaData.
     */
    @SuppressWarnings("unchecked")
    public <T extends IArenaData> T getArenaData()
    {
        //noinspection unchecked
        return (T) this.arenaData;
    }

    public GameHostManager getGameHostManager()
    {
        return this.gameHostManager;
    }

    /**
     * Zwraca instancję reprezentującą mecz rozgrywany na tej arenie.
     * Można go pobrać tylko gdy arena jest w trakcie lub po grze,
     * czyli {@link GamePhase#STARTED} lub {@link GamePhase#POST_GAME}.
     *
     * @return Mecz rozgrywany na tej arenie.
     */
    public IMatchAccess getMatch()
    {
        final GamePhase gamePhase = this.getGamePhase();
        Preconditions.checkState(gamePhase == GamePhase.STARTED || gamePhase == GamePhase.POST_GAME);

        return this.match;
    }

    /**
     * Konczy gre na tej arenie. Przelacza w tryb POST_GAME.
     * Dziala tylko w {@link GamePhase#STARTED}.
     */
    public void endGame()
    {
        Preconditions.checkState(this.getGamePhase() == GamePhase.STARTED);

        log.info("Ending game on {}", this.getId());
        this.setGamePhase(GamePhase.POST_GAME);
    }

    /**
     * Przygotowuje arene do nowego cyklu.
     * Powinno byc wywolywane po zakonczeniu gry po jakims czasie.
     * Dziala tylko w {@link GamePhase#POST_GAME}.
     */
    public void prepareNewCycle()
    {
        Preconditions.checkState(this.getGamePhase() == GamePhase.POST_GAME); // arena moze byc zresetowana tylko po grze
        log.info("Preparing {} to new cycle", this.getId());

        if (this.isDynamic())
        {
            // jesli gra jest dynamiczna to nikogo nie wywalamy tylko
            // przelaczamy do inutialising
            this.setGamePhase(GamePhase.INITIALISING);
        }
        else if (this.getPlayersManager().getAllPlayers().isEmpty())
        {
            // jesli nie ma zadnych graczy na arenie to od razu przelaczamy do INITIALISING.
            this.setGamePhase(GamePhase.INITIALISING);
        }
        else
        {
            // planujemy task ktory wymusi kick graczy po 30 sekundach
            // na wypadek gdyby bungee nie zdazylo ich wyrzucic
            // zostanie automatycznie usuniety gdy arena przelaczy sie do INITIALISING
            this.getScheduler().runTaskLater(this::kickPendingPlayers, MAX_TIME_TO_DISCONNECT);

            // arena zostanie przelaczona do INITIALISING gdy opuszcza ja wszyscy gracze
            final String gameHubId = this.gameHostManager.getMiniGameConfig().getHubId();
            this.gameHostManager.tpToHub(this.playersManager.getAllPlayers(), gameHubId);
        }
    }

    // uzywane do wyrzucenia graczy ktorzy zostali na arenie po probie teleportu do huba
    private void kickPendingPlayers()
    {
        log.warn("There are still connected players to {}, kicking them...", this.getId());

        final Set<INorthPlayer> players = this.playersManager.getAllPlayers();
        players.forEach(player -> player.kickPlayer(""));
    }

    /**
     * Usuwa dana arene z listy aren i wysyla ArenaDeletedEvent do Redisa.
     * <for>
     * Uwaga! Modyfikuje liste aren z {@link ArenaManager} wiec trzeba
     * uwazac z petlami.
     */
    public void delete()
    {
        log.info("Removing arena {}", this.getId());

        // removes arena from local list and from redis
        this.localArenaManager.removeArena(this);

        final UUID serverId = this.gameHostManager.getServerId();
        this.gameHostManager.publishArenaEvent(new ArenaDeletedNetEvent(this.getId(), serverId, this.getMiniGame()));

        this.world.deleteCurrentWorld();
    }

    public void startVoting()
    {
        Preconditions.checkState(this.getGamePhase() == GamePhase.LOBBY);
        
        if ( this.gameHostManager.getMiniGameConfig().getMapVoting().getEnabled() )
        {
            this.mapVote = new MapVote(this.gameHostManager, this);
            this.mapVote.printStartVoteInfo();
        }
    }

    public void setMatch(final IMatchAccess match)
    {
        this.match = match;
    }

    // kończy głosowanie i zmienia etap gry na STARTED
    // aktualnie wywoływane po zakończeniu odliczania do startu
    /*default*/ void startArenaGame()
    {
        if (this.mapVote == null)
        {
            this.setGamePhase(GamePhase.STARTED);
            return;
        }

        this.mapVote.printVotingResult();
        this.world.setActiveMap(this.mapVote.getWinner(), MapSwitchReason.ARENA_INITIALISE)
                  .onComplete(() -> this.setGamePhase(GamePhase.STARTED));
    }

    public void uploadRemoteData()
    {
        this.localArenaManager.updateArenaData(this);
        this.gameHostManager.publishArenaEvent(new ArenaDataChangedNetEvent(this.getId(), this.getMiniGame(), this.getWorldDisplayName(), this.getGamePhase(), this.data.getPlayers().size()));
    }
    
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("data", this.data).append("world", this.world).append("playersManager", this.playersManager).append("timer", this.timer).append("arenaData", this.arenaData).toString();
    }
}
