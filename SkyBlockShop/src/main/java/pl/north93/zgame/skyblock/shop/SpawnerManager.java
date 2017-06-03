package pl.north93.zgame.skyblock.shop;

import static org.bukkit.ChatColor.RED;


import java.text.MessageFormat;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.skyblock.shop.cfg.SpawnerEntryConfig;
import pl.north93.zgame.skyblock.shop.gui.MobPicker;

public class SpawnerManager
{
    private BukkitApiCore apiCore;
    @Inject
    private ShopComponent shopComponent;
    final private List<SpawnerEntryConfig> mobs;

    public SpawnerManager(final List<SpawnerEntryConfig> mobs)
    {
        this.mobs = mobs;
    }

    public void openMobPicker(final Player player, final Block spawner)
    {
        final MobPicker picker = new MobPicker(this.mobs, spawner);
        this.apiCore.getWindowManager().openWindow(player, picker);
    }

    public boolean changeMob(final Block spawner, final Player player, final SpawnerEntryConfig mob)
    {
        final Double balance = this.shopComponent.getShopManager().getBalance(player.getName());
        if(balance < mob.getPrice())
        {
            player.sendMessage(RED + "Nie posiadasz wymaganej kwoty do zmiany spawnera na " + mob.getDisplayName() + " ($" + mob.getPrice() + ")!");
            return false;
        }

        try {
            this.shopComponent.getShopManager().setMoney(player.getName(), balance - mob.getPrice());

            final BlockState state = spawner.getState();
            final CreatureSpawner cspawner = (CreatureSpawner) state;
            cspawner.setSpawnedType(mob.getMobEntityType());
            state.update();

            final String msg = "[SkyBlock.Shop] changeMob player={0},spawner={1},mob={2}";
            this.apiCore.getLogger().info(MessageFormat.format(msg, player, spawner, mob));

            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
