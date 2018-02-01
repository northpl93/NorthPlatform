package pl.north93.zgame.api.bukkit.utils.xml.itemstack;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

@XmlRootElement(name = "potionMeta")
@XmlAccessorType(XmlAccessType.NONE)
public class XmlPotionMeta extends XmlItemMeta
{
    @XmlElement
    private XmlPotionData potionData;
    // TODO: add custom effects;
    
    @Override
    public void apply(ItemMeta itemMeta)
    {
        super.apply(itemMeta);
        
        PotionMeta potionMeta = (PotionMeta) itemMeta;
        
        if ( potionData != null )
        {
            potionMeta.setBasePotionData(potionData.toPotionData());
        }
    }
    
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("potionData", potionData).toString();
    }
}
