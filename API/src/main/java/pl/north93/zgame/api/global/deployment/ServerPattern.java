package pl.north93.zgame.api.global.deployment;

import java.util.List;

import org.diorite.cfg.annotations.CfgComment;

import pl.north93.zgame.api.global.redis.messaging.annotations.MsgPackCustomTemplate;
import pl.north93.zgame.api.global.redis.messaging.templates.ArrayListTemplate;

/**
 * Reprezentuje wzór według którego tworzone będą instancje serwerów.
 */
public class ServerPattern
{
    @CfgComment("Nazwa tego wzoru")
    private String       patternName;
    @CfgComment("Nazwa silnika który zostanie użyty")
    private String       engineName;
    @CfgComment("Maksymalna ilość pamięci (w MB)")
    private Integer      maxMemory;
    @CfgComment("Startowa ilość pamięci (w MB)")
    private Integer      startMemory;
    @MsgPackCustomTemplate(ArrayListTemplate.class)
    @CfgComment("Lista komponentów z których będzie budowana przestrzeń robocza serwera")
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
