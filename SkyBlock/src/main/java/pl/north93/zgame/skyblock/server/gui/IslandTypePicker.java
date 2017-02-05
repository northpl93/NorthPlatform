package pl.north93.zgame.skyblock.server.gui;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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

            this.addElement(i, stack, event -> {
                event.getWindow().close();
                this.callback.accept(islandConfig.getName());
            });
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("islands", this.islands).toString();
    }
}
