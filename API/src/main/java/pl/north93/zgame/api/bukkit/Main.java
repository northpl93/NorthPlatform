package pl.north93.zgame.api.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Main extends JavaPlugin
{
    private final BukkitApiCore bukkitApiCore = new BukkitApiCore(this);

    @Override
    public void onEnable()
    {
        this.bukkitApiCore.startCore();
    }

    @Override
    public void onDisable()
    {
        this.bukkitApiCore.stopCore();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("bukkitApiCore", this.bukkitApiCore).toString();
    }
}
