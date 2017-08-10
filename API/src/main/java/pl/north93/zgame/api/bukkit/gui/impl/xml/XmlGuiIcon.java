package pl.north93.zgame.api.bukkit.gui.impl.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.diorite.utils.math.DioriteMathUtils;

import com.google.common.base.Preconditions;

import pl.north93.zgame.api.bukkit.gui.ConfigGuiIcon;
import pl.north93.zgame.api.bukkit.gui.IGuiIcon;
import pl.north93.zgame.api.bukkit.gui.impl.RenderContext;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.TranslatableString;

@XmlRootElement(name = "icon")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlGuiIcon
{
    @XmlAttribute
    private String id;
    @XmlAttribute
    private int    data;
    @XmlAttribute
    private int    count = 1;
    
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String lore;
    
    @XmlAttribute
    private boolean glowing;
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
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

    @SuppressWarnings("deprecation")
    private Material toMaterial()
    {
        final Integer numberId = DioriteMathUtils.asInt(this.id);
        final Material material;
        
        if ( numberId != null )
        {
            material = Material.getMaterial(numberId);
        }
        else if ( this.id.startsWith("minecraft:") )
        {
            material = Bukkit.getUnsafe().getMaterialFromInternalName(this.id);
        }
        else
        {
            material = Material.getMaterial(this.id);
        }
        
        return material != null ? material : Material.BEDROCK;
    }
    
    public IGuiIcon toGuiIcon(RenderContext renderContext, List<XmlVariable> variables)
    {
        final MessagesBox messages = renderContext.getMessagesBox();
        return ConfigGuiIcon.builder().type(this.toMaterial()).data(data).count(count)
                            .name(TranslatableString.of(messages, name)).lore(TranslatableString.of(messages, lore))
                            .glowing(glowing).variables(variables).build();
    }
}
