package pl.north93.northplatform.api.bungee;

import static pl.north93.northplatform.api.bungee.BungeeHostConnector.BUNGEE_HOST;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.plugin.Plugin;
import pl.north93.northplatform.api.global.ApiCore;

public class Main extends Plugin
{
    private final ApiCore apiCore = new ApiCore(BUNGEE_HOST, new BungeeHostConnector(this));

    @Override
    public void onEnable()
    {
        this.apiCore.startPlatform();
    }

    @Override
    public void onDisable()
    {
        this.apiCore.stopPlatform();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("apiCore", this.apiCore).toString();
    }
}
