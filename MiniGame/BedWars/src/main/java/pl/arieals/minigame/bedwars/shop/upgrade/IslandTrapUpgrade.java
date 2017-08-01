package pl.arieals.minigame.bedwars.shop.upgrade;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerStatus;


import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.region.ITrackedRegion;
import pl.arieals.api.minigame.shared.api.PlayerStatus;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.Team;

public class IslandTrapUpgrade implements IUpgrade
{
    @Override
    public void apply(final LocalArena arena, final Team team, final int level)
    {
        final ITrackedRegion region = arena.getRegionManager().create(team.getTeamArena());
        region.whenEnter(player -> this.onEnter(team, player));
    }

    private void onEnter(final Team owningTeam, final Player player)
    {
        final PlayerStatus playerStatus = getPlayerStatus(player);
        if (playerStatus != null && playerStatus.isSpectator())
        {
            // nic nie robimy spectatorom
            return;
        }

        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
        if (playerData == null)
        {
            return;
        }

        if (owningTeam != playerData.getTeam())
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
        }
    }

    @Override
    public int maxLevel()
    {
        return 1;
    }
}
