package pl.north93.zgame.api.bukkit.utils.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import org.diorite.utils.math.DioriteMathUtils;

import pl.north93.zgame.api.bukkit.utils.itemstack.ItemStackBuilder;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlItemStack
{
    @XmlAttribute(required = true)
    private String id;
    @XmlAttribute
    private int    data;
    @XmlAttribute
    private int    count;
    
    @XmlElement
    private String name;
    @XmlElementWrapper(name = "lore")
    @XmlElement(name = "line")
    private List<String> lore = new ArrayList<>();
    
    @XmlElementWrapper(name = "enchants")
    @XmlElement(name = "enchant")
    private List<XmlEnchant> enchants = new ArrayList<>();
    
    public XmlItemStack()
    {
    }
    
    public XmlItemStack(String id)
    {
        this(id, 0, 1);
    }
    
    public XmlItemStack(String id, int data)
    {
        this(id, data, 1);
    }
    
    public XmlItemStack(String id, int data, int count)
    {
        this.id = id;
        this.data = data;
        this.count = count;
    }
    
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
    
    public List<String> getLore()
    {
        return lore;
    }
    
    public void setLore(List<String> lore)
    {
        this.lore = lore;
    }
    
    public List<XmlEnchant> getEnchants()
    {
        return enchants;
    }
    
    public void setEnchants(List<XmlEnchant> enchants)
    {
        this.enchants = enchants;
    }
    
    @SuppressWarnings("deprecation")
    public ItemStack createItemStack()
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

        final ItemStackBuilder builder = new ItemStackBuilder().material(material).data(data).amount(count).name(name).lore(this.lore);
        this.enchants.forEach(builder::enchant);
        return builder.build();
    }
}
