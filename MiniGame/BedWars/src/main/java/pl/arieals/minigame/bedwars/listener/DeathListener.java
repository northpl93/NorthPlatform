package pl.arieals.minigame.bedwars.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.setPlayerStatus;
import static pl.north93.zgame.api.bukkit.utils.ChatUtils.translateAlternateColorCodes;
import static pl.north93.zgame.api.global.utils.JavaUtils.instanceOf;


import java.time.Duration;

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
import pl.arieals.api.minigame.server.gamehost.reward.CurrencyReward;
import pl.arieals.api.minigame.shared.api.PlayerStatus;
import pl.arieals.api.minigame.shared.api.arena.DeathMatchState;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.RevivePlayerCountdown;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.event.TeamEliminatedEvent;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.utils.dmgtracker.DamageContainer;
import pl.north93.zgame.api.bukkit.utils.dmgtracker.DamageEntry;
import pl.north93.zgame.api.bukkit.utils.dmgtracker.DamageTracker;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.players.Identity;

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

        if (playerData == null || damagerData == null || playerData.getTeam() == damagerData.getTeam())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event)
    {
        final Player player = event.getEntity();

        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
        final LocalArena arena = getArena(player);
        final Team team = playerData.getTeam();
        if (team == null)
        {
            return;
        }

        player.setHealth(20);
        setPlayerStatus(player, PlayerStatus.PLAYING_SPECTATOR);

        final Vector direction = player.getLocation().getDirection();
        final Vector newVector = direction.multiply(- 1).setY(2);
        player.setVelocity(newVector);

        this.handleKiller(event, arena, team); // podbija licznik zabojstw, wysyla wiadomosc i daje nagrode zabojcy
        this.handleRespawn(player, playerData, team);
        this.safePlaceTeleport(player, team, arena);

        if (! team.isTeamAlive())
        {
            // team wyeliminowany tym zabojstwem, wywolujemy event
            this.apiCore.callEvent(new TeamEliminatedEvent(arena, team));
        }
    }

    private void handleRespawn(final Player player, final BedWarsPlayer playerData, final Team team)
    {
        playerData.setAlive(false);
        if (team.isBedAlive())
        {
            new RevivePlayerCountdown(player, playerData).start(20);
            return;
        }

        final String locale = player.spigot().getLocale();
        final String title = translateAlternateColorCodes(this.messages.getMessage(locale, "die.norespawn.title"));
        final String subtitle = translateAlternateColorCodes(this.messages.getMessage(locale, "die.norespawn.subtitle"));
        player.sendTitle(new Title(title, subtitle, 20, 20, 20));
    }

    private void safePlaceTeleport(final Player player, final Team team, final LocalArena arena)
    {
        final EntityDamageEvent lastDamageCause = player.getLastDamageCause();
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

    private void handleKiller(final PlayerDeathEvent event, final LocalArena arena, final Team team)
    {
        event.setDeathMessage(null);
        final Player player = event.getEntity();
        final boolean elimination = ! team.isBedAlive();

        final DamageContainer dmgContainer = DamageTracker.get().getContainer(player);
        final DamageEntry lastDmg = dmgContainer.getLastDamageByPlayer(Duration.ofSeconds(10));

        if (lastDmg == null)
        {
            return;
        }

        final Player damager = (Player) lastDmg.getCauseByEntity().getDamager();
        final BedWarsPlayer damagerData = getPlayerData(damager, BedWarsPlayer.class);
        damagerData.incrementKills(); // dodajemy zabojcy killa
        arena.getRewards().addReward(Identity.of(damager), new CurrencyReward("elimination", "minigame", 100));

        if (elimination)
        {
            arena.getPlayersManager().broadcast(this.messages,
                    "die.broadcast.eliminated_by",
                    team.getColorChar(),
                    player.getDisplayName(),
                    damagerData.getTeam().getColorChar(),
                    damager.getDisplayName());
        }
        else
        {
            arena.getPlayersManager().broadcast(this.messages,
                    "die.broadcast.killed_by",
                    team.getColorChar(),
                    player.getDisplayName(),
                    damagerData.getTeam().getColorChar(),
                    damager.getDisplayName());
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
