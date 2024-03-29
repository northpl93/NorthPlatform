package pl.north93.northplatform.minigame.bedwars.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.bukkit.utils.nms.FastBlockOp;
import pl.north93.northplatform.api.bukkit.utils.region.Cuboid;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.utils.JaxbUtils;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.gamephase.LobbyInitEvent;
import pl.north93.northplatform.api.minigame.shared.api.arena.DeathMatchState;
import pl.north93.northplatform.minigame.bedwars.BedWarsChatFormatter;
import pl.north93.northplatform.minigame.bedwars.arena.BedDestroyTask;
import pl.north93.northplatform.minigame.bedwars.arena.BedWarsArena;
import pl.north93.northplatform.minigame.bedwars.arena.Team;
import pl.north93.northplatform.minigame.bedwars.arena.generator.GeneratorTask;
import pl.north93.northplatform.minigame.bedwars.cfg.BwArenaConfig;
import pl.north93.northplatform.minigame.bedwars.cfg.BwConfig;

@Slf4j
public class ArenaStartListener implements AutoListener
{
    @Inject
    private BwConfig config;

    @EventHandler
    public void onLobbyInit(final LobbyInitEvent event)
    {
        final LocalArena arena = event.getArena();

        final BwArenaConfig arenaConfig = JaxbUtils.unmarshal(arena.getWorld().getResource("BedWarsArena.xml"), BwArenaConfig.class);
        arena.setArenaData(new BedWarsArena(arena, this.config, arenaConfig));

        // ustawiamy formatter naszemu głównemu pokojowi czatu,
        // obsługuje on sytuacje gdy jesteśmy na etapie lobby (nie walnie NPE)
        arena.getChatManager().getChatRoom().setChatFormatter(BedWarsChatFormatter.INSTANCE);
    }

    @EventHandler
    public void scheduleTasks(final GameStartEvent event)
    {
        final LocalArena arena = event.getArena();

        // planujemy task generatorów co 1 tick
        arena.getScheduler().runTaskTimer(new GeneratorTask(arena), 1, 1);

        // planujemy task usuwajacy lozka
        arena.getScheduler().runTaskLater(new BedDestroyTask(arena), this.config.getDestroyBedsAt());

        // planujemy uruchomienie deathmatchu
        arena.getScheduler().runTaskLater(() ->
        {
            if (arena.getDeathMatch().getState() == DeathMatchState.NOT_STARTED)
            {
                arena.getDeathMatch().activateDeathMatch();
            }
        }, this.config.getStartDeathMatchAt());
    }

    @EventHandler
    public void destroyLobby(final GameStartEvent event)
    {
        final LocalArena arena = event.getArena();

        final BedWarsArena arenaData = arena.getArenaData();
        final Cuboid lobby = arenaData.getConfig().getLobbyCuboid().toCuboid(arena.getWorld().getCurrentWorld());

        final long startedAt = System.currentTimeMillis();
        for (final Block block : lobby)
        {
            if (block.getType() == Material.AIR)
            {
                continue;
            }
            FastBlockOp.setType(block, Material.AIR, (byte) 0);
        }
        log.info("BedWars lobby destroyed in: {}", System.currentTimeMillis() - startedAt);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void destroyBedsInEmptyTeams(final GameStartEvent event)
    {
        final BedWarsArena arenaData = event.getArena().getArenaData();

        for (final Team team : arenaData.getTeams())
        {
            if (! team.getPlayers().isEmpty())
            {
                continue;
            }

            team.destroyBed(true);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
