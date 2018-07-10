package pl.arieals.minigame.bedwars.shop.upgrade;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import java.util.Map;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.cfg.BwShopConfig;
import pl.north93.zgame.api.global.messages.MessagesBox;

public interface IUpgrade
{
    default String getName()
    {
        return this.getClass().getSimpleName();
    }

    default Integer getPrice(final BwShopConfig bwShopConfig, final Team team)
    {
        final Map<String, Integer> upgrades = bwShopConfig.getUpgrades();
        final String name = this.getName();

        if (upgrades.containsKey(name))
        {
            return upgrades.get(name);
        }

        final int nextLevel = team.getUpgrades().getUpgradeLevel(this) + 1;
        return upgrades.get(this.getName() + "_" + nextLevel);
    }

    default String getLoreDescription(final MessagesBox messagesBox, final Team team, final Player player)
    {
        return messagesBox.getString(player.getLocale(), "upgrade_gui." + this.getName() + ".lore");
    }

    void apply(LocalArena arena, Team team, int level);

    int maxLevel();

    default int getUpgradeLevel(final Player player)
    {
        final BedWarsPlayer playerData = getPlayerData(player, BedWarsPlayer.class);
        if (playerData == null || playerData.getTeam() == null)
        {
            return 0;
        }

        return playerData.getTeam().getUpgrades().getUpgradeLevel(this);
    }
}
