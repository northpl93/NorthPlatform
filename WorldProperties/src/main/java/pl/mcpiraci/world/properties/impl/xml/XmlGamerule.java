package pl.mcpiraci.world.properties.impl.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.NONE)
public class XmlGamerule
{
    @XmlAttribute
    private String name;
    
    @XmlAttribute
    private String value;
    
    public XmlGamerule()
    {
    }
    
    public XmlGamerule(String name, String value)
    {
        this.name = name;
        this.value = value;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
}
