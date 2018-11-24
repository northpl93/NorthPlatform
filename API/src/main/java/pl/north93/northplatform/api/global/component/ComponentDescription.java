package pl.north93.northplatform.api.global.component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.Platform;

@XmlRootElement(name = "component")
@XmlAccessorType(XmlAccessType.FIELD)
public class ComponentDescription
{
    @XmlElement
    private String       mainClass;
    @XmlElementWrapper(name = "packages")
    @XmlElement(name = "package")
    private List<String> packages = new ArrayList<>();
    @XmlElement
    private String       name;
    @XmlElement
    private boolean      enabled = true;
    @XmlElement
    private String       description;
    @XmlElementWrapper(name = "dependencies")
    @XmlElement(name = "dependency")
    private List<String> dependencies = new ArrayList<>();
    @XmlElementWrapper(name = "platforms")
    @XmlElement(name = "platform")
    private Platform[]   platforms = Platform.values(); // deault all platforms

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

    public Platform[] getPlatforms()
    {
        return this.platforms;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("mainClass", this.mainClass).append("packages", this.packages).append("name", this.name).append("enabled", this.enabled).append("description", this.description).append("dependencies", this.dependencies).append("platforms", this.platforms).toString();
    }

    private static Platform[] defaultPlatforms()
    {
        return new Platform[] { Platform.BUKKIT, Platform.BUNGEE, Platform.STANDALONE };
    }
}
