package pl.north93.northplatform.itemshop.shared;

import javax.annotation.Nullable;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DataEntry
{
    private String              type;
    private String              oneTime;
    private Map<String, String> data;

    public DataEntry(final String type, final String oneTime, final Map<String, String> data)
    {
        this.type = type;
        this.oneTime = oneTime;
        this.data = data;
    }

    public String getType()
    {
        return this.type;
    }

    @Nullable
    public String getOneTime()
    {
        return this.oneTime;
    }

    public Map<String, String> getData()
    {
        return this.data;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("type", this.type).append("data", this.data).toString();
    }
}
