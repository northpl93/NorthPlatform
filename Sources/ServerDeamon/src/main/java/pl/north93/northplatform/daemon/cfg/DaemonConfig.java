package pl.north93.northplatform.daemon.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.ToString;

@ToString
@XmlRootElement(name = "daemon")
@XmlAccessorType(XmlAccessType.FIELD)
public class DaemonConfig
{
    @XmlElement
    public String externalHost = "127.0.0.1";
    @XmlElement
    public String listenHost = "0.0.0.0";
    @XmlElement
    public int portRangeStart = 25600;
    @XmlElement
    public int maxMemory = 1024;
    @XmlElement
    public String debuggerHost = "*";
    @XmlElement
    public int debuggerRangeStart = 26600;
}
