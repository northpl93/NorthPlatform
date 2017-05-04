package pl.north93.zgame.api.bukkit.utils.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import pl.north93.zgame.api.bukkit.utils.ItemStackBuilder;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlItemStack
{
    @XmlAttribute(required = true)
    private int id;
    @XmlAttribute
    private int data;
    @XmlAttribute
    private int count;
    
    @XmlElement
    private String name;
    @XmlElementWrapper(name = "lore")
    @XmlElement(name = "line")
    private List<String> lore;
    
    @XmlElementWrapper(name = "ench")
    private List<XmlEnchant> enchants;
    
    public XmlItemStack()
    {
    }
    
    public XmlItemStack(int id)
    {
        this(id, 0, 1);
    }
    
    public XmlItemStack(int id, int data)
    {
        this(id, data, 1);
    }
    
    public XmlItemStack(int id, int data, int count)
    {
        this.id = id;
        this.data = data;
        this.count = count;
    }
    
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
    public ItemStack createItemStack() {
        
        // TODO: enchants
        return new ItemStackBuilder().material(Material.getMaterial(id)).data(data).amount(count).name(name).lore(lore).build();
    }
}
