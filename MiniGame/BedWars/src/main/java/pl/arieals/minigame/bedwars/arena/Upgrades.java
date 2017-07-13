package pl.arieals.minigame.bedwars.arena;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.upgrade.IUpgrade;

public class Upgrades
{
    private final LocalArena arena;
    private final Team       team;
    private final Map<IUpgrade, Integer> upgrades;

    public Upgrades(final LocalArena arena, final Team team)
    {
        this.arena = arena;
        this.team = team;
        this.upgrades = new HashMap<>();
    }

    public int getUpgradeLevel(final IUpgrade upgrade)
    {
        return this.upgrades.getOrDefault(upgrade, 0);
    }

    public boolean installUpgrade(final IUpgrade upgrade)
    {
        final int currentLevel = this.getUpgradeLevel(upgrade);
        if (currentLevel >= upgrade.maxLevel())
        {
            return false;
        }

        final int newLevel = currentLevel + 1;

        this.upgrades.put(upgrade, newLevel);
        upgrade.apply(this.arena, this.team, newLevel);

        return true;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("upgrades", this.upgrades).toString();
    }
}
