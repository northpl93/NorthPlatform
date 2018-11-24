package pl.north93.northplatform.api.bukkit.map.loader.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import pl.north93.northplatform.api.bukkit.utils.xml.XmlLocation;
import pl.north93.northplatform.api.bukkit.map.IMapRenderer;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class MapConfig
{
    @XmlElement(required = true)
    private XmlLocation leftCorner;
    @XmlElement(required = true)
    private XmlLocation rightCorner;

    public XmlLocation getLeftCorner()
    {
        return this.leftCorner;
    }

    public XmlLocation getRightCorner()
    {
        return this.rightCorner;
    }

    public abstract IMapRenderer createRenderer();
}
