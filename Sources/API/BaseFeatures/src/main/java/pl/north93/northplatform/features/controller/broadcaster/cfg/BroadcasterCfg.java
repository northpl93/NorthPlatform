package pl.north93.northplatform.features.controller.broadcaster.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "broadcaster")
public class BroadcasterCfg
{
    @XmlElement(name = "entry")
    private List<BroadcasterEntryCfg> entries;

    public List<BroadcasterEntryCfg> getEntries()
    {
        return this.entries;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("entries", this.entries).toString();
    }
}
