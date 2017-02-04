package pl.north93.zgame.itemshop.server.gui;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.windows.Window;
import pl.north93.zgame.itemshop.shared.DataEntry;
import pl.north93.zgame.itemshop.shared.ReceiveStorage;

public class ReceiveGui extends Window
{
    private final ReceiveStorage  receiveStorage;
    private final List<DataEntry> dataEntries;

    public ReceiveGui(final ReceiveStorage receiveStorage, final List<DataEntry> dataEntries)
    {
        super("Przedmioty z ItemShopu", 6 * 9);
        this.receiveStorage = receiveStorage;
        this.dataEntries = dataEntries;
    }

    private void receive(final ItemStack itemStack, final DataEntry dataEntry)
    {
        final PlayerInventory inventory = this.getPlayer().getInventory();
        final boolean isSuccess = inventory.addItem(itemStack).isEmpty();
        if (isSuccess)
        {
            this.close();
            this.receiveStorage.removeReceiveContent(this.getPlayer().getUniqueId(), dataEntry);
        }
    }

    @Override
    protected void onShow()
    {
        int i = 0;
        for (final DataEntry dataEntry : this.dataEntries)
        {
            final Map<String, String> properties = dataEntry.getProperties();
            switch (dataEntry.getDataType())
            {
                case ITEM:
                {
                    final Material material = Material.valueOf(properties.get("material"));
                    final int amount = Integer.parseInt(properties.get("amount"));
                    final short data = Short.parseShort(properties.get("data"));

                    final ItemStack itemStack = new ItemStack(material, amount, data);

                    this.addElement(i++, itemStack, event -> this.receive(itemStack, dataEntry));

                    break;
                }

                case HEAD:
                {
                    final int amount = properties.containsKey("amount") ? Integer.parseInt(properties.get("amount")) : 1;
                    final String owner = properties.get("owner");

                    final ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, amount, (short) 3);
                    final SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
                    itemMeta.setOwner(owner);
                    itemStack.setItemMeta(itemMeta);

                    this.addElement(i++, itemStack, event -> this.receive(itemStack, dataEntry));

                    break;
                }
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
