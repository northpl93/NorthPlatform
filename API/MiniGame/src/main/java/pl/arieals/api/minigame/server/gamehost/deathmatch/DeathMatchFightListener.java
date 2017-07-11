package pl.arieals.api.minigame.server.gamehost.deathmatch;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.north93.zgame.api.global.utils.JavaUtils.instanceOf;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import pl.arieals.api.minigame.server.gamehost.arena.DeathMatch;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;

public class DeathMatchFightListener implements Listener
{
    @EventHandler
    public void onFight(final EntityDamageByEntityEvent event)
    {
        final Player player = instanceOf(event.getEntity(), Player.class);
        final Player damager = instanceOf(event.getDamager(), Player.class);
        if (player == null || damager == null)
        {
            return;
        }

        final LocalArena attackerArena = getArena(player);
        final LocalArena damagerArena = getArena(damager);

        if (attackerArena != damagerArena)
        {
            return;
        }

        final DeathMatch deathMatch = attackerArena.getDeathMatch();
        if (! deathMatch.getState().isActive())
        {
            return;
        }

        if (! deathMatch.isFightActive())
        {
            event.setCancelled(true);
        }
    }
}
