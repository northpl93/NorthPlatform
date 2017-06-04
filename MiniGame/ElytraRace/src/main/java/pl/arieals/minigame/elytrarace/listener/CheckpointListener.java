package pl.arieals.minigame.elytrarace.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityToggleGlideEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.utils.math.DioriteRandomUtils;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.arieals.api.minigame.server.gamehost.region.ITrackedRegion;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.arieals.minigame.elytrarace.cfg.Checkpoint;
import pl.arieals.minigame.elytrarace.event.PlayerCheckpointEvent;
import pl.north93.zgame.api.bukkit.utils.region.Cuboid;
import pl.north93.zgame.api.bukkit.utils.xml.XmlLocation;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class CheckpointListener implements Listener
{
    @Inject @Messages("ElytraRace")
    private MessagesBox messages;

    @EventHandler(priority = EventPriority.HIGH) // post ArenaStartListener
    public void startGame(final GameStartEvent event)
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
            tracked.whenEnter(player -> this.playerEnterCheckpoint(player, elytraRaceArena, checkpoint));
        }
    }

    private void playerEnterCheckpoint(final Player player, final ElytraRaceArena arena, final Checkpoint checkpoint)
    {
        final ElytraRacePlayer elytraPlayer = getPlayerData(player, ElytraRacePlayer.class);

        final Checkpoint prevCheck = elytraPlayer.getCheckpoint();
        if (prevCheck != null && prevCheck.getNumber() >= checkpoint.getNumber())
        {
            return;
        }

        final PlayerCheckpointEvent event = new PlayerCheckpointEvent(player, checkpoint);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
        {
            return;
        }

        elytraPlayer.setCheckpoint(checkpoint);
        this.messages.sendMessage(player, "checkpoint.taken", elytraPlayer.getCheckpointNumber(), arena.getMaxCheckpoints());
    }

    @EventHandler
    public void playerReceiveDamage(final EntityDamageEvent event)
    {
        if (event.getEntityType() != EntityType.PLAYER)
        {
            return;
        }

        event.setCancelled(true);

        final Player player = (Player) event.getEntity();
        if (getArena(player).getGamePhase() != GamePhase.STARTED)
        {
            return;
        }

        final DamageCause cause = event.getCause();
        if (cause == DamageCause.FLY_INTO_WALL || cause == DamageCause.VOID)
        {
            this.backToCheckpoint(player);
        }
        else if (cause == DamageCause.FALL && event.getDamage() > 1)
        {
            this.backToCheckpoint(player);
        }
    }

    @EventHandler
    public void playerGlideDisable(final EntityToggleGlideEvent event)
    {
        if (event.getEntityType() != EntityType.PLAYER)
        {
            return;
        }

        final Player player = (Player) event.getEntity();
        if (getArena(player).getGamePhase() != GamePhase.STARTED)
        {
            return;
        }

        if (event.isGliding())
        {
            return;
        }

        this.backToCheckpoint(player);
    }

    private void backToCheckpoint(final Player player)
    {
        final ElytraRacePlayer elytraPlayer = getPlayerData(player, ElytraRacePlayer.class);
        if (elytraPlayer == null || elytraPlayer.isDev())
        {
            return;
        }

        this.messages.sendMessage(player, "checkpoint.teleport");

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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
