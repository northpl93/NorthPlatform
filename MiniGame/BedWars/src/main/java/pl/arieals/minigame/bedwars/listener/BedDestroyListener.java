package pl.arieals.minigame.bedwars.listener;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;
import static pl.arieals.minigame.bedwars.utils.PlayerTeamPredicates.isInTeam;
import static pl.arieals.minigame.bedwars.utils.PlayerTeamPredicates.notInTeam;


import com.destroystokyo.paper.Title;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.arena.PlayersManager;
import pl.arieals.api.minigame.server.gamehost.reward.CurrencyReward;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.event.BedDestroyedEvent;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.TranslatableString;
import pl.north93.zgame.api.global.network.players.Identity;

public class BedDestroyListener implements Listener
{
    @Inject
    private BukkitApiCore apiCore;
    @Inject @Messages("BedWars")
    private MessagesBox   messages;

    @EventHandler
    public void announceBedDestroy(final BedDestroyedEvent event)
    {
        if (event.isSilent())
        {
            return;
        }

        final LocalArena arena = event.getArena();
        final BedWarsArena bedWarsArena = arena.getArenaData();
        final Team team = event.getTeam();

        final PlayersManager playersManager = arena.getPlayersManager();
        if (event.getDestroyer() != null)
        {
            final BedWarsPlayer destroyerData = getPlayerData(event.getDestroyer(), BedWarsPlayer.class);
            assert destroyerData != null; // tu nie moze byc nullem

            final int currencyAmount = bedWarsArena.getBedWarsConfig().getRewards().getBedDestroy();
            arena.getRewards().addReward(Identity.of(event.getDestroyer()), new CurrencyReward("bedDestroy", "minigame", currencyAmount));

            playersManager.broadcast(isInTeam(team), this.messages,
                    "bed_destroyed.you", MessageLayout.SEPARATED,
                    team.getColor(),
                    destroyerData.getTeam().getColor(),
                    destroyerData.getBukkitPlayer().getDisplayName());

            final TranslatableString teamName = TranslatableString.of(this.messages, "@team.genitive." + team.getName());
            playersManager.broadcast(notInTeam(team), this.messages, "bed_destroyed.global", MessageLayout.SEPARATED, team.getColor(), teamName, destroyerData.getTeam().getColor(), destroyerData.getBukkitPlayer().getDisplayName());
        }
        else
        {
            playersManager.broadcast(isInTeam(team), this.messages,
                    "bed_destroyed.you_no_destroyer", MessageLayout.SEPARATED,
                    team.getColor());
        }

        for (final Player player : arena.getPlayersManager().getPlayers())
        {
            // globalny dzwiek niszczenia lozka
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 1, 1.5f); // volume, pitch
        }

        for (final Player player : team.getPlayers())
        {
            final String locale = player.getLocale();

            final String title = this.messages.getMessage(locale, "bed_destroyed.title.title");
            final String subtitle = this.messages.getMessage(locale, "bed_destroyed.title.subtitle");

            player.sendTitle(new Title(title, subtitle, 20, 20, 20));
        }
    }

    @EventHandler
    public void triggerTeamEliminationCheck(final BedDestroyedEvent event)
    {
        event.getTeam().checkEliminated();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
