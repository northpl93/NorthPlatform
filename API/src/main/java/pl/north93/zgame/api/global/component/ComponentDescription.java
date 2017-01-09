package pl.north93.zgame.api.global.component;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.defaults.CfgBooleanDefault;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;

import pl.north93.zgame.api.global.Platform;

public class ComponentDescription
{
    private String       mainClass;
    private String       packageToScan;
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
    @CfgDelegateDefault("defaultPlatforms")
    private Platform[]   platforms;

    public String getMainClass()
    {
        return this.mainClass;
    }

    public String getPackageToScan()
    {
        return this.packageToScan;
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

    public Platform[] getPlatforms()
    {
        return this.platforms;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("mainClass", this.mainClass).append("packageToScan", this.packageToScan).append("autoInstantiate", this.autoInstantiate).append("name", this.name).append("enabled", this.enabled).append("description", this.description).append("dependencies", this.dependencies).append("extensionPoints", this.extensionPoints).append("platforms", this.platforms).toString();
    }

    private static Platform[] defaultPlatforms()
    {
        return new Platform[] { Platform.BUKKIT, Platform.BUNGEE, Platform.STANDALONE };
    }
}
