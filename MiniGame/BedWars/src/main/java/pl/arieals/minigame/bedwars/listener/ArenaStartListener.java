package pl.arieals.minigame.bedwars.listener;

import javax.xml.bind.JAXB;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.LobbyInitEvent;
import pl.arieals.api.minigame.shared.api.arena.DeathMatchState;
import pl.arieals.minigame.bedwars.BedWarsChatFormatter;
import pl.arieals.minigame.bedwars.arena.BedDestroyTask;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.generator.GeneratorTask;
import pl.arieals.minigame.bedwars.cfg.BwArenaConfig;
import pl.arieals.minigame.bedwars.cfg.BwConfig;
import pl.north93.zgame.api.bukkit.utils.FastBlockOp;
import pl.north93.zgame.api.bukkit.utils.region.Cuboid;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class ArenaStartListener implements Listener
{
    @Inject
    private BwConfig config;

    @EventHandler
    public void onLobbyInit(final LobbyInitEvent event)
    {
        final LocalArena arena = event.getArena();

        final BwArenaConfig arenaConfig = JAXB.unmarshal(arena.getWorld().getResource("BedWarsArena.xml"), BwArenaConfig.class);
        arena.setArenaData(new BedWarsArena(arena, this.config, arenaConfig));

        // ustawiamy formatter naszemu głównemu pokojowi czatu,
        // obsługuje on sytuacje gdy jesteśmy na etapie lobby (nie walnie NPE)
        arena.getPlayersManager().getChatRoom().setChatFormatter(BedWarsChatFormatter.INSTANCE);
    }

    @EventHandler
    public void onGameStart(final GameStartEvent event)
    {
        final LocalArena arena = event.getArena();

        // niszczymy lobby
        this.destroyLobby(arena);

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

    private void destroyLobby(final LocalArena arena)
    {
        final BedWarsArena arenaData = arena.getArenaData();
        final Cuboid lobby = arenaData.getConfig().getLobbyCuboid().toCuboid(arena.getWorld().getCurrentWorld());

        final long l = System.currentTimeMillis();
        for (final Block block : lobby)
        {
            if (block.getType() == Material.AIR)
            {
                continue;
            }
            FastBlockOp.setType(block, Material.AIR, (byte) 0);
        }
        System.out.println(System.currentTimeMillis() - l);
    }

    @EventHandler
    public void onHungerLoss(final FoodLevelChangeEvent event)
    {
        // disable hunger loss
        event.setCancelled(true);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
