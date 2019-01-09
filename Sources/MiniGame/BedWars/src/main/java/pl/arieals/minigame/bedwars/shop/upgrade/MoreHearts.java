package pl.arieals.minigame.bedwars.shop.upgrade;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.cfg.BwConfig;

public class MoreHearts implements IUpgrade
{
    private static final double BASE_PLAYER_HEALTH = 20D;
    private final BwConfig bwConfig;

    private MoreHearts(final BwConfig bwConfig)
    {
        this.bwConfig = bwConfig;
    }

    @Override
    public void apply(final LocalArena arena, final Team team, final int level)
    {
        for (final Player player : team.getBukkitPlayers())
        {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(this.calculateMaxHp(level));
        }
    }

    private double calculateMaxHp(final int level)
    {
        return BASE_PLAYER_HEALTH + (level * 2);
    }

    @Override
    public int maxLevel()
    {
        if (this.bwConfig.getTeamSize() == 4)
        {
            return 4;
        }
        return 2;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
