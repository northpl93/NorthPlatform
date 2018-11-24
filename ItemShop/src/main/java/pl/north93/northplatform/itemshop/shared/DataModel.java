package pl.north93.northplatform.itemshop.shared;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DataModel
{
    private List<DataEntry> entries;

    public DataModel(final List<DataEntry> entries)
    {
        this.entries = entries;
    }

    public List<DataEntry> getEntries()
    {
        return this.entries;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("entries", this.entries).toString();
    }
}
