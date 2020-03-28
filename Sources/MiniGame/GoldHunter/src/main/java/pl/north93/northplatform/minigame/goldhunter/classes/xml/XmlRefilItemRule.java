package pl.north93.northplatform.minigame.goldhunter.classes.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import pl.north93.northplatform.api.bukkit.utils.xml.itemstack.XmlItemStack;

@XmlAccessorType(XmlAccessType.FIELD)
public class XmlRefilItemRule extends XmlItemStack
{

    @XmlElement(name = "default_slot")
    private int defaultSlot;
    
    @XmlElement(name = "max_count")
    private int maxCount;
    
    @XmlAttribute(name = "refil_rate")
    private int refilRate;
    
    public int getDefaultSlot()
    {
        return defaultSlot;
    }
    
    public int getMaxCount()
    {
        return maxCount;
    }
    
    public int getRefilRate()
    {
        return refilRate;
    }
}
