package pl.arieals.minigame.goldhunter.classes.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.NONE)
public class XmlClassEquipmentInvSlot extends XmlClassEquipmentSlot
{
    @XmlAttribute
    private int slot;
    
    public int getSlot()
    {
        return slot;
    }
}
