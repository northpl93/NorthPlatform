package pl.north93.zgame.api.bukkit.utils.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Preconditions;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@XmlRootElement(name = "enchant")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlEnchant
{
    @XmlAttribute(required = true)
    private String enchantment;
    @XmlAttribute
    private int    level = 1;
    
    public XmlEnchant()
    {
    }

    public XmlEnchant(final Enchantment enchantment, final int level)
    {
        this.setEnchantment(enchantment);
        this.level = level;
    }

    public XmlEnchant(final Enchantment enchantment)
    {
        this.setEnchantment(enchantment);
    }

    public Enchantment getEnchantment()
    {
        return Enchantment.getByName(this.enchantment);
    }

    public void setEnchantment(final Enchantment enchantment)
    {
        Preconditions.checkNotNull(enchantment);
        this.enchantment = enchantment.getName();
    }

    public int getLevel()
    {
        return this.level;
    }
    
    public void setLevel(int level)
    {
        this.level = level;
    }

    public void apply(final ItemStack itemStack)
    {
        if ( this.getEnchantment() == null )
        {
            log.warn("Couldn't find enchantment by name: '{}'", enchantment);
        }
        
        itemStack.addUnsafeEnchantment(this.getEnchantment(), this.level);
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }

        final XmlEnchant that = (XmlEnchant) o;

        return this.level == that.level && this.enchantment.equals(that.enchantment);
    }

    @Override
    public int hashCode()
    {
        int result = this.enchantment.hashCode();
        result = 31 * result + this.level;
        return result;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("enchantment", this.enchantment).append("level", this.level).toString();
    }
}