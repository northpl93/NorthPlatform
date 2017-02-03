package pl.north93.zgame.skyblock.shop.cfg;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.skyblock.shop.api.EntryType;
import pl.north93.zgame.skyblock.shop.api.IShopEntry;

public class ShopEntryConfig implements IShopEntry
{
    private EntryType  type;
    private BukkitItem item; // EntryType.ITEM
    private String     displayName;
    private Double     buyPrice;
    private Double     sellPrice;

    @Override
    public EntryType getEntryType()
    {
        if (this.type == null)
        {
            return EntryType.ITEM;
        }
        return this.type;
    }

    @Override
    public String getDisplayName()
    {
        return this.displayName;
    }

    @Override
    public int getAmount()
    {
        return this.item.getAmount();
    }

    @Override
    public BukkitItem getBukkitItem()
    {
        return this.item;
    }

    @Override
    public boolean canBuy()
    {
        return this.buyPrice != null;
    }

    @Override
    public boolean canSell()
    {
        return this.sellPrice != null;
    }

    @Override
    public Double getBuyPrice()
    {
        return this.buyPrice;
    }

    @Override
    public Double getSellPrice()
    {
        return this.sellPrice;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("type", this.type).append("item", this.item).append("displayName", this.displayName).append("buyPrice", this.buyPrice).append("sellPrice", this.sellPrice).toString();
    }
}
