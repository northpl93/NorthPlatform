package pl.north93.northplatform.api.global.network.daemon.config;

import pl.north93.serializer.platform.annotations.NorthField;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

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
    @XmlElementWrapper(name = "components")
    @XmlElement(name = "component")
    @NorthField(type = ArrayList.class)
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
