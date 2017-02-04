package pl.north93.zgame.itemshop.shared;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DataEntry
{
    private DataType            dataType;
    private Map<String, String> properties;

    public DataEntry()
    {
    }

    public DataEntry(final DataType dataType, final Map<String, String> properties)
    {
        this.dataType = dataType;
        this.properties = properties;
    }

    public DataType getDataType()
    {
        return this.dataType;
    }

    public Map<String, String> getProperties()
    {
        return this.properties;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("dataType", this.dataType).append("properties", this.properties).toString();
    }
}
