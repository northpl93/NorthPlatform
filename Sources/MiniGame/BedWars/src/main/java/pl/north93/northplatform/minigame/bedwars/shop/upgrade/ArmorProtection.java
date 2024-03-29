package pl.north93.northplatform.minigame.bedwars.shop.upgrade;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import org.diorite.commons.math.DioriteMathUtils;

import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.minigame.bedwars.arena.Team;
import pl.north93.northplatform.minigame.bedwars.cfg.BwConfig;
import pl.north93.northplatform.minigame.bedwars.event.ItemBuyEvent;

public class ArmorProtection implements IUpgrade, Listener
{
    private final BwConfig bwConfig;

    // system agregacji wspiera SmartExecutora, wiec ten konstruktor zadziala
    private ArmorProtection(final IBukkitServerManager serverManager, final BwConfig bwConfig)
    {
        this.bwConfig = bwConfig;
        serverManager.registerEvents(this);
    }

    @Override
    public void apply(final LocalArena arena, final Team team, final int level)
    {
        for (final Player player : team.getBukkitPlayers())
        {
            for (final ItemStack itemStack : player.getInventory().getArmorContents())
            {
                if (itemStack == null)
                {
                    continue;
                }

                itemStack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level);
            }
        }
    }

    @Override
    public String getLoreDescription(final MessagesBox messagesBox, final Team team, final Player player)
    {
        final String sharpnessLevel = DioriteMathUtils.toRoman(Math.min(team.getUpgrades().getUpgradeLevel(this) + 1, this.maxLevel()));
        return messagesBox.getString(player.getLocale(), "upgrade_gui.ArmorProtection.lore", sharpnessLevel);
    }

    @EventHandler(ignoreCancelled = true)
    public void newArmorHandler(final ItemBuyEvent event)
    {
        final String specialHandler = event.getShopEntry().getSpecialHandler();
        if (specialHandler == null || ! specialHandler.equals("ArmorEntry"))
        {
            return;
        }

        final int upgradeLevel = this.getUpgradeLevel(event.getPlayer());
        if (upgradeLevel <= 0)
        {
            return;
        }

        for (final ItemStack stack : event.getItems())
        {
            stack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, upgradeLevel);
        }
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
}
