package pl.north93.zgame.api.bukkit.map.loader.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import pl.north93.zgame.api.bukkit.map.IMapRenderer;
import pl.north93.zgame.api.bukkit.map.renderer.SimpleTranslatedRenderer;

@XmlRootElement(name = "static")
@XmlAccessorType(XmlAccessType.FIELD)
public class StaticMapConfig extends MapConfig
{
    @XmlElement(required = true)
    private XmlTranslatableImage image;

    @Override
    public IMapRenderer createRenderer()
    {
        return new SimpleTranslatedRenderer(this.image);
    }
}
