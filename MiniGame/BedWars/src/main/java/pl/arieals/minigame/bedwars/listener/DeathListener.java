package pl.arieals.minigame.bedwars.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;
import static pl.north93.zgame.api.bukkit.utils.ChatUtils.translateAlternateColorCodes;
import static pl.north93.zgame.api.global.utils.JavaUtils.instanceOf;


import com.destroystokyo.paper.Title;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.Vector;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.arena.DeathMatchState;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.RevivePlayerCountdown;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.event.TeamEliminatedEvent;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class DeathListener implements Listener
{
    @Inject
    private BukkitApiCore apiCore;
    @Inject @Messages("BedWars")
    private MessagesBox   messages;

    @EventHandler
    public void onPlayerHitPlayer(final EntityDamageByEntityEvent event)
    {
        final Player player = instanceOf(event.getEntity(), Player.class);
        final Player damager = instanceOf(event.getDamager(), Player.class);
        if (player == null || damager == null)
        {
            return;
        }

        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
        final BedWarsPlayer damagerData = getPlayerData(damager, BedWarsPlayer.class);

        if (playerData.getTeam() == damagerData.getTeam())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAnyDamage(final EntityDamageEvent event)
    {
        final Player player = instanceOf(event.getEntity(), Player.class);
        if (player == null)
        {
            return;
        }

        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
        if (! playerData.isAlive())
        {
            // wylaczamy damage jesli gracz nie zyje, zeby uniknac bugów itd
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event)
    {
        event.setDeathMessage(null); // my wcale nie umieramy!
        final Player player = event.getEntity();

        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
        final LocalArena arena = getArena(player);
        final Team team = playerData.getTeam();
        if (team == null)
        {
            return;
        }

        player.setHealth(20);
        player.setVisible(false);
        player.setAllowFlight(true);
        player.setFlying(true);

        final Vector direction = player.getLocation().getDirection();
        final Vector newVector = direction.multiply(- 1).setY(2);
        player.setVelocity(newVector);

        playerData.setAlive(false);
        if (team.isBedAlive())
        {
            new RevivePlayerCountdown(player, playerData).start(20);
        }
        else
        {
            if (! team.isTeamAlive())
            {
                this.apiCore.callEvent(new TeamEliminatedEvent(arena, team));
            }

            final String locale = player.spigot().getLocale();
            final String title = translateAlternateColorCodes(this.messages.getMessage(locale, "die.norespawn.title"));
            final String subtitle = translateAlternateColorCodes(this.messages.getMessage(locale, "die.norespawn.subtitle"));

            player.sendTitle(new Title(title, subtitle, 20, 20, 20));
        }

        final EntityDamageEvent lastDamageCause = event.getEntity().getLastDamageCause();
        if (lastDamageCause != null && lastDamageCause.getCause() == EntityDamageEvent.DamageCause.VOID)
        {
            // jesli gracz umarl w voidzie to teleportujemy go na spawn
            final DeathMatchState deathmatchState = arena.getDeathMatch().getState();
            if (deathmatchState.isStarted())
            {
                player.teleport(arena.getDeathMatch().getArenaSpawn());
            }
            else
            {
                player.teleport(team.getSpawn());
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
