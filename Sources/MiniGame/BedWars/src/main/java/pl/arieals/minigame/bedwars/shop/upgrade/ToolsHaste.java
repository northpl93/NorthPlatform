package pl.arieals.minigame.bedwars.shop.upgrade;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import org.diorite.commons.math.DioriteMathUtils;

import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.event.PlayerRevivedEvent;
import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class ToolsHaste implements IUpgrade, Listener
{
    private ToolsHaste(final BukkitApiCore apiCore)
    {
        apiCore.registerEvents(this);
    }

    @Override
    public void apply(final LocalArena arena, final Team team, final int level)
    {
        for (final Player player : team.getBukkitPlayers())
        {
            final int potionLevel = level - 1;
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, potionLevel, true, false));
        }
    }

    @EventHandler
    public void onPlayerRevive(final PlayerRevivedEvent event)
    {
        final Player player = event.getPlayer();
        final int upgradeLevel = this.getUpgradeLevel(player);
        if (upgradeLevel == 0)
        {
            return;
        }

        final int potionLevel = upgradeLevel - 1;
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, potionLevel, true, false));
    }

    @Override
    public String getLoreDescription(final MessagesBox messagesBox, final Team team, final Player player)
    {
        final String hasteLevel = DioriteMathUtils.toRoman(Math.min(team.getUpgrades().getUpgradeLevel(this) + 1, this.maxLevel()));
        return messagesBox.getLegacyMessage(player.getLocale(), "upgrade_gui.ToolsHaste.lore", hasteLevel);
    }

    @Override
    public int maxLevel()
    {
        return 2;
    }
}
