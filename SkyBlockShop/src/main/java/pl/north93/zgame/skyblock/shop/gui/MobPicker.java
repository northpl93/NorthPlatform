package pl.north93.zgame.skyblock.shop.gui;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.north93.zgame.api.bukkit.windows.Window;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.skyblock.shop.SpawnerComponent;
import pl.north93.zgame.skyblock.shop.cfg.SpawnerEntryConfig;

import java.util.List;


public class MobPicker extends Window
{
    @Inject
    private SpawnerComponent spawnerComponent;

    private final List<SpawnerEntryConfig> mobs;
    private final Block spawner;

    public MobPicker(final List<SpawnerEntryConfig> mobs, Block spawner)
    {
        super("Wybierz Moba", ((mobs.size() / 9) + 1) * 9);
        this.mobs = mobs;
        this.spawner = spawner;
    }

    @Override
    protected void onShow()
    {
        int id = 0;
        for(final SpawnerEntryConfig mob : this.mobs)
        {
            final ItemStack representingItem = mob.getRepresentingItem();
            final ItemMeta representingItemMeta = representingItem.getItemMeta();

            representingItemMeta.setDisplayName(mob.getDisplayName());
            representingItem.setItemMeta(representingItemMeta);

            this.addElement(id++, representingItem, event -> this.pickMob(mob));
        }
    }

    private void pickMob(final SpawnerEntryConfig mob)
    {
        if(this.spawnerComponent.getSpawnerManager().changeMob(spawner, this.getPlayer(), mob))
        {
            this.close();
        }
    }
}
