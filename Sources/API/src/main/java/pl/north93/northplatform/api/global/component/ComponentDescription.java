package pl.north93.northplatform.api.global.component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

@ToString
@XmlRootElement(name = "component")
@XmlAccessorType(XmlAccessType.FIELD)
public class ComponentDescription
{
    @XmlElement
    private String mainClass;
    @XmlElementWrapper(name = "packages")
    @XmlElement(name = "package")
    private List<String> packages = new ArrayList<>();
    @XmlElement
    private String name;
    @XmlElement
    private boolean enabled = true;
    @XmlElement
    private String description;
    @XmlElementWrapper(name = "dependencies")
    @XmlElement(name = "dependency")
    private List<String> dependencies = new ArrayList<>();
    @XmlElementWrapper(name = "hosts")
    @XmlElement(name = "host")
    private String[] hosts;

    public String getMainClass()
    {
        return this.mainClass;
    }

    public List<String> getPackages()
    {
        return this.packages;
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

    public String[] getHosts()
    {
        return this.hosts;
    }
}
