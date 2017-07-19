package pl.north93.zgame.api.bukkit.gui.impl.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.bukkit.gui.GuiContent;
import pl.north93.zgame.api.global.messages.TranslatableString;

@XmlRootElement(name = "gui")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({XmlContainerElement.class, XmlButtonElement.class})
public class XmlGuiLayout
{
    @XmlAttribute
    private int height;
    @XmlAttribute
    private String title;
    
    @XmlAnyElement(lax = true)
    private List<XmlGuiElement> content = new ArrayList<>();
    
    public int getHeight()
    {
        return height;
    }
    
    public void setHeight(int height)
    {
        this.height = height;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public List<XmlGuiElement> getContent()
    {
        return content;
    }
    
    public void setContent(List<XmlGuiElement> content)
    {
        this.content = content;
    }
    
    public GuiContent createGuiContent(Gui gui)
    {
        GuiContent content = new GuiContent(gui, height);
        content.setTitle(TranslatableString.of(title));
        
        for ( XmlGuiElement element : this.content )
        {
            content.addChild(element.toGuiElement());
        }
        
        return content;
    }
}
