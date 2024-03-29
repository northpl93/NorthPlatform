package pl.north93.northplatform.api.minigame.controller.arena;

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.minigame.shared.api.arena.RemoteArena;
import pl.north93.northplatform.api.minigame.shared.api.arena.netevent.ArenaDeletedNetEvent;
import pl.north93.northplatform.api.minigame.shared.impl.arena.ArenaManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.event.ServerDeathNetEvent;
import pl.north93.northplatform.api.global.network.impl.servers.ServerDto;
import pl.north93.northplatform.api.global.network.server.ServerType;
import pl.north93.northplatform.api.global.redis.event.IEventManager;
import pl.north93.northplatform.api.global.redis.event.NetEventSubscriber;

/**
 * System obsługujący usuwanie aren z listy które znajdowały się
 * na serwerach które uległy awarii.
 */
@Slf4j
public class DeathArenasRemover
{
    @Inject
    private IEventManager eventManager;
    @Inject
    private ArenaManager  arenaManager;

    private DeathArenasRemover()
    {
    }

    @NetEventSubscriber(ServerDeathNetEvent.class)
    public void handleDeathServer(final ServerDeathNetEvent event)
    {
        final ServerDto server = event.getServer();
        if (server.getType() == ServerType.NORMAL)
        {
            // areny znajdują się tylko na serwerach o typie MINIGAME
            return;
        }

        final UUID serverId = server.getUuid();
        for (final RemoteArena arena : this.arenaManager.getAllArenas())
        {
            if (! arena.getServerId().equals(serverId))
            {
                continue;
            }

            this.arenaManager.removeArena(arena.getId());
            this.eventManager.callEvent(new ArenaDeletedNetEvent(arena.getId(), serverId, arena.getMiniGame()));
            log.info("Removed arena with ID {} due to server {} crash", arena.getId(), serverId);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
