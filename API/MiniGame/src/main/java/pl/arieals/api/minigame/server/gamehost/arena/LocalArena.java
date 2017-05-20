package pl.arieals.api.minigame.server.gamehost.arena;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GamePhaseEventFactory;
import pl.arieals.api.minigame.server.gamehost.region.IRegionManager;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.LobbyMode;
import pl.arieals.api.minigame.shared.api.arena.IArena;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.api.arena.netevent.ArenaDataChanged;
import pl.arieals.api.minigame.shared.impl.ArenaManager;
import pl.north93.zgame.api.bukkit.utils.StaticTimer;

public class LocalArena implements IArena
{
    private final GameHostManager     gameHostManager;
    private final ArenaManager        arenaManager;
    private final RemoteArena         data;
    private final ArenaWorld          world;
    private final PlayersManager      playersManager;
    private final StaticTimer         timer;
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
        
        /*if ( gamePhase == GamePhase.INITIALISING && getLobbyMode() == LobbyMode.EXTERNAL )
        {
            // W zewnetrzym lobby nie ma potrzeby restartowania mapy, ponieważ lobby jest zawsze załadowane
            // Manualnie wywołujemy event, aby nie rozgłaszać sieci zmiany stanu gry,
            // Natychmiast po wywołaniu ewentu przełącza gre w stan LOBBY
            Bukkit.getPluginManager().callEvent(new GameRestartEvent(this));
            setGamePhase(GamePhase.LOBBY);
            return;
        }*/
        
        this.data.setGamePhase(gamePhase);
        this.arenaManager.setArena(this.data);
        this.gameHostManager.publishArenaEvent(new ArenaDataChanged(this.data.getId(), gamePhase, this.data.getPlayers().size()));
        
        GamePhaseEventFactory.getInstance().callEvent(this);
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

    public ArenaWorld getWorld()
    {
        return this.world;
    }

    public ArenaStartScheduler getStartScheduler()
    {
        return startScheduler;
    }
    
    public MapVote getMapVote()
    {
        return mapVote;
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
        return gameHostManager.getMiniGameConfig().getLobbyMode();
    }
    
    public boolean isDynamic()
    {
        return gameHostManager.getMiniGameConfig().isDynamic();
    }
    
    public void teleportToLobby(Player player)
    {
        if ( getLobbyMode() == LobbyMode.EXTERNAL )
        {
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }
        
        // TODO: rewrite lobby system
        gameHostManager.getLobbyManager().addPlayer(this, player);
    }
    
    @SuppressWarnings("unchecked")
    public <T extends IArenaData> T getArenaData()
    {
        //noinspection unchecked
        return (T) this.arenaData;
    }

    public void prepareNewCycle()
    {
        Preconditions.checkState(this.getGamePhase() == GamePhase.POST_GAME); // arena moze byc zresetowana tylko po grze
        this.setGamePhase(GamePhase.INITIALISING);
    }

    public void startVoting()
    {
        Preconditions.checkState(getGamePhase() == GamePhase.LOBBY);
        
        if ( this.gameHostManager.getMiniGameConfig().getMapVoting().getEnabled() )
        {
            this.mapVote = new MapVote(gameHostManager, this);
            mapVote.printStartVoteInfo();
        }
    }
    
    void startArenaGame()
    {
        if ( mapVote == null )
        {
            setGamePhase(GamePhase.STARTED);
            return;
        }
        
        mapVote.printVotingResult();
        world.setActiveMap(mapVote.getWinner()).onComplete(() -> setGamePhase(GamePhase.STARTED));
    }
    
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("data", this.data).append("world", this.world).append("playersManager", this.playersManager).append("timer", this.timer).append("arenaData", this.arenaData).toString();
    }
}
