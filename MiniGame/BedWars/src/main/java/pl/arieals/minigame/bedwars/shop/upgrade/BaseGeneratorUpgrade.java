package pl.arieals.minigame.bedwars.shop.upgrade;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.arena.generator.GeneratorController;
import pl.arieals.minigame.bedwars.cfg.BwConfig;
import pl.arieals.minigame.bedwars.cfg.BwGeneratorItemConfig;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class BaseGeneratorUpgrade implements IUpgrade
{
    @Inject
    private BwConfig config;

    @Override
    public void apply(final LocalArena arena, final Team team, final int level)
    {
        final BedWarsArena arenaData = arena.getArenaData();
        final GeneratorController generator = this.findGenerator(arenaData, team);

        for (final BwGeneratorItemConfig itemConfig : generator.getGeneratorType().getItems())
        {
            if (itemConfig.getName() == null || ! itemConfig.getName().equals("upgrade_" + level))
            {
                continue;
            }

            final BwGeneratorItemConfig newConfig = new BwGeneratorItemConfig(itemConfig.getName(), itemConfig.getMaterial(), itemConfig.getData(), itemConfig.getAmount(), itemConfig.getEvery(), false, level);
            generator.addNewEntry(newConfig);
        }
    }

    @Override
    public String getLoreDescription(final MessagesBox messagesBox, final Team team, final Player player)
    {
        final int nextLevel = Math.min(team.getUpgrades().getUpgradeLevel(this) + 1, this.maxLevel());
        if (nextLevel < 3)
        {
            return messagesBox.getMessage(player.spigot().getLocale(), "upgrade_gui.BaseGeneratorUpgrade.lore");
        }
        else if (nextLevel == 3)
        {
            return messagesBox.getMessage(player.spigot().getLocale(), "upgrade_gui.BaseGeneratorUpgrade.lore3");
        }
        return messagesBox.getMessage(player.spigot().getLocale(), "upgrade_gui.BaseGeneratorUpgrade.lore4");
    }

    @Override
    public int maxLevel()
    {
        if (this.config.getTeamSize() == 4)
        {
            return 4;
        }
        return 2;
    }

    private GeneratorController findGenerator(final BedWarsArena arena, final Team team)
    {
        for (final GeneratorController generator : arena.getGenerators())
        {
            if (team.getTeamArena().contains(generator.getLocation()))
            {
                return generator;
            }
        }

        throw new RuntimeException("Can't find team's generator");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
