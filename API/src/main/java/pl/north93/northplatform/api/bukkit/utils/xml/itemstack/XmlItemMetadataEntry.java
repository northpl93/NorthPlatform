package pl.north93.northplatform.api.bukkit.utils.xml.itemstack;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class XmlItemMetadataEntry
{
    @XmlAttribute
    private String key;
    
    @XmlAttribute
    private String value;
    
    public String getKey()
    {
        return key;
    }
    
    public String getValue()
    {
        return value;
    }
    
    @Override
    public String toString()
    {
        return new ToStringBuilder(this).append("key", key).append("value", value).build();
    }
}
