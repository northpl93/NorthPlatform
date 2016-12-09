package pl.north93.zgame.api.global.component;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.defaults.CfgBooleanDefault;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;

public class ComponentDescription
{
    private String       mainClass;
    @CfgBooleanDefault(true)
    private boolean      autoInstantiate;
    private String       name;
    @CfgBooleanDefault(true)
    private boolean      enabled;
    private String       description;
    @CfgDelegateDefault("{ArrayList}")
    private List<String> dependencies;
    @CfgDelegateDefault("{ArrayList}")
    private List<String> extensionPoints;
    //@CfgDelegateDefault("{ArrayList}")
    //private List<String> implementations;

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

    public List<String> getExtensionPoints()
    {
        return this.extensionPoints;
    }

    //public List<String> getImplementations()
    //{
    //    return this.implementations;
    //}

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("mainClass", this.mainClass).append("autoInstantiate", this.autoInstantiate).append("name", this.name).append("enabled", this.enabled).append("description", this.description).append("dependencies", this.dependencies).append("extensionPoints", this.extensionPoints).toString();
    }
}
