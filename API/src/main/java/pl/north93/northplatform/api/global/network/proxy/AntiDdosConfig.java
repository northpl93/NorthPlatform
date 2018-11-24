package pl.north93.northplatform.api.global.network.proxy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@XmlRootElement(name = "antiddos")
@XmlAccessorType(XmlAccessType.NONE)
public class AntiDdosConfig
{
    @XmlElement
    private AntiDdosMode mode; // okresla tryb pracy systemu AntyDDoS w sieci

    @XmlElement
    private int connectionsThreshold;

    @XmlElement
    private int duration;

    @XmlElement(name = "execute")
    @XmlElementWrapper(name = "onEnable")
    private List<AntiDdosExecute> onEnable;

    @XmlElement(name = "execute")
    @XmlElementWrapper(name = "onDisable")
    private List<AntiDdosExecute> onDisable;
}
