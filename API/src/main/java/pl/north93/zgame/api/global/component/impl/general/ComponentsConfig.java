package pl.north93.zgame.api.global.component.impl.general;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;

import pl.north93.zgame.api.global.component.ComponentDescription;

class ComponentsConfig
{
    @CfgDelegateDefault("{ArrayList}")
    private List<String>               include;
    @CfgDelegateDefault("{HashSet}")
    private Set<String>                excludedPackages;
    @CfgDelegateDefault("{ArrayList}")
    private List<ComponentDescription> components;

    public List<String> getInclude()
    {
        return this.include;
    }

    public Set<String> getExcludedPackages()
    {
        return this.excludedPackages;
    }

    public List<ComponentDescription> getComponents()
    {
        return this.components;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("include", this.include).append("excludedPackages", this.excludedPackages).append("components", this.components).toString();
    }
}
