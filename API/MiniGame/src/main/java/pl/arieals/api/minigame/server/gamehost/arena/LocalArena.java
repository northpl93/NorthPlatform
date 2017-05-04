package pl.arieals.api.minigame.server.gamehost.arena;

import static pl.arieals.api.minigame.shared.api.utils.InvalidGamePhaseException.checkGamePhase;


import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GamePhaseEventFactory;
import pl.arieals.api.minigame.server.gamehost.utils.Timer;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.arena.IArena;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.api.arena.netevent.ArenaDataChanged;
import pl.arieals.api.minigame.shared.impl.ArenaManager;

public class LocalArena implements IArena
{
    private final GameHostManager gameHostManager;
    private final ArenaManager    arenaManager;
    private final RemoteArena     data;
    private final ArenaWorld      world;
    private final PlayersManager  playersManager;
    private final Timer           timer;
    private       IArenaData      arenaData;

    public LocalArena(final GameHostManager gameHostManager, final ArenaManager arenaManager, final RemoteArena data)
    {
        this.gameHostManager = gameHostManager;
        this.arenaManager = arenaManager;
        this.data = data;
        this.world = new ArenaWorld(gameHostManager, this);
        this.playersManager = new PlayersManager(gameHostManager, arenaManager, this);
        this.timer = new Timer();
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

    public Timer getTimer()
    {
        return this.timer;
    }

    public ArenaWorld getWorld()
    {
        return this.world;
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

    public <T extends IArenaData> T getArenaData()
    {
        //noinspection unchecked
        return (T) this.arenaData;
    }

    public void prepareNewCycle()
    {
        checkGamePhase(this.getGamePhase(), GamePhase.POST_GAME); // arena moze byc zresetowana tylko po grze

        this.setGamePhase(GamePhase.LOBBY);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("data", this.data).append("world", this.world).append("playersManager", this.playersManager).append("timer", this.timer).append("arenaData", this.arenaData).toString();
    }
}