package pl.arieals.minigame.bedwars.shop.upgrade;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import org.diorite.utils.math.DioriteMathUtils;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.cfg.BwConfig;
import pl.arieals.minigame.bedwars.event.ItemBuyEvent;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class ArmorProtection implements IUpgrade, Listener
{
    private final BwConfig bwConfig;

    // system agregacji wspiera SmartExecutora, wiec ten konstruktor zadziala
    private ArmorProtection(final BukkitApiCore apiCore, final BwConfig bwConfig)
    {
        this.bwConfig = bwConfig;
        apiCore.registerEvents(this);
    }

    @Override
    public void apply(final LocalArena arena, final Team team, final int level)
    {
        for (final Player player : team.getPlayers())
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
        return messagesBox.getMessage(player.spigot().getLocale(), "upgrade_gui.ArmorProtection.lore", sharpnessLevel);
    }

    @EventHandler
    public void newArmorHandler(final ItemBuyEvent event)
    {
        final String specialHandler = event.getShopEntry().getSpecialHandler();
        if (specialHandler == null || ! specialHandler.equals("ArmorEntry"))
        {
            return;
        }

        final ItemStack itemStack = event.getPlayer().getInventory().getArmorContents()[3]; // czapka
        if (itemStack == null)
        {
            return;
        }

        final int level = itemStack.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
        if (level == 0)
        {
            return;
        }

        for (final ItemStack stack : event.getItems())
        {
            stack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level);
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
