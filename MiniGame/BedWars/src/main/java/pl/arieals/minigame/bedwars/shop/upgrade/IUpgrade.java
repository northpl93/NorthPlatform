package pl.arieals.minigame.bedwars.shop.upgrade;

import java.util.Map;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.cfg.BwConfig;
import pl.north93.zgame.api.global.messages.MessagesBox;

public interface IUpgrade
{
    default String getName()
    {
        return this.getClass().getSimpleName();
    }

    default Integer getPrice(final BwConfig bwConfig, final Team team)
    {
        final Map<String, Integer> upgrades = bwConfig.getUpgrades();
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
        return messagesBox.getMessage(player.spigot().getLocale(), "upgrade_gui." + this.getName() + ".lore");
    }

    void apply(LocalArena arena, Team team, int level);

    int maxLevel();
}
