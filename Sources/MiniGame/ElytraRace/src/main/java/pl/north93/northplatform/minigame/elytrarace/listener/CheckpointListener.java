package pl.north93.northplatform.minigame.elytrarace.listener;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;


import org.bukkit.Bukkit;
import org.bukkit.Sound;
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

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.utils.region.Cuboid;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.region.ITrackedRegion;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;
import pl.north93.northplatform.minigame.elytrarace.arena.ElytraRaceArena;
import pl.north93.northplatform.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.north93.northplatform.minigame.elytrarace.arena.ElytraScorePlayer;
import pl.north93.northplatform.minigame.elytrarace.cfg.Checkpoint;
import pl.north93.northplatform.minigame.elytrarace.event.PlayerCheckpointEvent;

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

    private void playerEnterCheckpoint(final INorthPlayer player, final ElytraRaceArena arena, final Checkpoint checkpoint)
    {
        final ElytraRacePlayer elytraPlayer = player.getPlayerData(ElytraRacePlayer.class);
        if (elytraPlayer == null)
        {
            // don't handle if player isn't known by minigame
            return;
        }

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

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 1, 0);
        elytraPlayer.setCheckpoint(checkpoint);
        player.sendMessage(this.messages, "checkpoint.taken", elytraPlayer.getCheckpointNumber(), arena.getMaxCheckpoints());
    }

    @EventHandler
    public void playerReceiveDamage(final EntityDamageEvent event)
    {
        if (event.getEntityType() != EntityType.PLAYER)
        {
            return;
        }

        event.setCancelled(true);

        final INorthPlayer player = INorthPlayer.wrap((Player) event.getEntity());

        final LocalArena arena = getArena(player);
        if (arena.getGamePhase() != GamePhase.STARTED)
        {
            return;
        }

        final DamageCause cause = event.getCause();
        if (cause == DamageCause.FLY_INTO_WALL || cause == DamageCause.VOID)
        {
            this.backToCheckpoint(player, arena);
        }
        else if (cause == DamageCause.FALL && event.getDamage() > 1)
        {
            this.backToCheckpoint(player, arena);
        }
    }

    @EventHandler
    public void playerGlideDisable(final EntityToggleGlideEvent event)
    {
        if (event.getEntityType() != EntityType.PLAYER)
        {
            return;
        }

        final INorthPlayer player = INorthPlayer.wrap((Player) event.getEntity());

        final LocalArena arena = getArena(player);
        if (arena.getGamePhase() != GamePhase.STARTED)
        {
            return;
        }

        if (event.isGliding())
        {
            return;
        }

        this.backToCheckpoint(player, arena);
    }

    private void backToCheckpoint(final INorthPlayer player, final LocalArena arena)
    {
        final ElytraRacePlayer elytraPlayer = player.getPlayerData(ElytraRacePlayer.class);
        if (elytraPlayer == null || elytraPlayer.isDev() || elytraPlayer.isFinished())
        {
            return;
        }

        final ElytraScorePlayer scorePlayer = player.getPlayerData(ElytraScorePlayer.class);
        if (scorePlayer != null)
        {
            // resetujemy combo gdy teleportujemy do checkpointu
            scorePlayer.setCombo(0);
        }

        // wylaczamy lot elytra i ustawiamy odleglosc upadku na 0
        // zeby uniknac zdublowanego komunikatu
        player.setGliding(false);
        player.setFallDistance(0);

        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 0);

        player.sendMessage(this.messages, "checkpoint.teleport");

        final Checkpoint checkpoint = elytraPlayer.getCheckpoint();
        if (checkpoint != null)
        {
            player.teleport(checkpoint.getTeleport().toBukkit(player.getWorld()));
        }
        else
        {
            final ElytraRaceArena data = arena.getArenaData();
            player.teleport(data.getArenaConfig().getRestartLocation().toBukkit(player.getWorld()));
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
