package pl.north93.zgame.api.bukkit.gui.impl.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import pl.north93.zgame.api.bukkit.gui.GuiElement;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class XmlGuiElement
{
    @XmlAttribute(name = "pos")
    private String position = "0,0";
    
    @XmlElement
    private List<String> onClick = new ArrayList<>();
    
    @XmlElementWrapper(name = "content")
    @XmlAnyElement(lax = true)
    private List<XmlGuiElement> content = new ArrayList<>();
    
    public GuiElement toGuiElement()
    {
        GuiElement element = toGuiElement0();
        
        String[] split = position.split(",");
        element.setPosition(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        
        for ( String clickHandler : onClick )
        {
            element.getClickHandlers().add(clickHandler);
        }
        
        for ( XmlGuiElement child : content )
        {
            element.addChild(child.toGuiElement());
        }
        
        return element;
    }
    
    protected abstract GuiElement toGuiElement0();
}
