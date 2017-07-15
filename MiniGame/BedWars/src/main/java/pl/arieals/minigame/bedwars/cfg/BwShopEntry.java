package pl.arieals.minigame.bedwars.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.utils.xml.XmlItemStack;

@XmlRootElement(name = "shopEntry")
@XmlAccessorType(XmlAccessType.FIELD)
public class BwShopEntry
{
    @XmlElement(required = true)
    private String             internalName;
    @XmlElement
    private String             specialHandler;
    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    private List<XmlItemStack> items;
    @XmlElement(required = true)
    private XmlItemStack       price;
    @XmlElement(required = true)
    private boolean            isPersistent;

    public String getInternalName()
    {
        return this.internalName;
    }

    public String getSpecialHandler()
    {
        return this.specialHandler;
    }

    public List<XmlItemStack> getItems()
    {
        return this.items;
    }

    public XmlItemStack getPrice()
    {
        return this.price;
    }

    public boolean isPersistent()
    {
        return this.isPersistent;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("internalName", this.internalName).append("specialHandler", this.specialHandler).append("items", this.items).append("price", this.price).append("isPersistent", this.isPersistent).toString();
    }
}
