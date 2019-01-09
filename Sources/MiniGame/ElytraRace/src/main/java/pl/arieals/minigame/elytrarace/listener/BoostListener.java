package pl.arieals.minigame.elytrarace.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.arieals.api.minigame.server.gamehost.region.ITrackedRegion;
import pl.arieals.minigame.elytrarace.arena.ElytraRaceArena;
import pl.arieals.minigame.elytrarace.cfg.Boost;
import pl.north93.zgame.api.bukkit.utils.region.Cuboid;

public class BoostListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGH) // post ArenaStartListener
    public void startGame(final GameStartEvent event)
    {
        final LocalArena arena = event.getArena();
        final ElytraRaceArena arenaData = arena.getArenaData();

        for (final Boost boost : arenaData.getArenaConfig().getBoosts())
        {
            final Cuboid boostArea = boost.getArea().toCuboid(arena.getWorld().getCurrentWorld());
            final ITrackedRegion boostRegion = arena.getRegionManager().create(boostArea);

            boostRegion.whenEnter(player -> this.boostPlayer(player, boost));
        }
    }

    private void boostPlayer(final Player player, final Boost boost)
    {
        final Vector playerVector = player.getLocation().getDirection();

        if (boost.getHeightPower() != null)
        {
            playerVector.setY(boost.getHeightPower());
        }

        final Double speedPower = boost.getSpeedPower();
        if (speedPower != null)
        {
            playerVector.setX(playerVector.getX() * speedPower);
            playerVector.setZ(playerVector.getZ() * speedPower);
        }

        player.setVelocity(playerVector);

        player.playSound(player.getLocation(), Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 1, 0);
    }
}
