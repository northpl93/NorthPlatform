package pl.north93.zgame.api.global.component.impl;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.ComponentDescription;

public class ComponentsConfig
{
    private List<ComponentDescription> components;

    public List<ComponentDescription> getComponents()
    {
        return this.components;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("components", this.components).toString();
    }
}
