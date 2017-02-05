package pl.north93.zgame.skyplayerexp.bungee;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginManager;
import pl.north93.zgame.api.bungee.BungeeApiCore;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.skyplayerexp.bungee.tablist.TablistManager;
import pl.north93.zgame.skyplayerexp.bungee.tablistarieals.AriealsTablist;

public class ExperienceBungee extends Component
{
    private static final int UPDATE_TIME = 20 * 60 * 5;
    private BungeeApiCore  apiCore;
    private TablistManager tablistManager;

    @Override
    protected void enableComponent()
    {
        final PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();

        this.tablistManager = new TablistManager();
        pluginManager.registerListener(this.apiCore.getBungeePlugin(), this.tablistManager);

        final AriealsTablist ariealsTablist = new AriealsTablist(this.tablistManager);
        pluginManager.registerListener(this.apiCore.getBungeePlugin(), ariealsTablist);
        ariealsTablist.setup();
        ariealsTablist.update();

        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            ariealsTablist.update();
            this.tablistManager.updateAll();
        }, UPDATE_TIME);
    }

    @Override
    protected void disableComponent()
    {

    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
