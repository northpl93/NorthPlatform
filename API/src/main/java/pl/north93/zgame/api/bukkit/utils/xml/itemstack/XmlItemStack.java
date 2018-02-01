package pl.north93.zgame.api.bukkit.utils.xml.itemstack;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.diorite.utils.math.DioriteMathUtils;

import pl.north93.zgame.api.bukkit.utils.itemstack.ItemStackBuilder;
import pl.north93.zgame.api.bukkit.utils.xml.XmlEnchant;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({XmlItemMeta.class, XmlSkullMeta.class, XmlPotionMeta.class})
public class XmlItemStack
{
    @XmlAttribute(required = true)
    private String id = Material.STONE.name();
    @XmlAttribute
    private int    data = 0;
    @XmlAttribute
    private int    count = 1;

    @XmlAnyElement(lax = true)
    private XmlItemMeta itemMeta;

    @XmlAttribute
    private String name;
    @XmlElement(name = "loreLine")
    private List<String> loreLines = new ArrayList<>(0);
    @XmlAttribute(name = "lore")
    private String lore;
    
    @XmlElementWrapper(name = "enchants")
    @XmlElement(name = "enchant")
    private List<XmlEnchant> enchants = new ArrayList<>(0);
    
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

    public XmlItemMeta getItemMeta()
    {
        return this.itemMeta;
    }

    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }

    public List<String> getLoreLines()
    {
        return this.loreLines;
    }

    public void setLoreLines(final List<String> loreLines)
    {
        this.loreLines = loreLines;
    }

    public String getLore()
    {
        return this.lore;
    }

    public void setLore(final String lore)
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

        return material;
    }

    public ItemStack createItemStack()
    {
        final Material material = this.toMaterial();
        if ( material == null )
        {
            // Print error stack trace without throw an exception
            new IllegalArgumentException("Cannot recognize item by id: '" + this.id).printStackTrace();
            return null;
        }
        
        final ItemStackBuilder builder = new ItemStackBuilder().material(material).data(data).amount(count).name(name);

        if (! this.loreLines.isEmpty())
        {
            builder.lore(this.loreLines);
        }
        else if (this.lore != null)
        {
            builder.lore(this.lore);
        }

        this.enchants.forEach(builder::enchant);

        final ItemStack build = builder.build();
        if (this.itemMeta != null)
        {
            final ItemMeta itemMeta = build.getItemMeta();
            this.itemMeta.apply(itemMeta);
            build.setItemMeta(itemMeta);
        }

        return build;
    }
}
