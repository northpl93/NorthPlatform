package pl.north93.zgame.skyblock.server.gui;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.north93.zgame.api.bukkit.windows.Window;
import pl.north93.zgame.skyblock.api.cfg.IslandConfig;

public class IslandTypePicker extends Window
{
    private final List<IslandConfig> islands;
    private final Consumer<String>   callback;

    public IslandTypePicker(final String title, final List<IslandConfig> islands, final Consumer<String> callback)
    {
        super(title, 9);
        this.islands = islands;
        this.callback = callback;
    }

    @Override
    protected void onShow()
    {
        for (int i = 0; i < this.islands.size(); i++)
        {
            final IslandConfig islandConfig = this.islands.get(i);

            final ItemStack stack = new ItemStack(Material.GRASS, i + 1);
            final ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setDisplayName(islandConfig.getName());
            final int bok = islandConfig.getRadius() * 2;
            itemMeta.setLore(Arrays.asList("Rozmiar: " + bok + "x" + bok));
            stack.setItemMeta(itemMeta);

            this.addElement(i, stack, window -> {
                window.close();
                this.callback.accept(islandConfig.getName());
            });
        }
    }
}
