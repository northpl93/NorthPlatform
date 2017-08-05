package pl.arieals.api.minigame.server.gamehost.arena;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GamePhaseEventFactory;
import pl.arieals.api.minigame.server.gamehost.region.IRegionManager;
import pl.arieals.api.minigame.server.gamehost.reward.IArenaRewards;
import pl.arieals.api.minigame.server.gamehost.reward.impl.ArenaRewardsImpl;
import pl.arieals.api.minigame.server.gamehost.scheduler.ArenaScheduler;
import pl.arieals.api.minigame.server.gamehost.scheduler.IArenaScheduler;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.LobbyMode;
import pl.arieals.api.minigame.shared.api.arena.IArena;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.api.arena.netevent.ArenaDataChangedNetEvent;
import pl.arieals.api.minigame.shared.api.arena.netevent.ArenaDeletedNetEvent;
import pl.arieals.api.minigame.shared.impl.ArenaManager;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.utils.StaticTimer;

public class LocalArena implements IArena
{
    private final GameHostManager     gameHostManager;
    private final ArenaManager        arenaManager;
    private final RemoteArena         data;
    private final ArenaWorld          world;
    private final PlayersManager      playersManager;
    private final StaticTimer         timer;
    private final ArenaScheduler      scheduler;
    private final DeathMatch          deathMatch;
    private final IArenaRewards       rewards;
    private       IArenaData          arenaData;
    private       MapVote             mapVote;
    private final ArenaStartScheduler startScheduler;

    public LocalArena(final GameHostManager gameHostManager, final ArenaManager arenaManager, final RemoteArena data)
    {
        this.gameHostManager = gameHostManager;
        this.arenaManager = arenaManager;
        this.data = data;
        this.world = new ArenaWorld(gameHostManager, this);
        this.playersManager = new PlayersManager(gameHostManager, arenaManager, this);
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
    public String getMiniGameId()
    {
        return this.gameHostManager.getMiniGameConfig().getMiniGameId();
    }

    @Override
    public String getWorldId()
    {
        return this.world.getCurrentMapTemplate().getName();
    }

    @Override
    public GamePhase getGamePhase()
    {
        return this.data.getGamePhase();
    }

    public void setGamePhase(final GamePhase gamePhase)
    {
        if ( this.getGamePhase() == gamePhase )
        {
            return; // nic nie rób, jeżeli nie następuje zmiana fazy gry
        }

        this.scheduler.cancelAndClear();
        this.data.setGamePhase(gamePhase);
        this.arenaManager.setArena(this.data);
        
        String mapName = this.world.getCurrentMapTemplate() != null ? this.world.getCurrentMapTemplate().getName() : "Lobby";
        this.gameHostManager.publishArenaEvent(new ArenaDataChangedNetEvent(this.data.getId(), this.getMiniGameId(), mapName, gamePhase, this.data.getPlayers().size()));
        
        GamePhaseEventFactory.getInstance().callEvent(this);
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
    public List<UUID> getPlayers()
    {
        return this.data.getPlayers();
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
    
    void setMapVote(MapVote mapVote)
    {
        this.mapVote = mapVote;
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
    
    public boolean isDynamic()
    {
        return this.gameHostManager.getMiniGameConfig().isDynamic();
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

    /**
     * Przygotowuje arene do nowego cyklu.
     * Powinno byc wywolywane po zakonczeniu gry po jakims czasie.
     * Dziala tylko w {@link GamePhase#POST_GAME}.
     */
    public void prepareNewCycle()
    {
        Preconditions.checkState(this.getGamePhase() == GamePhase.POST_GAME); // arena moze byc zresetowana tylko po grze
        this.setGamePhase(GamePhase.INITIALISING);
    }

    /**
     * Usuwa dana arene z listy aren i wysyla ArenaDeletedEvent do Redisa.
     * <for>
     * Uwaga! Modyfikuje liste aren z {@link ArenaManager} wiec trzeba
     * uwazac z petlami.
     */
    public void delete()
    {
        final BukkitApiCore apiCore = this.gameHostManager.getApiCore();
        apiCore.getLogger().log(Level.INFO, "Removing arena {0}", this.getId());

        this.arenaManager.removeArena(this.getId());

        final UUID serverId = apiCore.getServerId();
        final String miniGameId = this.gameHostManager.getMiniGameConfig().getMiniGameId();
        this.gameHostManager.publishArenaEvent(new ArenaDeletedNetEvent(this.getId(), serverId, miniGameId));

        this.gameHostManager.getArenaManager().getArenas().remove(this);

        if (! this.world.delete())
        {
            apiCore.getLogger().log(Level.WARNING, "Failed to unload world of arena {0}", this.getId());
        }
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
    
    void startArenaGame()
    {
        if (this.mapVote == null )
        {
            this.setGamePhase(GamePhase.STARTED);
            return;
        }

        this.mapVote.printVotingResult();
        this.world.setActiveMap(this.mapVote.getWinner()).onComplete(() -> this.setGamePhase(GamePhase.STARTED));
    }
    
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("data", this.data).append("world", this.world).append("playersManager", this.playersManager).append("timer", this.timer).append("arenaData", this.arenaData).toString();
    }
}
