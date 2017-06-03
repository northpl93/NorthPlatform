package pl.north93.zgame.api.global.component.impl;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;
import org.diorite.cfg.annotations.defaults.CfgStringDefault;

import pl.north93.zgame.api.global.component.ComponentDescription;

class ComponentsConfig
{
    @CfgStringDefault("pl.north93")
    private String                     rootPackage;
    @CfgDelegateDefault("{ArrayList}")
    private List<String>               include;
    @CfgDelegateDefault("{ArrayList}")
    private List<ComponentDescription> components;

    public String getRootPackage()
    {
        return this.rootPackage;
    }

    public List<String> getInclude()
    {
        return this.include;
    }

    public List<ComponentDescription> getComponents()
    {
        return this.components;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("rootPackage", this.rootPackage).append("include", this.include).append("components", this.components).toString();
    }
}
