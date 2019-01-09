package pl.north93.northplatform.api.global.network.proxy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.NONE)
public class AntiDdosExecute
{
    @XmlAttribute
    private Boolean asRoot = true;

    @XmlValue
    private String command;
}
