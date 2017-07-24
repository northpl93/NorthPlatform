package pl.north93.zgame.api.bukkit.gui.impl.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "hotbar")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlHotbarLayout
{
    @XmlElement(name = "entry")
    private List<XmlHotbarEntry> entries = new ArrayList<>();
    
    public List<XmlHotbarEntry> getEntries()
    {
        return entries;
    }
    
    public void setEntries(List<XmlHotbarEntry> entries)
    {
        this.entries = entries;
    }
}
