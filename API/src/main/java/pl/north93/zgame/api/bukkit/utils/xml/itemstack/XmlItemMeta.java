package pl.north93.zgame.api.bukkit.utils.xml.itemstack;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "itemMeta")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlItemMeta
{
    @XmlElementWrapper(name = "itemFlags")
    @XmlElement(name = "itemFlag")
    private List<ItemFlag> flags = new ArrayList<>(0);

    public void apply(final ItemMeta itemMeta)
    {
        for (final ItemFlag flag : this.flags)
        {
            itemMeta.addItemFlags(flag);
        }

        // tutaj dopisywac reszte opcji
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("flags", this.flags).toString();
    }
}
