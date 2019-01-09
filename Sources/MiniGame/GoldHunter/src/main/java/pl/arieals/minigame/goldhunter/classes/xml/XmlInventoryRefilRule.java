package pl.arieals.minigame.goldhunter.classes.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import pl.north93.zgame.api.bukkit.utils.xml.itemstack.XmlItemStack;

@XmlAccessorType(XmlAccessType.FIELD)
public class XmlInventoryRefilRule extends XmlItemStack
{
    @XmlAttribute
    private int maxCount;
    
    @XmlAttribute
    private int period;
    
    public int getMaxCount()
    {
        return maxCount;
    }
    
    public int getPeriod()
    {
        return period;
    }
}
