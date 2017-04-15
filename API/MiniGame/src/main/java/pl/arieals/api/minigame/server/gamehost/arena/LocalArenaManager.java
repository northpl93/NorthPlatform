package pl.arieals.api.minigame.server.gamehost.arena;

import static java.text.MessageFormat.format;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.arena.RemoteArena;
import pl.arieals.api.minigame.shared.impl.ArenaManager;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;

public class LocalArenaManager
{
    private BukkitApiCore          apiCore;
    private Logger                 logger;
    @InjectComponent("MiniGameApi.Server")
    private MiniGameServer         miniGameServer;
    private final List<LocalArena> arenas = new ArrayList<>();

    public LocalArena createArena()
    {
        final ArenaManager arenaManager = this.miniGameServer.getArenaManager();
        final UUID arenaId = UUID.randomUUID();
        final UUID serverId = this.apiCore.getServer().get().getUuid();

        final RemoteArena arenaData = new RemoteArena(arenaId, serverId, GamePhase.LOBBY, new ArrayList<>());
        final LocalArena localArena = new LocalArena(arenaManager, arenaData);
        this.arenas.add(localArena);
        arenaManager.setArena(arenaData);

        final String msg = "Added new local arena! Arena ID:{0}, Server ID:{1}, Game Phase:{2}";
        this.logger.info(format(msg, arenaId, serverId, arenaData.getGamePhase()));

        return localArena;
    }

    public List<LocalArena> getArenas()
    {
        return this.arenas;
    }
}
