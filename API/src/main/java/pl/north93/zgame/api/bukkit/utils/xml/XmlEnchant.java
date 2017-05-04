package pl.north93.zgame.api.bukkit.utils.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "enchant")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlEnchant
{
    @XmlAttribute(required = true)
    private int id;
    @XmlAttribute
    private int level;
    
    public XmlEnchant()
    {
    }
    
    public XmlEnchant(int id, int level)
    {
        this.id = id;
        this.level = level;
    }
    
    public int getLevel()
    {
        return level;
    }
    
    public void setLevel(int level)
    {
        this.level = level;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
}