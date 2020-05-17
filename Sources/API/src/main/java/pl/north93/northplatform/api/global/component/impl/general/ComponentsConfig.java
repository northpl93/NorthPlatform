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

import lombok.ToString;
import pl.north93.northplatform.api.global.component.ComponentDescription;

@ToString
@XmlRootElement(name = "bundle")
@XmlAccessorType(XmlAccessType.FIELD)
class ComponentsConfig
{
    @XmlElementWrapper(name = "includes")
    @XmlElement(name = "include")
    private List<String> include = new ArrayList<>();
    @XmlElementWrapper(name = "excludedPackages")
    @XmlElement(name = "excludedPackage")
    private Set<String> excludedPackages = new HashSet<>();
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
}
