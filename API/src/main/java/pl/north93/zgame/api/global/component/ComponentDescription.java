package pl.north93.zgame.api.global.component;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.defaults.CfgBooleanDefault;

public class ComponentDescription
{
    private String       mainClass;
    @CfgBooleanDefault(true)
    private boolean      autoInstantiate;
    private String       name;
    @CfgBooleanDefault(true)
    private boolean      enabled;
    private String       description;
    private List<String> dependencies;

    public String getMainClass()
    {
        return this.mainClass;
    }

    public boolean isAutoInstantiate()
    {
        return this.autoInstantiate;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public String getDescription()
    {
        return this.description;
    }

    public List<String> getDependencies()
    {
        return this.dependencies;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("mainClass", this.mainClass).append("name", this.name).append("enabled", this.enabled).append("description", this.description).append("dependencies", this.dependencies).toString();
    }
}
