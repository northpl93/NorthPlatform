package pl.arieals.api.minigame.server.gamehost.arena;

import static java.text.MessageFormat.format;

import static pl.arieals.api.minigame.shared.api.utils.ArenaStreamUtils.containsPlayer;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;

import org.bukkit.World;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GamePhaseEventFactory;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.impl.ArenaManager;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class LocalArenaManager
{
    @Inject
    private BukkitApiCore          apiCore;
    @Inject
    private Logger                 logger;
    @Inject
    private MiniGameServer         miniGameServer;
    private final List<LocalArena> arenas = new ArrayList<>();

    public LocalArena createArena()
    {
        final GameHostManager serverManager = this.miniGameServer.getServerManager();
        final ArenaManager arenaManager = this.miniGameServer.getArenaManager();

        final UUID arenaId = UUID.randomUUID();
        final UUID serverId = this.apiCore.getServerId();
        final String gameId = serverManager.getMiniGameConfig().getMiniGameId();

        final RemoteArena arenaData = new RemoteArena(arenaId, serverId, gameId, "", GamePhase.INITIALISING, new ArrayList<>());
        final LocalArena localArena = new LocalArena(serverManager, arenaManager, arenaData);
        this.arenas.add(localArena);
        arenaManager.setArena(arenaData);

        GamePhaseEventFactory.getInstance().callEvent(localArena); // invoke GameInitEvent

        final String msg = "Added new local arena! Game ID:{0}, Arena ID:{1}, Server ID:{2}, Game Phase:{3}";
        this.logger.info(format(msg, gameId, arenaId, serverId, arenaData.getGamePhase()));

        return localArena;
    }

    public List<LocalArena> getArenas()
    {
        return this.arenas;
    }

    public Optional<LocalArena> getArenaAssociatedWith(final UUID player)
    {
        return this.arenas.stream().filter(containsPlayer(player)).findFirst();
    }

    public LocalArena getArena(final UUID arenaId)
    {
        for (final LocalArena arena : this.arenas)
        {
            if (arena.getId().equals(arenaId))
            {
                return arena;
            }
        }
        return null;
    }

    public LocalArena getArena(final World world)
    {
        Preconditions.checkNotNull(world, "world can't be null");
        for (final LocalArena arena : this.arenas)
        {
            if (world.equals(arena.getWorld().getCurrentWorld()))
            {
                return arena;
            }
        }
        return null;
    }

    public void removeArenas()
    {
        for (final LocalArena arena : new ArrayList<>(this.arenas)) // unikamy ConcurrentModificationException
        {
            arena.delete();
        }
    }
}
