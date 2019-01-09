package pl.north93.northplatform.api.bukkit.utils.xml.itemstack;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

@XmlAccessorType(XmlAccessType.NONE)
public class XmlPotionData
{
    @XmlAttribute
    private PotionType type = PotionType.UNCRAFTABLE;
    @XmlAttribute
    private boolean extended = false;
    @XmlAttribute
    private boolean upgraded = false;
    
    public XmlPotionData()
    {
    
    }
    
    public XmlPotionData(PotionType type)
    {
        this(type, false, false);
    }
    
    public XmlPotionData(PotionType type, boolean extended, boolean upgraded)
    {
        this.type = type;
        this.extended = extended;
        this.upgraded = upgraded;
    }
    
    public PotionData toPotionData()
    {
        return new PotionData(type, extended, upgraded);
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (extended ? 1231 : 1237);
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + (upgraded ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null || getClass() != obj.getClass() )
        {
            return false;
        }

        XmlPotionData other = (XmlPotionData) obj;
        if ( extended != other.extended || type != other.type || upgraded != other.upgraded )
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("type", type).append("extended", extended).append("upgraded", upgraded).toString();
    }
}
