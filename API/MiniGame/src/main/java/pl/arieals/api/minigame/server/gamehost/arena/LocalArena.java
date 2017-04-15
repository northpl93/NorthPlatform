package pl.arieals.api.minigame.server.gamehost.arena;

import java.util.List;
import java.util.UUID;

import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.arena.IArena;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.impl.ArenaManager;

public class LocalArena implements IArena
{
    private final ArenaManager arenaManager;
    private final RemoteArena  data;

    public LocalArena(final ArenaManager arenaManager, final RemoteArena data)
    {
        this.arenaManager = arenaManager;
        this.data = data;
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
    }

    @Override
    public List<UUID> getPlayers()
    {
        return this.data.getPlayers();
    }

    public void addPlayer(final UUID uuid)
    {
        this.data.getPlayers().add(uuid);
        this.arenaManager.setArena(this.data);
    }

    public void resetArena()
    {
        this.data.setGamePhase(GamePhase.LOBBY);
        this.data.getPlayers().clear();
        this.arenaManager.setArena(this.data);
    }
}
