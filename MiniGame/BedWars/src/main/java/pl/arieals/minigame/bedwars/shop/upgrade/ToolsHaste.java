package pl.arieals.minigame.bedwars.shop.upgrade;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import org.diorite.utils.math.DioriteMathUtils;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class ToolsHaste implements IUpgrade
{
    @Override
    public void apply(final LocalArena arena, final Team team, final int level)
    {
        for (final Player player : team.getPlayers())
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, level, true, false));
        }
    }

    @Override
    public String getLoreDescription(final MessagesBox messagesBox, final Team team, final Player player)
    {
        final String hasteLevel = DioriteMathUtils.toRoman(Math.min(team.getUpgrades().getUpgradeLevel(this) + 1, this.maxLevel()));
        return messagesBox.getMessage(player.spigot().getLocale(), "upgrade_gui.ToolsHaste.lore", hasteLevel);
    }

    @Override
    public int maxLevel()
    {
        return 2;
    }
}
