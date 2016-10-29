package pl.north93.zgame.api.bungee;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin
{
    private final BungeeApiCore apiCore = new BungeeApiCore(this);

    @Override
    public void onEnable()
    {
        this.apiCore.startCore();
    }

    @Override
    public void onDisable()
    {
        this.apiCore.stopCore();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("apiCore", this.apiCore).toString();
    }
}
