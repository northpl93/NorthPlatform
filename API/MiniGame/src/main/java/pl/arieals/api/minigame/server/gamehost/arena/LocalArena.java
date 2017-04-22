package pl.arieals.api.minigame.server.gamehost.arena;

import java.util.List;
import java.util.UUID;

import pl.arieals.api.minigame.server.gamehost.GameHostManager;
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
    private final PlayersManager  playersManager;

    public LocalArena(final GameHostManager gameHostManager, final ArenaManager arenaManager, final RemoteArena data)
    {
        this.gameHostManager = gameHostManager;
        this.arenaManager = arenaManager;
        this.data = data;
        this.playersManager = new PlayersManager(gameHostManager, arenaManager, this);
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
    }

    @Override
    public List<UUID> getPlayers()
    {
        return this.data.getPlayers();
    }

    public PlayersManager getPlayersManager()
    {
        return this.playersManager;
    }

    public RemoteArena getAsRemoteArena()
    {
        return this.data;
    }

    public void resetArena()
    {
        this.data.setGamePhase(GamePhase.LOBBY);
        this.data.getPlayers().clear();
        this.arenaManager.setArena(this.data);
    }
}
