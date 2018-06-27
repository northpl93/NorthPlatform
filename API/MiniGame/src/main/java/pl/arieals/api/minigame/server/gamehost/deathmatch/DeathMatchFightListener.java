package pl.arieals.api.minigame.server.gamehost.deathmatch;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.north93.zgame.api.global.utils.lang.JavaUtils.instanceOf;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

public class DeathMatchFightListener implements AutoListener
{
    @EventHandler(ignoreCancelled = true)
    public void blockDamageBeforeDeathMatchStart(final EntityDamageEvent event)
    {
        final Player player = instanceOf(event.getEntity(), Player.class);
        if (player == null)
        {
            return;
        }

        final LocalArena arena = getArena(player);
        if (arena == null)
        {
            return;
        }
        if (arena.getDeathMatch().getState().isActive() && !arena.getDeathMatch().isFightActive())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFight(final EntityDamageByEntityEvent event)
    {
        final Player damager = instanceOf(event.getDamager(), Player.class);
        if (damager == null)
        {
            return;
        }
        final LocalArena damagerArena = getArena(damager);

        if (damagerArena == null)
        {
            return;
        }
        if (damagerArena.getDeathMatch().getState().isActive() && !damagerArena.getDeathMatch().isFightActive())
        {
            event.setCancelled(true);
        }
    }
}
