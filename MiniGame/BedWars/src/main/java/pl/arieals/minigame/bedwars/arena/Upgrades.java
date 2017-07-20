package pl.arieals.minigame.bedwars.arena;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.shop.upgrade.IUpgrade;

public class Upgrades
{
    private final LocalArena             arena;
    private final Team                   team;
    private final Map<IUpgrade, Integer> installedUpgrades;

    public Upgrades(final LocalArena arena, final Team team)
    {
        this.arena = arena;
        this.team = team;
        this.installedUpgrades = new HashMap<>();
    }

    public int getUpgradeLevel(final IUpgrade upgrade)
    {
        return this.installedUpgrades.getOrDefault(upgrade, 0);
    }

    /**
     * Instaluje dany upgrade nie robiac przy tym zadnych checkow.
     * Uzywac ostroznie.
     *
     * @see pl.arieals.minigame.bedwars.shop.UpgradeManager
     * @param upgrade upgrade do zainstalowania/ulepszenia.
     */
    public void installUpgrade(final IUpgrade upgrade)
    {
        final int currentLevel = this.getUpgradeLevel(upgrade);
        final int newLevel = currentLevel + 1;

        this.installedUpgrades.put(upgrade, newLevel);
        upgrade.apply(this.arena, this.team, newLevel);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("installedUpgrades", this.installedUpgrades).toString();
    }
}
