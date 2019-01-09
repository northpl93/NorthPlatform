package pl.north93.northplatform.api.global.component.impl.general;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.component.ComponentDescription;

@XmlRootElement(name = "bundle")
@XmlAccessorType(XmlAccessType.FIELD)
class ComponentsConfig
{
    @XmlElementWrapper(name = "includes")
    @XmlElement(name = "include")
    private List<String>               include = new ArrayList<>();
    @XmlElementWrapper(name = "excludedPackages")
    @XmlElement(name = "excludedPackage")
    private Set<String>                excludedPackages = new HashSet<>();
    @XmlElementWrapper(name = "components")
    @XmlElement(name = "component")
    private List<ComponentDescription> components = new ArrayList<>();

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
