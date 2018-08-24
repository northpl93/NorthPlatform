package pl.arieals.api.minigame.shared.api.cfg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@XmlRootElement(name = "gameMap")
@XmlAccessorType(XmlAccessType.FIELD)
public class GameMapConfig
{
    @XmlElement(required = true)
    private String              displayName;
    @XmlElement
    private boolean             enabled = true;
    @XmlElement
    private Map<String, String> properties = new HashMap<>();
    @XmlElement
    private Map<String, String> gameRules = new HashMap<>();
}
