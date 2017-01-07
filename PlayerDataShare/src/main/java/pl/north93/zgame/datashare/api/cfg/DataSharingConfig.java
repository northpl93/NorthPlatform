package pl.north93.zgame.datashare.api.cfg;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DataSharingConfig
{
    private List<DataSharingGroupConfig> sharingGroups;

    public List<DataSharingGroupConfig> getSharingGroups()
    {
        return this.sharingGroups;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("sharingGroups", this.sharingGroups).toString();
    }
}
