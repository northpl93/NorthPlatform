package pl.north93.zgame.skyplayerexp.server;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyplayerexp.server.compass.CompassManager;
import pl.north93.zgame.skyplayerexp.server.compass.ICompassManager;
import pl.north93.zgame.skyplayerexp.server.gui.IServerGuiManager;
import pl.north93.zgame.skyplayerexp.server.gui.ServerGuiManager;

public class ExperienceServer extends Component
{
    @Inject
    private BukkitApiCore    bukkitApiCore;
    @Inject
    private SkyBlockServer   server;
    private ServerGuiManager serverGuiManager;
    private CompassManager   compassManager;

    @Override
    protected void enableComponent()
    {
        this.serverGuiManager = new ServerGuiManager();
        this.compassManager = new CompassManager();
    }

    @Override
    protected void disableComponent()
    {
    }

    public SkyBlockServer getSkyBlock()
    {
        return this.server;
    }

    public IServerGuiManager getServerGuiManager()
    {
        return this.serverGuiManager;
    }

    public ICompassManager getCompassManager()
    {
        return this.compassManager;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
