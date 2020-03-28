package pl.arieals.minigame.bedwars.listener;

import static pl.north93.northplatform.api.global.utils.lang.JavaUtils.instanceOf;
import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getPlayerStatus;
import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.setPlayerStatus;


import java.time.Duration;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.RevivePlayerCountdown;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.shop.elimination.IEliminationEffect;
import pl.arieals.minigame.bedwars.shop.stattrack.StatTrackManager;
import pl.arieals.minigame.bedwars.shop.stattrack.TrackedStatistic;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.utils.dmgtracker.DamageContainer;
import pl.north93.northplatform.api.bukkit.utils.dmgtracker.DamageEntry;
import pl.north93.northplatform.api.bukkit.utils.dmgtracker.DamageTracker;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.reward.CurrencyReward;
import pl.north93.northplatform.api.minigame.shared.api.PlayerStatus;
import pl.north93.northplatform.api.minigame.shared.api.arena.DeathMatchState;

@Slf4j
public class DeathListener implements Listener
{
    @Inject
    private StatTrackManager statTrackManager;
    @Inject @Messages("BedWars")
    private MessagesBox      messages;

    @EventHandler
    public void onVoidDamage(final EntityDamageEvent event)
    {
        final Player bukkitPlayer = instanceOf(event.getEntity(), Player.class);
        if (bukkitPlayer == null)
        {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.VOID)
        {
            return;
        }

        final INorthPlayer player = INorthPlayer.wrap(bukkitPlayer);

        final PlayerStatus playerStatus = getPlayerStatus(player);
        if (playerStatus == PlayerStatus.PLAYING)
        {
            event.setDamage(Integer.MAX_VALUE);
        }
    }

    @EventHandler
    public void onPlayerHitPlayer(final EntityDamageByEntityEvent event)
    {
        final Player bukkitVictim = instanceOf(event.getEntity(), Player.class);
        final Player bukkitDamager = new DamageEntry(event, null).getPlayerDamager(); // pozyczylismy sobie kod z damagetrackera
        if (bukkitVictim == null || bukkitDamager == null)
        {
            return;
        }

        final INorthPlayer victim = INorthPlayer.wrap(bukkitVictim);
        final INorthPlayer damager = INorthPlayer.wrap(bukkitDamager);

        final BedWarsPlayer playerData = victim.getPlayerData(BedWarsPlayer.class);
        final BedWarsPlayer damagerData = damager.getPlayerData(BedWarsPlayer.class);

        if (playerData == null || damagerData == null || playerData.getTeam() == damagerData.getTeam())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event)
    {
        final INorthPlayer player = INorthPlayer.wrap(event.getEntity());
        final LocalArena arena = getArena(player);
        final BedWarsPlayer playerData = player.getPlayerData(BedWarsPlayer.class);

        final Team team = playerData.getTeam();
        if (arena == null || team == null)
        {
            return;
        }

        log.info("Player {} death on arena {}", player.getName(), arena.getId());

        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        setPlayerStatus(player, PlayerStatus.PLAYING_SPECTATOR);

        // usuwamy wszystkie efekty potionek, upgradey zadbaja zeby je oddac
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

        this.handleKiller(event, arena, playerData); // podbija licznik zabojstw, wysyla wiadomosc i daje nagrode zabojcy
        this.handleRespawn(player, arena, playerData); // zmniejsza licznik zycia, uruchamia task respawnujacy, wysyla title
        this.safePlaceTeleport(player, team, arena);

        team.checkEliminated();
    }

    private void handleRespawn(final INorthPlayer player, final LocalArena arena, final BedWarsPlayer playerData)
    {
        final Team team = playerData.getTeam();
        if (team.isBedAlive())
        {
            new RevivePlayerCountdown(player, playerData).start(20);
            return;
        }
        else
        {
            final DeathMatchState deathMatchState = arena.getDeathMatch().getState();

            // gdy gracz ma zycie i deathmatch nie jest wlaczony to zabieramy zycie i normalnie respawnimy
            if (playerData.getLives() > 0 && deathMatchState == DeathMatchState.NOT_STARTED)
            {
                playerData.removeLife();
                new RevivePlayerCountdown(player, playerData).start(20);
                return;
            }
        }

        playerData.eliminate();
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

    private void handleKiller(final PlayerDeathEvent event, final LocalArena arena, final BedWarsPlayer deathData)
    {
        event.setDeathMessage(null);

        final INorthPlayer player = INorthPlayer.wrap(event.getEntity());
        final Team team = deathData.getTeam();

        final DamageContainer dmgContainer = DamageTracker.get().getContainer(player);
        final DamageEntry lastDmg = dmgContainer.getLastDamageByPlayer(Duration.ofSeconds(10));

        if (lastDmg == null)
        {
            return;
        }

        final INorthPlayer damager = INorthPlayer.wrap(lastDmg.getPlayerDamager());
        assert damager != null; // damager nie moze byc tu nullem bo uzywamy getLastDamageByPlayer

        final BedWarsPlayer damagerData = damager.getPlayerData(BedWarsPlayer.class);
        if (damagerData == null || damagerData.isEliminated())
        {
            // jesli damager jest wyeliminowany to nie uznajemy go za zabojce
            return;
        }

        final IEliminationEffect eliminationEffect = damagerData.getEliminationEffect();
        if (eliminationEffect != null)
        {
            // odtwarzamy animacje zabicia gracza
            eliminationEffect.playerEliminated(arena, player, damager);
        }

        lastDmg.getTool().ifPresent(tool ->
        {
            // dodajemy zabojcy killa w systemia stattrak, jesli narzedzie nie jest nullem
            this.statTrackManager.bumpStatistic(damager, TrackedStatistic.KILLS, tool);
        });

        // dodajemy zabojcy killa w obiekcie BedWarsPlayer
        damagerData.incrementKills();

        // jesli gracz ma 0 i mniej zycia, a lozko jest zniszczone to nastapila eliminacja
        final boolean elimination = deathData.getLives() <= 0 && ! team.isBedAlive();

        final BedWarsArena bedWarsArena = arena.getArenaData();
        if (elimination)
        {
            final int currencyAmount = bedWarsArena.getBedWarsConfig().getRewards().getElimination();
            arena.getRewards().addReward(Identity.of(damager), new CurrencyReward("elimination", "minigame", currencyAmount));
        }
        else
        {
            final int currencyAmount = bedWarsArena.getBedWarsConfig().getRewards().getKill();
            arena.getRewards().addReward(Identity.of(damager), new CurrencyReward("kill", "minigame", currencyAmount));
        }

        final String deathMessageKey = this.getDeathMessageKey(player, elimination);
        if (elimination)
        {
            arena.getChatManager().broadcast(this.messages, deathMessageKey,
                    team.getColor(),
                    player.getDisplayName(),
                    damagerData.getTeam().getColor(),
                    damager.getDisplayName());
        }
        else
        {
            arena.getChatManager().broadcast(this.messages, deathMessageKey,
                    team.getColor(),
                    player.getDisplayName(),
                    damagerData.getTeam().getColor(),
                    damager.getDisplayName());
        }
    }

    private String getDeathMessageKey(final Player deathPlayer, final boolean elimination)
    {
        final StringBuilder builder = new StringBuilder("die.broadcast.");

        if (deathPlayer.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.VOID)
        {
            builder.append("fall.");
        }
        else
        {
            builder.append("kill.");
        }

        builder.append(elimination ? "eliminated_by" : "killed_by");

        return builder.toString();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
