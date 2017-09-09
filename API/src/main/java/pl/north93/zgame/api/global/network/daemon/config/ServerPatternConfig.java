package pl.north93.zgame.api.global.network.daemon.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.templates.ArrayListTemplate;

/**
 * Reprezentuje wzór według którego tworzone będą instancje serwerów.
 */
@XmlRootElement(name = "serverPattern")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerPatternConfig
{
    @XmlElement
    private String       patternName;
    @XmlElement
    private String       engineName;
    @XmlElement
    private Integer      maxMemory;
    @XmlElement
    private Integer      startMemory;
    @MsgPackCustomTemplate(ArrayListTemplate.class)
    @XmlElementWrapper(name = "components")
    @XmlElement(name = "component")
    private List<String> components;

    public String getPatternName()
    {
        return this.patternName;
    }

    public void setPatternName(final String patternName)
    {
        this.patternName = patternName;
    }

    public String getEngineName()
    {
        return this.engineName;
    }

    public void setEngineName(final String engineName)
    {
        this.engineName = engineName;
    }

    public Integer getMaxMemory()
    {
        return this.maxMemory;
    }

    public void setMaxMemory(final Integer maxMemory)
    {
        this.maxMemory = maxMemory;
    }

    public Integer getStartMemory()
    {
        return this.startMemory;
    }

    public void setStartMemory(final Integer startMemory)
    {
        this.startMemory = startMemory;
    }

    public List<String> getComponents()
    {
        return this.components;
    }

    public void setComponents(final List<String> components)
    {
        this.components = components;
    }
}
