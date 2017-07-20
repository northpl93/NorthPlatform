package pl.north93.zgame.api.bukkit.gui.impl.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import java.util.ArrayList;
import java.util.List;

import pl.north93.zgame.api.bukkit.gui.GuiElement;
import pl.north93.zgame.api.global.messages.MessagesBox;

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
    
    public GuiElement toGuiElement(MessagesBox messagesBox)
    {
        GuiElement element = toGuiElement0(messagesBox);
        
        String[] split = position.split(",");
        element.setPosition(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        
        for ( String clickHandler : onClick )
        {
            element.getClickHandlers().add(clickHandler);
        }
        
        for ( XmlGuiElement child : content )
        {
            element.addChild(child.toGuiElement(messagesBox));
        }
        
        return element;
    }
    
    protected abstract GuiElement toGuiElement0(MessagesBox messagesBox);
}
