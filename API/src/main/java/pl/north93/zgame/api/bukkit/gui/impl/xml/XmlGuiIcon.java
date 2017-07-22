package pl.north93.zgame.api.bukkit.gui.impl.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import org.bukkit.Material;

import pl.north93.zgame.api.bukkit.gui.GuiIcon;
import pl.north93.zgame.api.bukkit.gui.impl.RenderContext;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.TranslatableString;

@XmlRootElement(name = "icon")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlGuiIcon
{
    @XmlAttribute
    private int id;
    @XmlAttribute
    private int data;
    @XmlAttribute
    private int count = 1;
    
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String lore;
    
    @XmlAttribute
    private boolean glowing;
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public int getData()
    {
        return data;
    }
    
    public void setData(int data)
    {
        this.data = data;
    }
    
    public int getCount()
    {
        return count;
    }
    
    public void setCount(int count)
    {
        this.count = count;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getLore()
    {
        return lore;
    }
    
    public void setLore(String lore)
    {
        this.lore = lore;
    }
    
    public boolean isGlowing()
    {
        return glowing;
    }
    
    public void setGlowing(boolean glowing)
    {
        this.glowing = glowing;
    }
    
    public GuiIcon toGuiIcon(RenderContext renderContext, List<XmlVariable> variables)
    {
        final MessagesBox messages = renderContext.getMessagesBox();
        return GuiIcon.builder().type(Material.getMaterial(id)).data(data).count(count)
                .name(TranslatableString.of(messages, name)).lore(TranslatableString.of(messages, lore))
                      .glowing(glowing).variables(variables).build();
    }
}