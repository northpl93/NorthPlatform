package pl.arieals.minigame.elytrarace.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartedEvent;
import pl.arieals.api.minigame.server.gamehost.region.ITrackedRegion;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.arieals.minigame.elytrarace.cfg.Checkpoint;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.utils.region.Cuboid;

public class CheckpointListener implements Listener
{
    private BukkitApiCore apiCore;

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
            System.out.println("Wczytywanie checkpointu " + checkpoint);
            final Cuboid region = checkpoint.getArea().toCuboid(arena.getWorld().getWorld());
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
}
