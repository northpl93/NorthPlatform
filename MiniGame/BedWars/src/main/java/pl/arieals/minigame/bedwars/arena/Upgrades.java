package pl.arieals.minigame.bedwars.arena;

import java.util.HashMap;
import java.util.Map;

import pl.arieals.minigame.bedwars.arena.upgrade.IUpgrade;

public class Upgrades
{
    private final Team team;
    private final Map<IUpgrade, Integer> upgrades;

    public Upgrades(final Team team)
    {
        this.team = team;
        this.upgrades = new HashMap<>();
    }

    public int getUpgradeLevel(final IUpgrade upgrade)
    {
        return this.upgrades.getOrDefault(upgrade, 0);
    }

    public void installUpgrade()
    {

    }
}
