package pl.arieals.minigame.elytrarace.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.math.DioriteRandomUtils;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartedEvent;
import pl.arieals.api.minigame.server.gamehost.region.ITrackedRegion;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.arieals.minigame.elytrarace.cfg.Checkpoint;
import pl.north93.zgame.api.bukkit.utils.region.Cuboid;
import pl.north93.zgame.api.bukkit.utils.xml.XmlLocation;

public class CheckpointListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGH) // post ArenaStartListener
    public void startGame(final GameStartedEvent event)
    {
        final ElytraRaceArena arenaData = event.getArena().getArenaData();

        this.setupRegions(event.getArena(), arenaData);
    }

    private void setupRegions(final LocalArena arena, final ElytraRaceArena elytraRaceArena)
    {
        for (final Checkpoint checkpoint : elytraRaceArena.getArenaConfig().getCheckpoints())
        {
            final Cuboid region = checkpoint.getArea().toCuboid(arena.getWorld().getCurrentWorld());
            final ITrackedRegion tracked = arena.getRegionManager().create(region);
            tracked.whenEnter(player -> this.playerEnterCheckpoint(player, checkpoint));
        }
    }

    private void playerEnterCheckpoint(final Player player, final Checkpoint checkpoint)
    {
        final ElytraRacePlayer elytraPlayer = getPlayerData(player, ElytraRacePlayer.class);

        final Checkpoint prevCheck = elytraPlayer.getCheckpoint();
        if (prevCheck != null && prevCheck.getNumber() >= checkpoint.getNumber())
        {
            return;
        }

        elytraPlayer.setCheckpoint(checkpoint);
        player.sendMessage("Zaliczyles checkpoint: " + checkpoint);
    }

    @EventHandler
    public void playerReceiveDamage(final EntityDamageEvent event)
    {
        if (event.getEntityType() != EntityType.PLAYER)
        {
            return;
        }

        final Player player = (Player) event.getEntity();

        final DamageCause cause = event.getCause();
        if (cause == DamageCause.FLY_INTO_WALL || cause == DamageCause.FALL || cause == DamageCause.VOID)
        {
            final ElytraRacePlayer elytraPlayer = getPlayerData(player, ElytraRacePlayer.class);

            final Checkpoint checkpoint = elytraPlayer.getCheckpoint();
            if (checkpoint != null)
            {
                player.teleport(checkpoint.getTeleport().toBukkit(player.getWorld()));
            }
            else
            {
                final ElytraRaceArena data = getArena(player).getArenaData();
                final XmlLocation randomLocation = DioriteRandomUtils.getRandom(data.getArenaConfig().getStartLocations());

                player.teleport(randomLocation.toBukkit(player.getWorld()));
            }
        }

        event.setCancelled(true);
    }

    // TODO blokowanie wyłączenia latania/teleport na checkpoint przy wyłączeniu?

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
