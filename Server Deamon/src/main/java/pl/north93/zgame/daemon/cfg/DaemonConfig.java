package pl.north93.zgame.daemon.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "daemon")
@XmlAccessorType(XmlAccessType.FIELD)
public class DaemonConfig
{
    @XmlElement
    public String externalHost = "127.0.0.1";
    @XmlElement
    public String listenHost = "0.0.0.0";
    @XmlElement
    public int portRangeStart = 25570;
    @XmlElement
    public int maxMemory = 1024;

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("externalHost", this.externalHost).append("listenHost", this.listenHost).append("portRangeStart", this.portRangeStart).append("maxMemory", this.maxMemory).toString();
    }
}
