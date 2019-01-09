package pl.arieals.minigame.bedwars.shop.upgrade;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerStatus;


import com.destroystokyo.paper.Title;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.region.ITrackedRegion;
import pl.arieals.api.minigame.shared.api.PlayerStatus;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.TranslatableString;

public class IslandTrapUpgrade implements IUpgrade
{
    private final MessagesBox bedWarsMessages;

    private IslandTrapUpgrade(final @Messages("BedWars") MessagesBox bedWarsMessages) // powered by smartexecutor
    {
        this.bedWarsMessages = bedWarsMessages;
    }

    @Override
    public void apply(final LocalArena arena, final Team team, final int level)
    {
        final ITrackedRegion region = arena.getRegionManager().create(team.getTeamArena());
        region.whenEnter(player -> this.onEnter(region, team, player));
    }

    private void onEnter(final ITrackedRegion region, final Team owningTeam, final INorthPlayer player)
    {
        final PlayerStatus playerStatus = getPlayerStatus(player);
        if (playerStatus != null && playerStatus.isSpectator())
        {
            // nic nie robimy spectatorom
            return;
        }

        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
        if (playerData == null || owningTeam == playerData.getTeam())
        {
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));

        this.announceRemoved(owningTeam);
        owningTeam.getUpgrades().removeUpgrade(this); // to ulepszenie usuwa sie po uzyciu
        region.unTrack();
    }

    private void announceRemoved(final Team team)
    {
        for (final BedWarsPlayer playerData : team.getPlayers())
        {
            if (playerData.isEliminated())
            {
                continue;
            }

            final Player player = playerData.getBukkitPlayer();

            final BaseComponent title = TranslatableString.of(this.bedWarsMessages, "@trap_used.title").getValue(player);
            final BaseComponent subtitle = TranslatableString.of(this.bedWarsMessages, "@trap_used.subtitle").getValue(player);

            player.sendTitle(new Title(title, subtitle, 0, 50, 10));
        }
    }

    @Override
    public int maxLevel()
    {
        return 1;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
