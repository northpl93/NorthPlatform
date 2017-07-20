package pl.arieals.minigame.bedwars.shop.upgrade;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.cfg.BwConfig;
import pl.arieals.minigame.bedwars.event.ItemBuyEvent;
import pl.north93.zgame.api.bukkit.BukkitApiCore;

public class RoadOfWarrior implements IUpgrade, Listener
{
    private final BwConfig bwConfig;

    // system agregacji wspiera SmartExecutora
    private RoadOfWarrior(final BwConfig config, final BukkitApiCore apiCore)
    {
        this.bwConfig = config;
        apiCore.registerEvents(this);
    }

    @Override
    public void apply(final LocalArena arena, final Team team, final int level)
    {
        for (final Player player : team.getPlayers())
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

    @EventHandler
    public void onItemBuy(final ItemBuyEvent event)
    {
        final BedWarsPlayer playerData = getPlayerData(event.getPlayer(), BedWarsPlayer.class);
        final int upgradeLevel = playerData.getTeam().getUpgrades().getUpgradeLevel(this);

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

    private void apply(final ItemStack itemStack, final int level)
    {
        final int enchantLevel = this.bwConfig.getTeamSize() == 4 ? 3 : level;
        itemStack.addEnchantment(Enchantment.DAMAGE_ALL, enchantLevel);
    }

    @Override
    public int maxLevel()
    {
        if (this.bwConfig.getTeamSize() == 4)
        {
            return 1;
        }
        return 2;
    }
}
