package pl.north93.zgame.skyblock.shop.cfg;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.skyblock.shop.api.ICategory;

public class CategoryConfig implements ICategory
{
    private String     internalName;
    private String     fileName;
    private String     displayName;
    private BukkitItem representingItem;
    private String     permission;

    @Override
    public String getInternalName()
    {
        return this.internalName;
    }

    @Override
    public String getFileName()
    {
        return this.fileName;
    }

    @Override
    public String getDisplayName()
    {
        return this.displayName;
    }

    @Override
    public BukkitItem getRepresentingItem()
    {
        return this.representingItem;
    }

    @Override
    public String getPermission()
    {
        return this.permission;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("internalName", this.internalName).append("fileName", this.fileName).append("displayName", this.displayName).append("representingItem", this.representingItem).append("permission", this.permission).toString();
    }
}
