package pl.north93.northplatform.minigame.bedwars.shop.upgrade;

import org.bukkit.Material;
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

public class RoadOfWarrior implements IUpgrade, Listener
{
    private final BwConfig bwConfig;

    // system agregacji wspiera SmartExecutora
    private RoadOfWarrior(final BwConfig config, final IBukkitServerManager serverManager)
    {
        this.bwConfig = config;
        serverManager.registerEvents(this);
    }

    @Override
    public void apply(final LocalArena arena, final Team team, final int level)
    {
        for (final Player player : team.getBukkitPlayers())
        {
            for (final ItemStack itemStack : player.getInventory().getContents())
            {
                if (itemStack == null || ! this.isSword(itemStack.getType()))
                {
                    continue;
                }
                this.apply(itemStack, level);
            }
        }
    }

    @Override
    public String getLoreDescription(final MessagesBox messagesBox, final Team team, final Player player)
    {
        final String sharpnessLevel = DioriteMathUtils.toRoman(Math.min(team.getUpgrades().getUpgradeLevel(this) + 1, this.maxLevel()));
        return messagesBox.getString(player.getLocale(), "upgrade_gui.RoadOfWarrior.lore", sharpnessLevel);
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemBuy(final ItemBuyEvent event)
    {
        final int upgradeLevel = this.getUpgradeLevel(event.getPlayer());
        if (upgradeLevel == 0)
        {
            return;
        }

        for (final ItemStack itemStack : event.getItems())
        {
            final Material type = itemStack.getType();
            if (! this.isSword(type))
            {
                continue;
            }
            this.apply(itemStack, upgradeLevel);
        }
    }

    private boolean isSword(final Material type)
    {
        return type == Material.WOOD_SWORD || type == Material.STONE_SWORD || type == Material.IRON_SWORD || type == Material.GOLD_SWORD || type == Material.DIAMOND_SWORD;
    }

    public void apply(final ItemStack itemStack, final int level)
    {
        itemStack.addEnchantment(Enchantment.DAMAGE_ALL, level);
    }

    @Override
    public int maxLevel()
    {
        if (this.bwConfig.getTeamSize() == 4)
        {
            return 3;
        }
        return 1;
    }
}
